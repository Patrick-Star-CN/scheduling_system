package team.delete.scheduling_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.ProfessionMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.List;

/**
 * @author cookie1551
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class ProfessionService{
    final ProfessionMapper professionMapper;
    final UserMapper userMapper;

    /**
     * 判断权限
     *
     * @param userId 需要判断权限的用户对象id
     * @param professionId 操作的职位对象id
     */
    public void judgePermission(Integer userId, Integer professionId) {
        if (userId == null || professionId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        Profession profession = professionMapper.selectById(professionId);
        if (user.getType() != User.Type.MANAGER || !profession.getStoreId().equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 查询操作用户主管的所有职位
     *
     * @param userId   操作的用户对象id
     * @return 职位信息列表
     */
    public List<Profession> fetchAllProfession(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return professionMapper.selectProfessionListByStoreId(user.getStoreId());
    }

    /**
     * 查询职位信息
     *
     * @param userId 查询的用户对象id
     * @param professionId 查询的职位对象id
     * @return 职位信息列表
     */
    public Profession fetchProfessionByProfessionId(Integer userId, Integer professionId) {
        judgePermission(userId, professionId);
        return professionMapper.selectById(professionId);
    }

    /**
     * 查询职位信息
     *
     * @param userId 查询的用户对象id
     * @param storeId 查询的门店对象id
     * @param managerId 查询的主管对象id
     * @return 职位信息列表
     */
    public Profession fetchProfessionByStoreIdAndManagerId(Integer userId, Integer storeId, Integer managerId) {
        if (userId == null || storeId == null || managerId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.MANAGER || !storeId.equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return professionMapper.selectProfessionListByStoreIdAndManagerId(storeId, managerId);
    }

    /**
     * 增加职位信息
     * 只能新增操作用户所属门店的职位
     *
     * @param userId  操作的用户对象id
     * @param professionAdd 新增的职位对象
     */
    public void addProfession(Integer userId, Profession professionAdd) {
        if (userId == null || professionAdd == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.MANAGER || !professionAdd.getStoreId().equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        professionMapper.insert(professionAdd);
    }

    /**
     * 删除职位信息
     *
     * @param userId 操作的用户id
     * @param professionIdNeedDelete 删除的职位对象id
     */
    public void deleteProfession(Integer userId, Integer professionIdNeedDelete) {
        judgePermission(userId, professionIdNeedDelete);
        professionMapper.deleteById(professionIdNeedDelete);
    }

    /**
     * 更新职位信息
     *
     * @param userId     操作的用户id
     * @param professionUpdate 更新的职位对象
     */
    public void updateProfession(Integer userId, Profession professionUpdate) {
        judgePermission(userId, professionUpdate.getId());
        if (professionMapper.selectById(professionUpdate.getId()) == null) {
            throw new AppException(ErrorCode.Profession_NOT_EXISTED);
        }
        professionMapper.updateById(professionUpdate);
    }
}
