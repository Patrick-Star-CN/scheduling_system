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
import team.delete.scheduling_system.entity.Rule;
import team.delete.scheduling_system.entity.RuleDetail;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.StoreMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.Map;

/**
 * @author Patrick_Star
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class StoreRuleService {
    @Resource
    private MongoTemplate mongoTemplate;

    private final StoreMapper storeMapper;

    private final UserMapper userMapper;

    /**
     * 判断权限
     *
     * @param userId 需要判断权限的用户id
     */
    public void judgePermission(Integer userId, Integer storeId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (user.getType() != User.Type.MANAGER || !storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 判断门店是否存在
     */
    private void judgeStoreExist(Integer storeId) {
        if (storeId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (storeMapper.selectById(storeId) == null) {
            throw new AppException(ErrorCode.STORE_NOT_EXISTED);
        }
    }

    /**
     * 插入门店规则
     *
     * @param userId 操作的用户id
     * @param rule 规则对象
     */
    public void insertRule(Integer userId, Rule rule) {
        if (rule == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgeStoreExist(rule.getStoreId());
        judgePermission(userId, rule.getStoreId());
        mongoTemplate.save(rule);
    }

    /**
     * 删除门店规则
     *
     * @param userId 操作的用户id
     * @param ruleId 规则对象id
     */
    public void deleteRule(Integer userId, String ruleId) {
        if (ruleId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        Rule rule = mongoTemplate.findById(ruleId, Rule.class);
        if (rule == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgeStoreExist(rule.getStoreId());
        judgePermission(userId, rule.getStoreId());
        Criteria criteria = Criteria.where("_id").is(ruleId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Rule.class);
    }

    /**
     * 修改门店规则
     *
     * @Param userId 操作的用户
     * @Param rule 规则对象
     */
    public void updateRule(Integer userId, Map<Integer, RuleDetail> ruleDetailMap, Integer storeId) {
        Criteria criteria = Criteria.where("store_id").is(storeId);
        Query query = new Query(criteria);
        Rule rule = mongoTemplate.findOne(query, Rule.class);
        if (rule == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgePermission(userId, rule.getStoreId());
        if (ruleDetailMap.get(0) != null) {
            rule.setCloseStore(ruleDetailMap.get(0));
        }
        if (ruleDetailMap.get(1) != null) {
            rule.setOpenStore(ruleDetailMap.get(1));
        }
        if (ruleDetailMap.get(2) != null) {
            rule.setPassenger(ruleDetailMap.get(2));
        }
        mongoTemplate.save(rule);
    }

    /**
     * 查询门店规则
     *
     * @Param userId 操作的用户
     * @Param storeId 门店id
     */
    public Rule fetchRule(Integer userId, Integer storeId) {
        if (storeId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgeStoreExist(storeId);
        judgePermission(userId, storeId);
        Criteria criteria = Criteria.where("store_id").is(storeId);
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Rule.class);
    }
}
