package team.delete.scheduling_system.service;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Preference;
import team.delete.scheduling_system.entity.PreferenceDetail;
import team.delete.scheduling_system.exception.AppException;

/**
 * @author Devin100086
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class PreferenceService {
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 查询用户是否存在
     *
     * @param userId 用户id
     */
    private void findUser(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        // 创建条件对象
        Criteria criteria = Criteria.where("userId").is(userId);
        Query query = new Query(criteria);
        // 查询
        Preference preference = mongoTemplate.findOne(query, Preference.class);
        if (preference == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
    }

    /**
     * 查询用户个人偏好
     *
     * @param userId 用户id
     * @return 偏好
     */
    public Preference findPreferenceByUserId(Integer userId) {
        findUser(userId);
        // 创建条件对象
        Criteria criteria = Criteria.where("userId").is(userId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Preference.class);
    }

    /**
     * 删除用户个人偏好
     *
     * @param userId 用户id
     */
    public void deletePreferenceByUserId(Integer userId) {
        findUser(userId);
        // 创建条件对象
        Criteria criteria = Criteria.where("userId").is(userId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = Query.query(criteria);
        mongoTemplate.remove(query, Preference.class);
    }

    /**
     * 添加用户偏好
     *
     * @param preference 偏好
     */
    public void addPreference(Preference preference) {
        if (preference == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        mongoTemplate.save(preference);
    }

    /**
     * 更新用户偏好
     *
     * @param userId           用户id
     * @param preferenceDetail 偏好值
     */
    public void updatePreference(Integer userId, PreferenceDetail preferenceDetail) {
        findUser(userId);
        if (preferenceDetail == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        // 创建条件对象
        Criteria criteria = Criteria.where("userId").is(userId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        // 创建更新对象,并设置更新的内容
        Update update = new Update().set("preferenceDetail", preferenceDetail);
        mongoTemplate.upsert(query, update, Preference.class);
    }
}
