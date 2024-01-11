package team.delete.scheduling_system.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.dto.UserScheduleDto;
import team.delete.scheduling_system.entity.*;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.GroupMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class ScheduleService {
    @Resource
    private MongoTemplate mongoTemplate;

    private final UserService userService;

    private final UserMapper userMapper;

    private final ShiftService shiftService;

    private final GroupMapper groupMapper;

    /**
     * 删除排班框架
     *
     * @param userId 操作的用户id
     * @param storeId 操作的门店id
     */
    public void deleteSchedule(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        shiftService.validateUserPermission(user, storeId);

        mongoTemplate.remove(new Query(Criteria.where("store_id").is(storeId.toString())), Schedule.class);
    }

    /**
     * 生成排班安排
     *
     * @param userId  操作的用户id
     * @param storeId 操作的门店id
     */
    public void initSchedule(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        shiftService.validateUserPermission(user, storeId);

        Shift shift = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId.toString())), Shift.class);
        if (shift == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }

        Schedule scheduleTmp = fetchScheduleByDay(userId, storeId, 0);
        if (scheduleTmp != null) {
            deleteSchedule(userId, storeId);
        }

        Map<User.Type, List<User>> usersByType = Arrays.stream(User.Type.values())
                .collect(Collectors.toMap(type -> type, type -> userMapper.selectUserListByStoreIdAndType(storeId, type)));

        Map<User.Type, List<Preference>> preferencesByType = usersByType.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry ->
                        entry.getValue().stream()
                                .map(userTmp -> mongoTemplate.findOne(new Query(Criteria.where("user_id").is(userTmp.getUserId())), Preference.class))
                                .collect(Collectors.toList())));

        Map<Integer, Integer> weekScheduleCount = new HashMap<>();
        List<Map<Integer, Integer>> dayScheduleCountList = IntStream.range(0, 7)
                .mapToObj(i -> new HashMap<Integer, Integer>())
                .collect(Collectors.toList());

        usersByType.forEach((type, userList) -> {
            List<Preference> preferenceList = preferencesByType.get(type);
            userList.forEach(userTmp -> {
                preferenceList.add(mongoTemplate.findOne(new Query(Criteria.where("user_id").is(userTmp.getUserId())), Preference.class));
                weekScheduleCount.put(userTmp.getUserId(), 12);
                dayScheduleCountList.forEach(dayScheduleCount -> dayScheduleCount.put(userTmp.getUserId(), 2));
            });
        });

        List<Schedule> scheduleList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            List<ScheduleDetail> scheduleDetailList = new ArrayList<>();
            for (int j = 0; j < shift.getStaffShift().size(); j++) {
                ScheduleDetail scheduleDetail = generateScheduleDetail(i, j, shift, usersByType, weekScheduleCount, dayScheduleCountList);
                scheduleDetailList.add(scheduleDetail);
            }
            Schedule schedule = Schedule.builder().storeId(storeId.toString()).weekId(i).scheduleDetails(scheduleDetailList).build();
            scheduleList.add(schedule);
        }

        scheduleList.forEach(schedule -> mongoTemplate.save(schedule));
    }

    /**
     * 生成排班安排细则
     *
     * @param dayIndex             每周中的序号
     * @param shiftIndex           班次序号
     * @param shift                班次
     * @param usersByType          按照职位分类的用户列表
     * @param weekScheduleCount    每周排班次数
     * @param dayScheduleCountList 每日排班次数列表
     * @return 排班安排细则
     */
    private ScheduleDetail generateScheduleDetail(int dayIndex, int shiftIndex, Shift shift,
                                                  Map<User.Type, List<User>> usersByType,
                                                  Map<Integer, Integer> weekScheduleCount,
                                                  List<Map<Integer, Integer>> dayScheduleCountList) {
        ScheduleDetail scheduleDetail = ScheduleDetail.builder()
                .shiftId(shiftIndex)
                .cashierList(new ArrayList<>())
                .customerServiceList(new ArrayList<>())
                .storageList(new ArrayList<>())
                .build();

        processPreferences(usersByType.get(User.Type.STORAGE), dayIndex, shiftIndex, shift.getStaffShift().get(shiftIndex).getStorageCount(),
                weekScheduleCount, dayScheduleCountList, scheduleDetail.getStorageList());
        processPreferences(usersByType.get(User.Type.CASHIER), dayIndex, shiftIndex, shift.getStaffShift().get(shiftIndex).getCashierCount(),
                weekScheduleCount, dayScheduleCountList, scheduleDetail.getCashierList());
        processPreferences(usersByType.get(User.Type.CUSTOMER_SERVICE), dayIndex, shiftIndex, shift.getStaffShift().get(shiftIndex).getCustomerServiceCount(),
                weekScheduleCount, dayScheduleCountList, scheduleDetail.getCustomerServiceList());

        return scheduleDetail;
    }

    /**
     * 处理用户偏好
     *
     * @param users                用户列表
     * @param dayIndex             每周中的序号
     * @param shiftIndex           班次序号
     * @param requiredCount        需要的人数
     * @param weekScheduleCount    每周排班次数
     * @param dayScheduleCountList 每日排班次数列表
     * @param userList             用户排班列表
     */
    private void processPreferences(List<User> users, int dayIndex, int shiftIndex, int requiredCount,
                                    Map<Integer, Integer> weekScheduleCount,
                                    List<Map<Integer, Integer>> dayScheduleCountList, List<UserScheduleDto> userList) {
        int count = 0;
        for (User user : users) {
            Preference preference = mongoTemplate.findOne(new Query(Criteria.where("user_id").is(user.getUserId())), Preference.class);

            if ((preference == null || (preference.getPreferenceDetail().isEmpty() || !preference.getPreferenceDetail().get(0).getTime().contains(dayIndex))
                    && (preference.getPreferenceDetail().size() == 1 || !preference.getPreferenceDetail().get(1).getTime().contains(shiftIndex)))
                    && weekScheduleCount.get(user.getUserId()) > 0
                    && dayScheduleCountList.get(dayIndex).get(user.getUserId()) > 0) {
                UserScheduleDto userScheduleDto = UserScheduleDto.builder()
                        .userId(user.getUserId())
                        .name(userMapper.selectById(user.getUserId()).getName())
                        .leave(false)
                        .build();
                userList.add(userScheduleDto);
                weekScheduleCount.put(user.getUserId(), weekScheduleCount.get(user.getUserId()) - 1);
                dayScheduleCountList.get(dayIndex).put(user.getUserId(), dayScheduleCountList.get(dayIndex).get(user.getUserId()) - 1);
                count++;

                if (count >= requiredCount) {
                    break;
                }
            }
        }
    }

    /**
     * 获取排班安排
     *
     * @param userId   操作的用户id
     * @param storeId  操作的门店id
     * @param dayIndex 每周中的序号
     * @return 排班安排
     */
    public Schedule fetchScheduleByDay(Integer userId, Integer storeId, Integer dayIndex) {
        User user = userService.fetchUserByUserId(userId);
        shiftService.validateUserPermissionOnlyStore(user, storeId);

        if (!storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }

        return mongoTemplate.findOne(
                new Query(Criteria.where("store_id").is(storeId.toString())
                        .and("week_id").is(dayIndex)),
                Schedule.class
        );
    }

    /**
     * 获取排班安排
     *
     * @param userId  操作的用户id
     * @param storeId 操作的门店id
     * @return 排班安排
     */
    public List<Schedule> fetchScheduleList(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        shiftService.validateUserPermissionOnlyStore(user, storeId);

        if (!storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }

        return mongoTemplate.find(
                new Query(Criteria.where("store_id").is(storeId.toString())),
                Schedule.class
        );
    }

    /**
     * 获取个人排班表
     *
     * @param userId 操作的用户id
     * @return 个人排班表
     */
    public List<List<Integer>> fetchPersonalSchedule(Integer userId) {
        User user = userService.fetchUserByUserId(userId);
        shiftService.validateUserPermissionOnlyStore(user, user.getStoreId());
        Group group = groupMapper.selectById(user.getGroupId());
        if (!userId.equals(user.getUserId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }

        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            result.add(new ArrayList<>());
        }

        List<Schedule> scheduleList = fetchScheduleList(userId, user.getStoreId());
        for (Schedule schedule : scheduleList) {
            for (ScheduleDetail scheduleDetail : schedule.getScheduleDetails()) {
                switch (group.getType()) {
                    case STORAGE:
                        for (UserScheduleDto userScheduleDto : scheduleDetail.getStorageList()) {
                            if (userScheduleDto.getUserId().equals(userId)) {
                                result.get(schedule.getWeekId()).add(scheduleDetail.getShiftId());
                            }
                        }
                        break;
                    case CUSTOMER_SERVICE:
                        for (UserScheduleDto userScheduleDto : scheduleDetail.getCustomerServiceList()) {
                            if (userScheduleDto.getUserId().equals(userId)) {
                                result.get(schedule.getWeekId()).add(scheduleDetail.getShiftId());
                            }
                        }
                        break;
                    case CASHIER:
                        for (UserScheduleDto userScheduleDto : scheduleDetail.getCashierList()) {
                            if (userScheduleDto.getUserId().equals(userId)) {
                                result.get(schedule.getWeekId()).add(scheduleDetail.getShiftId());
                            }
                        }
                        break;
                }
            }
        }
        return result;
    }
}
