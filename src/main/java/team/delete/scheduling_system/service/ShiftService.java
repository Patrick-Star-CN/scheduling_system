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
import team.delete.scheduling_system.dto.UserScheduleDto;
import team.delete.scheduling_system.entity.*;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.CustomerFlowMapper;
import team.delete.scheduling_system.mapper.StoreMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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

    public void initShift(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermission(user, storeId);

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

    private void validateUserPermission(User user, Integer storeId) {
        if (user.getType() != User.Type.MANAGER || !storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    private void validateUserPermissionOnlyStore(User user, Integer storeId) {
        if (!storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    private List<CustomerFlow> getCustomerFlowList(Integer storeId) {
        List<CustomerFlow> customerFlowList = customerFlowMapper.selectList(new QueryWrapper<CustomerFlow>().eq("store_id", storeId));
        if (customerFlowList == null) {
            throw new AppException(ErrorCode.CUSTOMER_FLOW_NOT_EXISTED);
        }
        return customerFlowList;
    }

    private Rule getStoreRule(Integer storeId) {
        Rule storeRule = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId)), Rule.class);
        if (storeRule == null) {
            throw new AppException(ErrorCode.STORE_RULE_NOT_EXISTED);
        }
        return storeRule;
    }

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

    private String getStartTime(String startTime) {
        return LocalTime.parse(startTime).isBefore(LocalTime.parse("09:00")) ? "09:00" : startTime;
    }

    private String getEndTime(String endTime) {
        return LocalTime.parse(endTime).isAfter(LocalTime.parse("21:00")) ? "21:00" : endTime;
    }

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

    private int calculateCount(RuleDetail ruleDetail, double size, double formula) {
        return Math.max(1, Math.max(ruleDetail.getCount(), (int) (size / formula)) / 5);
    }


    public Shift fetchShift(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermissionOnlyStore(user, storeId);

        if (!storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId.toString())), Shift.class);
    }

    public void initSchedule(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermission(user, storeId);

        Shift shift = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(storeId.toString())), Shift.class);
        if (shift == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
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
            Schedule schedule = Schedule.builder().storeId(storeId.toString()).scheduleDetails(scheduleDetailList).build();
            scheduleList.add(schedule);
        }

        scheduleList.forEach(schedule -> mongoTemplate.save(schedule));
    }

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
}
