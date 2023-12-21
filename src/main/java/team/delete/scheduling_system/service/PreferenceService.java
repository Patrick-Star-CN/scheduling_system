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

import java.util.ArrayList;

/**
 * @author Devin100086 Patrick_Star
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class PreferenceService {
    @Resource
    private MongoTemplate mongoTemplate;

    private void checkDetail(PreferenceDetail preferenceDetail) {
        switch (preferenceDetail.getType()) {
            case WORKDAY:
                boolean[] workday = new boolean[7];
                for (Integer item : preferenceDetail.getTime()) {
                    if (item < 1 || item > 7 || workday[item - 1]) {
                        throw new AppException(ErrorCode.PARAM_ERROR);
                    }
                    workday[item - 1] = true;
                }
                break;
            case SHIFT:
                boolean[] shift = new boolean[3];
                for (Integer item : preferenceDetail.getTime()) {
                    if (item < 0 || item > 3 || shift[item]) {
                        throw new AppException(ErrorCode.PARAM_ERROR);
                    }
                    shift[item] = true;
                }
                break;
        }
    }

    /**
     * 查询用户个人偏好
     *
     * @param userId 用户id
     * @return 偏好
     */
    public Preference findPreferenceByUserId(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        // 创建条件对象
        Criteria criteria = Criteria.where("userId").is(userId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = Query.query(criteria);
        return mongoTemplate.findOne(query, Preference.class);
    }

    /**
     * 删除用户个人偏好
     *
     * @param userId                用户id
     * @param oldPreferenceDetailId 旧偏好id
     */
    public void deletePreferenceByUserId(Integer userId, Integer oldPreferenceDetailId) {
        if (userId == null || oldPreferenceDetailId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        Preference preference = findPreferenceByUserId(userId);
        if (oldPreferenceDetailId > preference.getPreferenceDetail().size() || oldPreferenceDetailId < 0) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        preference.getPreferenceDetail().remove((int) oldPreferenceDetailId);
        // 创建条件对象
        Criteria criteria = Criteria.where("userId").is(userId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        // 创建更新对象,并设置更新的内容
        Update update = new Update().set("preferenceDetail", preference.getPreferenceDetail());
        mongoTemplate.upsert(query, update, Preference.class);
    }

    /**
     * 添加用户偏好
     *
     * @param userId           用户id
     * @param preferenceDetail 偏好
     */
    public void addPreference(Integer userId, PreferenceDetail preferenceDetail) {
        if (preferenceDetail == null || userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        checkDetail(preferenceDetail);
        Preference preferenceNow = findPreferenceByUserId(userId);
        if (preferenceNow == null) {
            ArrayList<PreferenceDetail> preferenceDetails = new ArrayList<>();
            preferenceDetails.add(preferenceDetail);
            preferenceNow = Preference.builder()
                    .userId(userId)
                    .preferenceDetail(preferenceDetails).build();
        } else {
            if (preferenceNow.getPreferenceDetail() == null) {
                preferenceNow.setPreferenceDetail(new ArrayList<>());
            }
            preferenceNow.getPreferenceDetail().add(preferenceDetail);
        }
        mongoTemplate.save(preferenceNow);
    }

    /**
     * 更新用户偏好
     *
     * @param userId                用户id
     * @param oldPreferenceDetailId 旧偏好id
     * @param newPreferenceDetail   新偏好
     */
    public void updatePreference(Integer userId,
                                 Integer oldPreferenceDetailId,
                                 PreferenceDetail newPreferenceDetail) {
        if (userId == null || oldPreferenceDetailId == null || newPreferenceDetail == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        checkDetail(newPreferenceDetail);
        Preference preference = findPreferenceByUserId(userId);
        if (preference == null
                || preference.getPreferenceDetail() != null && oldPreferenceDetailId > preference.getPreferenceDetail().size()
                || oldPreferenceDetailId < 0) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        preference.getPreferenceDetail().remove((int) oldPreferenceDetailId);
        preference.getPreferenceDetail().add(newPreferenceDetail);
        // 创建条件对象
        Criteria criteria = Criteria.where("userId").is(userId);
        // 创建查询对象，然后将条件对象添加到其中
        Query query = new Query(criteria);
        // 创建更新对象,并设置更新的内容
        Update update = new Update().set("preferenceDetail", preference.getPreferenceDetail());
        mongoTemplate.upsert(query, update, Preference.class);
    }
}
