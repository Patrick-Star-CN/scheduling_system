package team.delete.scheduling_system.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.dto.UserScheduleDto;
import team.delete.scheduling_system.entity.*;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.ChangeShiftRecordMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.List;
import java.util.Objects;

/**
 * @author Devin100086
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class ChangeShiftService {
    @Resource
    private MongoTemplate mongoTemplate;
    final ChangeShiftRecordMapper changeShiftRecordMapper;
    final StringRedisTemplate stringRedisTemplate;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ScheduleService scheduleService;
    final MessageService messageService;

    /**
     * 判断是否有管理员权限
     *
     * @param user    用户对象
     * @param storeId 门店id
     */
    private void validateUserPermission(User user, Integer storeId) {
        if (user.getType() != User.Type.MANAGER || !storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 判断两个用户的类型是否相同
     *
     * @param userId1 用户1的id
     * @param userId2 用户2的id
     */
    private void validateType(Integer userId1, Integer userId2) {
        if (userMapper.selectUserTypeByUserId(userId1) != userMapper.selectUserTypeByUserId(userId2)) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 两人换班
     *
     * @param scheduleDetail1 第一个排班详细列表
     * @param scheduleDetail2 第二个排班详细列表
     * @param userId1         用户一id
     * @param userId2         用户二id
     * @param shiftId1        班次一id
     * @param shiftId2        班次二id
     */
    private void changeTwoPeople(ScheduleDetail scheduleDetail1, ScheduleDetail scheduleDetail2, Integer userId1, Integer userId2, Integer shiftId1, Integer shiftId2, Integer storeId) {
        User.Type type = userMapper.selectUserTypeByUserId(userId1);
        UserScheduleDto userScheduleDto1 = UserScheduleDto.builder()
                .userId(userId1)
                .name(userMapper.selectById(userId1).getName())
                .build();
        UserScheduleDto userScheduleDto2 = UserScheduleDto.builder()
                .userId(userId2)
                .name(userMapper.selectById(userId2).getName())
                .build();
        if (type == User.Type.STORAGE) {
            scheduleDetail1.getStorageList()
                    .removeIf(storage -> storage.getUserId().equals(userId1));
            scheduleDetail2.getStorageList()
                    .removeIf(storage -> storage.getUserId().equals(userId2));
            scheduleDetail1.getStorageList().add(userScheduleDto2);
            scheduleDetail2.getStorageList().add(userScheduleDto1);
        } else if (type == User.Type.CASHIER) {
            scheduleDetail1.getCashierList()
                    .removeIf(storage -> storage.getUserId().equals(userId1));
            scheduleDetail2.getCashierList()
                    .removeIf(storage -> storage.getUserId().equals(userId2));
            scheduleDetail1.getCashierList().add(userScheduleDto2);
            scheduleDetail2.getCashierList().add(userScheduleDto1);
        } else if (type == User.Type.CUSTOMER_SERVICE) {
            scheduleDetail1.getCustomerServiceList()
                    .removeIf(storage -> storage.getUserId().equals(userId1));
            scheduleDetail2.getCustomerServiceList()
                    .removeIf(storage -> storage.getUserId().equals(userId2));
            scheduleDetail1.getCustomerServiceList().add(userScheduleDto2);
            scheduleDetail2.getCustomerServiceList().add(userScheduleDto1);
        }

    }

    /**
     * 替换一个人的当前排班
     *
     * @param scheduleDetail 排班的信息
     * @param userId1        用户一id
     * @param userId2        用户一id
     */
    private void changeOnePeople(ScheduleDetail scheduleDetail, Integer userId1, Integer userId2) {
        User.Type type = userMapper.selectUserTypeByUserId(userId1);
        UserScheduleDto userScheduleDto2 = UserScheduleDto.builder()
                .userId(userId2)
                .name(userMapper.selectById(userId2).getName())
                .build();
        if (type == User.Type.STORAGE) {
            scheduleDetail.getStorageList()
                    .removeIf(storage -> storage.getUserId().equals(userId1));
            scheduleDetail.getStorageList().add(userScheduleDto2);
        } else if (type == User.Type.CASHIER) {
            scheduleDetail.getCashierList()
                    .removeIf(storage -> storage.getUserId().equals(userId1));
            scheduleDetail.getCashierList().add(userScheduleDto2);
        } else if (type == User.Type.CUSTOMER_SERVICE) {
            scheduleDetail.getCustomerServiceList()
                    .removeIf(storage -> storage.getUserId().equals(userId1));
            scheduleDetail.getCustomerServiceList().add(userScheduleDto2);
        }
    }

    /**
     * 获取用户数量
     *
     * @param scheduleDetail 班表信息
     * @param userId         用户id
     * @return 当前日期的用户数量
     */
    private int getCount(ScheduleDetail scheduleDetail, Integer userId) {
        int count = 0;
        for (UserScheduleDto userScheduleDto : scheduleDetail.getStorageList()) {
            if (userScheduleDto.getUserId().equals(userId)) {
                count += 1;
            }
        }
        for (UserScheduleDto userScheduleDto : scheduleDetail.getCashierList()) {
            if (userScheduleDto.getUserId().equals(userId)) {
                count += 1;
            }
        }
        for (UserScheduleDto userScheduleDto : scheduleDetail.getCustomerServiceList()) {
            if (userScheduleDto.getUserId().equals(userId)) {
                count += 1;
            }
        }
        return count;
    }

    /**
     * 判断是否能够换班
     *
     * @param scheduleDetail1 第一个换班信息
     * @param scheduleDetail2 第二个换班信息
     * @param userId1         用户1的ID
     * @param userId2         用户2的ID
     */
    private void judgeShift(ScheduleDetail scheduleDetail1, ScheduleDetail scheduleDetail2, Integer userId1, Integer userId2) {
        //记录当前的用户一要对应换班日期的数量
        int count1 = getCount(scheduleDetail2, userId1);
        //记录当前的用户二要对应换班日期的数量
        int count2 = getCount(scheduleDetail1, userId2);
        if (count1 > 1 && count2 > 1) {
            throw new AppException(ErrorCode.STORE_NOT_EXISTED);
        }
    }

    /**
     * 判断该用户在该天的班次是否已经存在排班信息
     *
     * @param userId  管理员id
     * @param storeId 商店id
     * @param weekId  日期id
     * @param shiftId 班次id
     */
    private void judgeExists(Integer userId, Integer storeId, Integer weekId, Integer shiftId, Integer userId1) {
        Schedule schedule = scheduleService.fetchScheduleByDay(userId, storeId, weekId);
        List<ScheduleDetail> scheduleDetailList = schedule.getScheduleDetails();
        ScheduleDetail scheduleDetail = scheduleDetailList.stream()
                .filter(detail -> Objects.equals(detail.getShiftId(), shiftId))
                .findFirst()
                .orElse(null);
        if (scheduleDetail != null) {
            for (UserScheduleDto userScheduleDto : scheduleDetail.getCashierList()) {
                if (Objects.equals(userScheduleDto.getUserId(), userId1)) {
                    return;
                }
            }
            for (UserScheduleDto userScheduleDto : scheduleDetail.getStorageList()) {
                if (Objects.equals(userScheduleDto.getUserId(), userId1)) {
                    return;
                }
            }
            for (UserScheduleDto userScheduleDto : scheduleDetail.getCustomerServiceList()) {
                if (Objects.equals(userScheduleDto.getUserId(), userId1)) {
                    return;
                }
            }
        }
        throw new AppException(ErrorCode.STORE_NOT_EXISTED);
    }

    private void changeSchedule(Integer weekId, Integer storeId, List<ScheduleDetail> scheduleDetails) {
        Criteria criteria = Criteria.where("store_id").is(storeId.toString()).and("week_id").is(weekId);
        Query query = Query.query(criteria);
        Update update = new Update().set("schedule_detail", scheduleDetails);
        mongoTemplate.updateFirst(query, update, Schedule.class);
    }

    /**
     * 改变换班
     *
     * @param userId   管理员id
     * @param storeId  商店id
     * @param userId1  用户一id
     * @param userId2  用户二id
     * @param shiftId1 班次一id
     * @param shiftId2 班次二id
     * @param weekId1  日期一id
     * @param weekId2  日期二id
     */
    public void changeShift(Integer userId, Integer storeId, Integer userId1, Integer userId2
            , Integer shiftId1, Integer shiftId2, Integer weekId1, Integer weekId2) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermission(user, storeId);
        validateType(userId1, userId2);
        judgeExists(userId, storeId, weekId1, shiftId1, userId1);
        if (weekId2 != null && shiftId2 != null) {
            judgeExists(userId, storeId, weekId2, shiftId2, userId2);
        }

        //当换班的日期数相同的时候，比如都是周一
        if (Objects.equals(weekId1, weekId2)) {
            Schedule schedule = scheduleService.fetchScheduleByDay(userId, storeId, weekId1);
            List<ScheduleDetail> scheduleDetailList = schedule.getScheduleDetails();
            ScheduleDetail scheduleDetail1 = scheduleDetailList.stream()
                    .filter(detail -> Objects.equals(detail.getShiftId(), shiftId1))
                    .findFirst()
                    .orElse(null);
            ScheduleDetail scheduleDetail2 = scheduleDetailList.stream()
                    .filter(detail -> Objects.equals(detail.getShiftId(), shiftId2))
                    .findFirst()
                    .orElse(null);
            //进行换班
            changeTwoPeople(scheduleDetail1, scheduleDetail2, userId1, userId2, shiftId1, shiftId2, storeId);
            changeSchedule(weekId1, storeId, scheduleDetailList);
        }
        //对应的班次没人
        else if (weekId2 == null && shiftId2 == null) {
            Schedule schedule = scheduleService.fetchScheduleByDay(userId, storeId, weekId1);
            List<ScheduleDetail> scheduleDetailList = schedule.getScheduleDetails();
            ScheduleDetail scheduleDetail = scheduleDetailList.stream()
                    .filter(detail -> Objects.equals(detail.getShiftId(), shiftId1))
                    .findFirst()
                    .orElse(null);
            changeOnePeople(scheduleDetail, userId1, userId2);
            changeSchedule(weekId1, storeId, scheduleDetailList);
        }
        //不同天进行换班
        else {
            Schedule schedule1 = scheduleService.fetchScheduleByDay(userId, storeId, weekId1);
            Schedule schedule2 = scheduleService.fetchScheduleByDay(userId, storeId, weekId2);
            List<ScheduleDetail> scheduleDetailList1 = schedule1.getScheduleDetails();
            ScheduleDetail scheduleDetail1 = scheduleDetailList1.stream()
                    .filter(detail -> Objects.equals(detail.getShiftId(), shiftId1))
                    .findFirst()
                    .orElse(null);
            List<ScheduleDetail> scheduleDetailList2 = schedule2.getScheduleDetails();
            ScheduleDetail scheduleDetail2 = scheduleDetailList2.stream()
                    .filter(detail -> Objects.equals(detail.getShiftId(), shiftId1))
                    .findFirst()
                    .orElse(null);
            judgeShift(scheduleDetail1, scheduleDetail2, userId1, userId2);
            changeTwoPeople(scheduleDetail1, scheduleDetail2, userId1, userId2, shiftId1, shiftId2, storeId);
            changeSchedule(weekId1, storeId, scheduleDetailList1);
            changeSchedule(weekId2, storeId, scheduleDetailList2);
        }
    }

    /**
     * 申请换班
     * @param userId1 申请人id
     * @param userId2 请求人id
     * @param shiftId1 原先班次id
     * @param shiftId2 换班班次id
     * @param weekId1 原先日期id
     * @param weekId2 换班日期id
     */
    public void addChangeShiftRecord(Integer userId1, Integer userId2
            , Integer shiftId1, Integer shiftId2, Integer weekId1, Integer weekId2) {
        if (userId1 == null || userId2 == null
                || shiftId1 == null || weekId1 == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        Integer storeId=userMapper.selectUserByUserId(userId1).getStoreId();
        Integer userId = changeShiftRecordMapper.findStorageIdByStoreId(storeId);
        validateType(userId1, userId2);
        judgeExists(userId, storeId, weekId1, shiftId1, userId1);
        if (weekId2 != null && shiftId2 != null) {
            judgeExists(userId, storeId, weekId2, shiftId2, userId2);
        }
        ChangeShiftRecord build = ChangeShiftRecord.builder()
                .requestPersonId(userId1)
                .reviewerPersonId(userId2)
                .shiftId1(shiftId1)
                .weekId1(weekId1)
                .shiftId2(shiftId2)
                .weekId2(weekId2)
                .type(ChangeShiftRecord.Type.NOT_PROCEED)
                .build();
        changeShiftRecordMapper.insert(build);
        ChangeShiftRecord changeShiftRecord = changeShiftRecordMapper.selectOne(new QueryWrapper<ChangeShiftRecord>()
                .eq("request_person_id", userId1)
                .eq("reviewer_person_id", userId2)
                .eq("week_id1", weekId1)
                .eq("week_id2", weekId2)
                .eq("shift_id1", shiftId1)
                .eq("shift_id2", shiftId2));
        stringRedisTemplate.opsForList().leftPush(userId2 + "-change-shift", JSON.toJSONString(changeShiftRecord));
    }
    public void reviewLeaveRecord(Integer userId, Integer recordId, boolean result) {
        if (userId == null || recordId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        ChangeShiftRecord changeRecord = changeShiftRecordMapper.selectById(recordId);
        if (changeRecord == null) {
            throw new AppException(ErrorCode.CHANGE_RECORD_NOT_EXISTED);
        }
        stringRedisTemplate.opsForList().remove(userId + "-change-shift", 1, JSON.toJSONString(changeRecord));
        if (result) {
            changeRecord.setType(ChangeShiftRecord.Type.PASS);
        } else {
            changeRecord.setType(ChangeShiftRecord.Type.REJECT);
        }
        if(result){
            Integer storeId=userMapper.selectUserByUserId(userId).getStoreId();
            Integer ManagerId = changeShiftRecordMapper.findStorageIdByStoreId(storeId);
            changeShift(ManagerId,storeId
                    ,changeRecord.getRequestPersonId()
                    ,changeRecord.getReviewerPersonId()
                    ,changeRecord.getShiftId1(),changeRecord.getShiftId2()
                    ,changeRecord.getWeekId1(),changeRecord.getWeekId2());
        }
        messageService.sendMessage(changeRecord.getRequestPersonId(), "您的换班申请已被审核");
    }
}
