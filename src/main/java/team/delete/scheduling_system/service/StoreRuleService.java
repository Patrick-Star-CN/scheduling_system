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

import java.util.List;
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
    public void judgePermission(Integer userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (user.getType() != User.Type.MANAGER) {
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
     * @param userId     操作的用户id
     * @param openStore  开店规则
     * @param closeStore 关店规则
     * @param passenger  客流规则
     */
    public void insertRule(Integer userId, RuleDetail openStore, RuleDetail closeStore, RuleDetail passenger) {
        if (openStore == null || closeStore == null || passenger == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        judgePermission(userId);
        User user = userMapper.selectById(userId);
        if (fetchRule(userId) != null) {
            deleteRule(userId);
        }

        mongoTemplate.save(Rule.builder()
                .storeId(user.getStoreId())
                .openStore(openStore)
                .closeStore(closeStore)
                .passenger(passenger).build());
    }

    /**
     * 删除门店规则
     *
     * @param userId 操作的用户id
     */
    public void deleteRule(Integer userId) {
        judgePermission(userId);
        User user = userMapper.selectById(userId);
        Rule rule = mongoTemplate.findOne(new Query(Criteria.where("store_id").is(user.getStoreId())), Rule.class);
        if (rule == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        mongoTemplate.remove(new Query(Criteria.where("_id").is(rule.getRuleId())), Rule.class);
    }

    /**
     * 查询门店规则
     *
     * @Param userId 操作的用户
     * @Param storeId 门店id
     */
    public Rule fetchRule(Integer userId) {
        judgePermission(userId);
        User user = userMapper.selectById(userId);
        return mongoTemplate.findOne(new Query(Criteria.where("store_id").is(user.getStoreId())), Rule.class);
    }
}
