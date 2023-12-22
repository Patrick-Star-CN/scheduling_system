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
 * @author cookie1551 Patrick_Star
 * @version 1.2
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
        Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
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
        Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        if (profession == null) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return groupMapper.selectGroupList(profession.getType(), user.getStoreId());
    }

    /**
     * 查询店铺某一工种的小组列表
     *
     * @param userId 操作的用户对象id
     * @param type 操作的工种
     * @param storeId 操作的店铺id
     * @return 组别信息列表
     */
    public List<Group> fetchGroupListByTypeAndStoreId(Integer userId, User.Type type, Integer storeId) {
        if (userId == null || type == null || storeId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.MANAGER && user.getType() != User.Type.SUPER_ADMIN) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return groupMapper.selectGroupList(type, storeId);
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
     * @param userId  操作的用户id
     * @param managerId 新增的组别负责人id
     * @param name 新增的组别名称
     */
    public void addGroup(Integer userId, Integer managerId, String name) {
        if (userId == null || managerId == null || name == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        User manager = userMapper.selectById(managerId);
        if (manager == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (user.getType() != User.Type.VICE_MANAGER || manager.getType() != User.Type.GROUP_MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        Group groupAdd = Group.builder()
                .storeId(user.getStoreId())
                .managerId(managerId)
                .name(name)
                .type(profession.getType()).build();
        groupMapper.insert(groupAdd);
    }

    private void checkParameter(Integer userId, Group groupAdd) {
        if (userId == null || groupAdd == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        if (user.getType() != User.Type.VICE_MANAGER || !groupAdd.getType().equals(profession.getType()) || !groupAdd.getStoreId().equals(user.getStoreId())) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
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
        checkParameter(userId, groupUpdate);
        if (groupMapper.selectById(groupUpdate.getId()) == null) {
            throw new AppException(ErrorCode.GROUP_NOT_EXISTED);
        }
        groupMapper.updateById(groupUpdate);
    }
}
