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
import team.delete.scheduling_system.entity.*;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.CustomerFlowMapper;

import java.time.LocalTime;
import java.util.*;

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

    private final CustomerFlowMapper customerFlowMapper;

    private final StoreService storeService;

    /**
     * 删除排班框架
     *
     * @param userId  操作的用户id
     * @param storeId 操作的门店id
     */
    public void deleteShift(Integer userId, Integer storeId) {
        User user = userService.fetchUserByUserId(userId);
        validateUserPermission(user, storeId);

        mongoTemplate.remove(new Query(Criteria.where("store_id").is(storeId.toString())), Shift.class);
    }

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
            deleteShift(userId, storeId);
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
    public void validateUserPermission(User user, Integer storeId) {
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
    public void validateUserPermissionOnlyStore(User user, Integer storeId) {
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
}
