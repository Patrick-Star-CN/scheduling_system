package team.delete.scheduling_system.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.dto.ShiftDto;
import team.delete.scheduling_system.dto.UserScheduleDto;
import team.delete.scheduling_system.entity.*;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.CustomerFlowMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.time.LocalTime;
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
public class ShiftService {
    @Resource
    private MongoTemplate mongoTemplate;

    private final UserService userService;

    private final UserMapper userMapper;

    private final CustomerFlowMapper customerFlowMapper;

    private final StoreService storeService;

    /**
     * 生成排班框架
     *
     * @param userId  操作的用户id
     * @param storeId 操作的门店id
     */
    public void initShift(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermission(user, storeId);

        Shift shiftTmp = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId.toString())), Shift.class);
        if (shiftTmp != null) {
            return;
        }

        Store store = storeService.getStoreById(storeId);
        List<CustomerFlow> customerFlowList = getCustomerFlowList(storeId);
        Rule storeRule = getStoreRule(storeId);

        List<ShiftDetail> shiftDetailList = createShiftDetails(storeRule, store, customerFlowList);

        Shift shift = Shift.builder()
                .storeId(storeId.toString())
                .staffShift(shiftDetailList)
                .build();

        mongoTemplate.save(shift);
    }

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
     * 判断是否有普通员工权限
     *
     * @param user    用户对象
     * @param storeId 门店id
     */
    private void validateUserPermissionOnlyStore(User user, Integer storeId) {
        if (!storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 获取客流信息
     *
     * @param storeId 门店id
     * @return 客流信息列表
     */
    private List<CustomerFlow> getCustomerFlowList(Integer storeId) {
        List<CustomerFlow> customerFlowList = customerFlowMapper.selectList(new QueryWrapper<CustomerFlow>().eq("store_id", storeId));
        if (customerFlowList == null) {
            throw new AppException(ErrorCode.CUSTOMER_FLOW_NOT_EXISTED);
        }
        return customerFlowList;
    }

    /**
     * 获取门店规则
     *
     * @param storeId 门店id
     * @return 规则对象
     */
    private Rule getStoreRule(Integer storeId) {
        Rule storeRule = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId)), Rule.class);
        if (storeRule == null) {
            throw new AppException(ErrorCode.STORE_RULE_NOT_EXISTED);
        }
        return storeRule;
    }

    /**
     * 生成排班框架细则
     *
     * @param storeRule        门店规则
     * @param store            门店
     * @param customerFlowList 客流信息列表
     * @return 排班框架细则列表
     */
    private List<ShiftDetail> createShiftDetails(Rule storeRule, Store store, List<CustomerFlow> customerFlowList) {
        List<ShiftDetail> shiftDetailList = new ArrayList<>();

        shiftDetailList.add(createShiftDetail(0, String.valueOf(LocalTime.parse("09:00").minusMinutes(storeRule.getOpenStore().getTime().longValue())), "09:00", storeRule.getOpenStore(), store.getSize()));

        for (int i = 0; i < customerFlowList.size(); i++) {
            CustomerFlow customerFlow = customerFlowList.get(i);
            shiftDetailList.add(createShiftDetail(i + 1,
                    getStartTime(customerFlow.getStartTime()),
                    getEndTime(customerFlow.getEndTime()),
                    storeRule.getPassenger(),
                    customerFlow.getFlow()));
        }

        shiftDetailList.add(createShiftDetail(customerFlowList.size() + 1, "21:00", String.valueOf(LocalTime.parse("21:00").plusMinutes(storeRule.getCloseStore().getTime().longValue())), storeRule.getCloseStore(), store.getSize()));

        return shiftDetailList;
    }

    /**
     * 通过客流数据计算每一班开始的时间，该时间不能早于 09:00
     *
     * @param startTime 开始时间
     * @return 最终开始时间
     */
    private String getStartTime(String startTime) {
        return LocalTime.parse(startTime).isBefore(LocalTime.parse("09:00")) ? "09:00" : startTime;
    }

    /**
     * 通过客流数据计算每一班结束的时间，该时间不能迟于 21:00
     *
     * @param endTime 结束时间
     * @return 最终结束时间
     */
    private String getEndTime(String endTime) {
        return LocalTime.parse(endTime).isAfter(LocalTime.parse("21:00")) ? "21:00" : endTime;
    }

    /**
     * 生成排班框架细则
     *
     * @param shiftId    班次id
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param ruleDetail 规则细则
     * @param size       门店大小
     * @return 排班框架细则
     */
    private ShiftDetail createShiftDetail(int shiftId, String startTime, String endTime, RuleDetail ruleDetail, double size) {
        return ShiftDetail.builder()
                .shiftId(shiftId)
                .startTime(startTime)
                .endTime(endTime)
                .cashierCount(calculateCount(ruleDetail, size, ruleDetail.getFormula()))
                .storageCount(calculateCount(ruleDetail, size, ruleDetail.getFormula()))
                .customerServiceCount(calculateCount(ruleDetail, size, ruleDetail.getFormula()) * 3)
                .build();
    }

    /**
     * 计算每一班的人数
     *
     * @param ruleDetail 规则细则
     * @param size       门店大小
     * @param formula    公式
     * @return 人数
     */
    private int calculateCount(RuleDetail ruleDetail, double size, double formula) {
        return Math.max(1, Math.max(ruleDetail.getCount(), (int) (size / formula)) / 5);
    }

    /**
     * 获取排班框架
     *
     * @param userId  操作的用户id
     * @param storeId 操作的门店id
     * @return 排班框架
     */
    public ShiftDto fetchShift(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermissionOnlyStore(user, storeId);

        if (!storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        Shift shift = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId.toString())), Shift.class);
        if (shift == null) {
            throw new AppException(ErrorCode.SHIFT_NOT_EXISTED);
        }

        return ShiftDto.builder()
                .storeId(storeId.toString())
                .startTime(shift.getStaffShift().get(0).getStartTime())
                .endTime(shift.getStaffShift().get(shift.getStaffShift().size() - 1).getEndTime())
                .build();
    }

    /**
     * 生成排班安排
     *
     * @param userId  操作的用户id
     * @param storeId 操作的门店id
     */
    public void initSchedule(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermission(user, storeId);

        Shift shift = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId.toString())), Shift.class);
        if (shift == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }

        Schedule scheduleTmp = fetchScheduleByDay(userId, storeId, 0);
        if (scheduleTmp != null) {
            return;
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

            if ((preference == null || !preference.getPreferenceDetail().get(0).getTime().contains(dayIndex)
                    && !preference.getPreferenceDetail().get(1).getTime().contains(shiftIndex))
                    && weekScheduleCount.get(user.getUserId()) > 0
                    && dayScheduleCountList.get(dayIndex).get(user.getUserId()) > 0) {
                UserScheduleDto userScheduleDto = UserScheduleDto.builder()
                        .userId(user.getUserId())
                        .name(userMapper.selectById(user.getUserId()).getName())
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
        validateUserPermissionOnlyStore(user, storeId);

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
        validateUserPermissionOnlyStore(user, storeId);

        if (!storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }

        return mongoTemplate.find(
                new Query(Criteria.where("store_id").is(storeId.toString())),
                Schedule.class
        );
    }
}
