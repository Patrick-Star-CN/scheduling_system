package team.delete.scheduling_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.entity.Group;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.GroupMapper;
import team.delete.scheduling_system.mapper.ProfessionMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.List;

/**
 * @author cookie1551
 * @version 1.1
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class GroupService {
    final GroupMapper groupMapper;
    final UserMapper userMapper;
    final ProfessionMapper professionMapper;

    /**
     * 判断权限
     *
     * @param userId 需要判断权限的用户对象id
     * @param groupId 操作的组别对象id
     */
    public void judgePermission(Integer userId, Integer groupId) {
        if (userId == null || groupId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        Group group = groupMapper.selectById(groupId);
        Profession profession = professionMapper.selectProfessionListByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        if (user.getType() != User.Type.VICE_MANAGER || !group.getType().equals(profession.getType()) || !group.getStoreId().equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 查询操作用户主管的所有组别
     *
     * @param userId   操作的用户对象id
     * @return 组别信息列表
     */
    public List<Group> fetchAllGroup(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.VICE_MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        Profession profession = professionMapper.selectProfessionListByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        return groupMapper.selectGroupList(profession.getType(), user.getStoreId());
    }

    /**
     * 查询组别信息
     *
     * @param userId 查询的用户对象id
     * @param groupId 查询的组别对象id
     * @return 组别信息列表
     */
    public Group fetchGroupByGroupId(Integer userId, Integer groupId) {
        judgePermission(userId, groupId);
        return groupMapper.selectById(groupId);
    }

    /**
     * 增加组别信息
     * 只能新增操作用户所属门店所属工种的组别
     *
     * @param userId  操作的用户对象id
     * @param groupAdd 新增的组别对象
     */
    public void addGroup(Integer userId, Group groupAdd) {
        if (userId == null || groupAdd == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        Profession profession = professionMapper.selectProfessionListByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        if (user.getType() != User.Type.VICE_MANAGER || !groupAdd.getType().equals(profession.getType()) || !groupAdd.getStoreId().equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        groupMapper.insert(groupAdd);
    }

    /**
     * 删除组别信息
     *
     * @param userId 操作的用户id
     * @param groupIdNeedDelete 删除的组别对象id
     */
    public void deleteGroup(Integer userId, Integer groupIdNeedDelete) {
        judgePermission(userId, groupIdNeedDelete);
        groupMapper.deleteById(groupIdNeedDelete);
    }

    /**
     * 更新组别信息
     *
     * @param userId     操作的用户id
     * @param groupUpdate 更新的组别对象
     */
    public void updateGroup(Integer userId, Group groupUpdate) {
        if (userId == null || groupUpdate == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        Profession profession = professionMapper.selectProfessionListByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        if (user.getType() != User.Type.VICE_MANAGER || !groupUpdate.getType().equals(profession.getType()) || !groupUpdate.getStoreId().equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        if (groupMapper.selectById(groupUpdate.getId()) == null) {
            throw new AppException(ErrorCode.GROUP_NOT_EXISTED);
        }
        groupMapper.updateById(groupUpdate);
    }
}
