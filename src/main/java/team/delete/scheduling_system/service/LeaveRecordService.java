package team.delete.scheduling_system.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.dto.LeaveRecordDto;
import team.delete.scheduling_system.dto.UserDto;
import team.delete.scheduling_system.dto.UserScheduleDto;
import team.delete.scheduling_system.entity.LeaveRecord;
import team.delete.scheduling_system.entity.Schedule;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.LeaveRecordMapper;
import team.delete.scheduling_system.mapper.ProfessionMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author YYHelen
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class LeaveRecordService {
    final LeaveRecordMapper leaveRecordMapper;
    final ProfessionMapper professionMapper;
    final UserMapper userMapper;
    final StringRedisTemplate stringRedisTemplate;
    final MongoTemplate mongoTemplate;
    final MessageService messageService;

    /**
     * 查询自己的所有请假记录
     *
     * @param userId 请假人id
     * @return 请假人请假记录
     */
    public List<LeaveRecordDto> fetchAllLeaveRecord(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }

        List<LeaveRecord> leaveRecords = leaveRecordMapper.selectLeaveRecordListByUserId(userId);
        List<LeaveRecordDto> leaveRecordDtos = new ArrayList<>();
        for (LeaveRecord record : leaveRecords) {
            leaveRecordDtos.add(convertToDto(record));
        }
        return leaveRecordDtos;
    }

    /**
     * 转换为dto
     *
     * @param leaveRecord 请假人记录
     * @return leaveRecordDto
     */
    public LeaveRecordDto convertToDto(LeaveRecord leaveRecord) {
        Timestamp reviewTime = leaveRecord.getReviewTime();
        String reviewTimeString = (reviewTime != null) ? reviewTime.toString() : "未审核";
        String requesterName=userMapper.selectUserByUserId(leaveRecord.getRequestPersonId()).getName();
        String reviewerName=userMapper.selectUserByUserId(leaveRecord.getReviewerPersonId()).getName();
        return LeaveRecordDto.builder()
                .recordId(leaveRecord.getRecordId())
                .requestPerson(requesterName)
                .reviewerPerson(reviewerName)
                .leaveTime(leaveRecord.getLeaveTime().toString())
                .reviewTime(reviewTimeString)
                .type(leaveRecord.getType().toString())
                .scheduleShift(leaveRecord.getScheduleShift())
                .build();
    }



    /**
     * 查询自己的具体某一天某个班次请假记录
     *
     * @param userId        请假人id
     * @param leaveTime     申请的请假时间
     * @param scheduleShift 申请请假的排班班次
     * @return 请假记录列表
     */
    public LeaveRecord findLeaveRecordByShift(Integer userId, LocalDate leaveTime, Integer scheduleShift) {
        if (userId == null || leaveTime == null || scheduleShift == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return leaveRecordMapper.selectLeaveRecordByShift(userId, leaveTime, scheduleShift);
    }

    /**
     * 查询自己的具体某一天请假记录
     *
     * @param userId    请假人id
     * @param leaveTime 申请的请假时间
     * @return 请假记录列表
     */
    public List<LeaveRecord> findLeaveRecordByDate(Integer userId, LocalDate leaveTime) {
        if (userId == null || leaveTime == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return leaveRecordMapper.selectLeaveRecordByDate(userId, leaveTime);
    }

    /**
     * 查询自己在某个时间区间请假记录
     *
     * @param userId    请假人id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 请假记录列表
     */
    public List<LeaveRecord> fetchAllLeaveRecordByRange(Integer userId, LocalDate startTime, LocalDate endTime) {
        if (userId == null || startTime == null || endTime == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (endTime.isBefore(startTime)) {
            throw new AppException(ErrorCode.USELESS_TIME_RANGE);
        }
        return leaveRecordMapper.selectLeaveRecordListByRange(userId, startTime, endTime);
    }

    /**
     * 根据记录id删除未审核的记录
     *
     * @param userId   删除人id
     * @param recordId 请假记录id
     */
    public void deleteLeaveRecordByRecordId(Integer userId, Integer recordId) {
        if (userId == null || recordId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        LeaveRecord leaveRecord = leaveRecordMapper.selectLeaveRecordByRecordId(recordId);
        if (leaveRecord == null) {
            throw new AppException(ErrorCode.LEAVE_RECORD_NOT_EXISTED);
        }
        Integer requestPersonId = leaveRecord.getRequestPersonId();
        Integer reviewerPersonId = leaveRecord.getReviewerPersonId();
        if (userId.equals(reviewerPersonId) || userId.equals(requestPersonId)) {
            leaveRecordMapper.deleteById(leaveRecord);
        } else {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 根据请假时间删除未审核的记录
     *
     * @param userId        删除人id
     * @param leaveTime     请假时间
     * @param scheduleShift 请假的班次
     */
    public void deleteLeaveRecordByTime(Integer userId, LocalDate leaveTime, Integer scheduleShift) {
        if (userId == null || leaveTime == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        LeaveRecord leaveRecord = findLeaveRecordByShift(userId, leaveTime, scheduleShift);
        if (leaveRecord == null) {
            throw new AppException(ErrorCode.LEAVE_RECORD_NOT_EXISTED);
        }
        Integer requestPersonId = leaveRecord.getRequestPersonId();
        Integer reviewerPersonId = leaveRecord.getReviewerPersonId();
        if (userId.equals(reviewerPersonId) || userId.equals(requestPersonId)) {
            leaveRecordMapper.deleteById(leaveRecord);
        } else {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 添加请假记录（申请请假）
     *
     * @param requestPersonId 用户id
     * @param leaveTime       请假人需要的请假时间
     * @param scheduleShift   请假人需要的请假班次
     */
    public void addLeaveRecord(Integer requestPersonId, LocalDate leaveTime, Integer scheduleShift) {
        //输入参数不能为0
        if (requestPersonId == null || leaveTime == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        // 获取当前的 LocalDate
        LocalDate currentLocalDate = LocalDate.now();
        int dayOfWeek = leaveTime.getDayOfWeek().getValue() - 1;

        // 比较 leaveTime 是否为当天日期或当天之后的日期
        if (leaveTime.isBefore(currentLocalDate)) {
            throw new AppException(ErrorCode.ILLEGAL_LEAVE_TIME);
        }
        UserDto requestPerson = userMapper.selectUserByUserId(requestPersonId);
        if (requestPerson == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (leaveRecordMapper.selectLeaveRecordByDateShift(requestPersonId, leaveTime, scheduleShift) != null) {
            throw new AppException(ErrorCode.REPEAT_LEAVE_RECORD);
        }
        String requestPersonType = requestPerson.getType();
        if (requestPersonType == null) {
            throw new AppException(ErrorCode.INCOMPLETE_USER_INFOEMATION);
        }
        Integer storeId = requestPerson.getStoreId();
        Integer groupId = requestPerson.getGroupId();
        //筛选出来的用户信息必须完整
        if (storeId == null || groupId == null) {
            throw new AppException(ErrorCode.INCOMPLETE_USER_INFOEMATION);
        }

        List<Schedule> scheduleList = mongoTemplate.find(new Query(Criteria.where("store_id").is(storeId.toString())), Schedule.class);
        if (scheduleList.isEmpty()) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        Schedule schedule = scheduleList.get(dayOfWeek);
        boolean flag = false;
        int size = 0;
        switch (requestPerson.getType()) {
            case "CASHIER":
                for (UserScheduleDto userScheduleDto : schedule.getScheduleDetails().get(scheduleShift).getCashierList()) {
                    if (userScheduleDto.getUserId().equals(requestPersonId)) {
                        flag = true;
                        size = schedule.getScheduleDetails().get(scheduleShift).getCashierList().size();
                        break;
                    }
                }
                break;
            case "CUSTOMER_SERVICE":
                for (UserScheduleDto userScheduleDto : schedule.getScheduleDetails().get(scheduleShift).getCustomerServiceList()) {
                    if (userScheduleDto.getUserId().equals(requestPersonId)) {
                        flag = true;
                        size = schedule.getScheduleDetails().get(scheduleShift).getCustomerServiceList().size();
                        break;
                    }
                }
                break;
            case "STORAGE":
                for (UserScheduleDto userScheduleDto : schedule.getScheduleDetails().get(scheduleShift).getStorageList()) {
                    if (userScheduleDto.getUserId().equals(requestPersonId)) {
                        flag = true;
                        size = schedule.getScheduleDetails().get(scheduleShift).getStorageList().size();
                        break;
                    }
                }
                break;
        }

        if (!flag) {
            throw new AppException(ErrorCode.USER_NOT_IN_SCHEDULE);
        }
        if (size == 1) {
            throw new AppException(ErrorCode.USER_CAN_NOT_LEAVE);
        }

        Integer reviewerId = null;
        //查找审核人
        switch (requestPersonType) {
            case "CASHIER":
            case "CUSTOMER_SERVICE":
            case "STORAGE":
                reviewerId = leaveRecordMapper.findGroupManagerIdByGroupId(groupId);
                break;
            case "GROUP_MANAGER":
                reviewerId = leaveRecordMapper.findViceManagerIdByStoreId(groupId, storeId);
                break;
            case "VICE_MANAGER":
                reviewerId = leaveRecordMapper.findStorageIdByStoreId(storeId);
                break;
            case "MANAGER":
                reviewerId = leaveRecordMapper.findSuperAdmin();
                break;
        }
        if (reviewerId == null) {
            throw new AppException(ErrorCode.REVIEWER_NOT_EXISTED);
        }

        LeaveRecord build = LeaveRecord.builder().requestPersonId(requestPersonId)
                .reviewerPersonId(reviewerId)
                .leaveTime(leaveTime)
                .type(LeaveRecord.Type.NOT_PROCEED)
                .scheduleShift(scheduleShift).build();
        String finalReviewerId = reviewerId.toString();
        leaveRecordMapper.insert(build);
        LeaveRecord leaveRecord = leaveRecordMapper.selectOne(new QueryWrapper<LeaveRecord>()
                .eq("request_person_id", requestPersonId)
                .eq("reviewer_person_id", reviewerId)
                .eq("leave_time", leaveTime)
                .eq("schedule_shift", scheduleShift));
        stringRedisTemplate.opsForList().leftPush(finalReviewerId + "-leave", JSON.toJSONString(leaveRecord));
    }

    /**
     * 查询需要审核的某个时间区间请假记录
     *
     * @param userId    请假人id
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 请假记录列表
     */
    public List<LeaveRecord> fetchAllReViewLeaveRecordByTime(Integer userId, LocalDate startTime, LocalDate endTime) {
        if (userId == null || startTime == null || endTime == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        return leaveRecordMapper.selectReviewLeaveRecordListByTimeRange(userId, startTime, endTime);
    }

    /**
     * 查询需要审核的请假记录
     *
     * @param userId 审核人id
     * @return 请假记录列表
     */
    public List<LeaveRecord> fetchAllNeedReviewLeaveRecord(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        UserDto userDto = userMapper.selectUserByUserId(userId);
        if (userDto == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        String userType = userDto.getType();
        if (userType == null) {
            throw new AppException(ErrorCode.INCOMPLETE_USER_INFOEMATION);
        }
        if ("CASHIER".equals(userType) || "CUSTOMER_SERVICE".equals(userType) || "STORAGE".equals(userType)) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        List<String> recordList = stringRedisTemplate.opsForList().range(userId + "-leave", 0, -1);
        if (recordList == null || recordList.isEmpty()) {
            return leaveRecordMapper.selectNeedReviewLeaveRecordListByUserId(userId);
        }
        return JSON.parseArray(recordList.toString(), LeaveRecord.class);
    }

    /**
     * 查询审核的请假记录(包括已审核和未审核的）
     *
     * @param userId 审核人id
     * @return 请假记录列表
     */
    public List<LeaveRecordDto> fetchAllReviewLeaveRecord(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        UserDto userDto = userMapper.selectUserByUserId(userId);
        if (userDto == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        String userType = userDto.getType();
        if (userType == null) {
            throw new AppException(ErrorCode.INCOMPLETE_USER_INFOEMATION);
        }
        if ("CASHIER".equals(userType) || "CUSTOMER_SERVICE".equals(userType) || "STORAGE".equals(userType)) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        List<LeaveRecord> leaveRecords =leaveRecordMapper.selectReviewLeaveRecordListByUserId(userId);
        List<LeaveRecordDto> leaveRecordDtos = new ArrayList<>();

        for (LeaveRecord record : leaveRecords) {
            leaveRecordDtos.add(convertToDto(record));
        }
        return leaveRecordDtos;
    }

    /**
     * 审核请假记录
     *
     * @param userId   审核人id
     * @param recordId 请假记录id
     * @param result   审核结果
     */
    public void reviewLeaveRecord(Integer userId, Integer recordId, boolean result) {
        if (userId == null || recordId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userMapper.selectUserByUserId(userId) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        LeaveRecord leaveRecord = leaveRecordMapper.selectLeaveRecordByRecordId(recordId);
        if (leaveRecord == null) {
            throw new AppException(ErrorCode.LEAVE_RECORD_NOT_EXISTED);
        }
        //检查有没有权限
        if (!leaveRecord.getReviewerPersonId().equals(userId)) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }

        stringRedisTemplate.opsForList().remove(userId + "-leave", 1, JSON.toJSONString(leaveRecord));
        Date currentDate = new Date();
        Timestamp currentTimestamp = new Timestamp(currentDate.getTime());
        // 将Timestamp对象设置到leaveRecord中的reviewTime属性
        leaveRecord.setReviewTime(currentTimestamp);
        if (result) {
            leaveRecord.setType(LeaveRecord.Type.PASS);
        } else {
            leaveRecord.setType(LeaveRecord.Type.REJECT);
        }
        leaveRecordMapper.updateById(leaveRecord);
        if (result) {
            int dayOfWeek = leaveRecord.getLeaveTime().getDayOfWeek().getValue() - 1;
            UserDto user = userMapper.selectUserByUserId(leaveRecord.getRequestPersonId());
            Schedule schedule = mongoTemplate.find(new Query(Criteria.where("store_id").is(user.getStoreId().toString())), Schedule.class).get(dayOfWeek);
            switch (user.getType()) {
                case "CASHIER":
                    for (UserScheduleDto userScheduleDto : schedule.getScheduleDetails().get(leaveRecord.getScheduleShift()).getCashierList()) {
                        if (userScheduleDto.getUserId().equals(user.getUserId())) {
                            userScheduleDto.setLeave(true);
                            break;
                        }
                    }
                    break;
                case "CUSTOMER_SERVICE":
                    for (UserScheduleDto userScheduleDto : schedule.getScheduleDetails().get(leaveRecord.getScheduleShift()).getCustomerServiceList()) {
                        if (userScheduleDto.getUserId().equals(user.getUserId())) {
                            userScheduleDto.setLeave(true);
                            break;
                        }
                    }
                    break;
                case "STORAGE":
                    for (UserScheduleDto userScheduleDto : schedule.getScheduleDetails().get(leaveRecord.getScheduleShift()).getStorageList()) {
                        if (userScheduleDto.getUserId().equals(user.getUserId())) {
                            userScheduleDto.setLeave(true);
                            break;
                        }
                    }
                    break;
            }
            mongoTemplate.save(schedule);
        }
        messageService.sendMessage(leaveRecord.getRequestPersonId(), "您的请假申请已被审核");
    }
}
