package team.delete.scheduling_system.service;

import cn.dev33.satoken.stp.StpUtil;
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
 * @version 1.4
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
        switch (user.getType()) {
            case VICE_MANAGER:
                Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
                if (!group.getType().equals(profession.getType()) || !group.getStoreId().equals(user.getStoreId())) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                break;
            case MANAGER:
                if (!group.getStoreId().equals(user.getStoreId())) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                break;
            default :
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
        switch (user.getType()) {
            case VICE_MANAGER:
                Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
                if (profession == null) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                return groupMapper.selectGroupList(profession.getType(), user.getStoreId());
            case MANAGER:
                return groupMapper.selectGroupListByStoreId(user.getStoreId());
            default :
                throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 查询店铺某一个工种的小组列表
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
        if (user.getType() != User.Type.MANAGER && user.getType() != User.Type.VICE_MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return groupMapper.selectGroupList(type, storeId);
    }


    /**
     * 查询某门店某工种的小组列表接口（副经理）
     *
     * @param userId 操作的用户对象id
     * @return 组别信息列表
     */
    public List<Group> fetchGroupListByUserId(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        switch (user.getType()) {
            case VICE_MANAGER:
                Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), userId);
                return groupMapper.selectGroupList(profession.getType(), user.getStoreId());
            default :
                throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 查询某门店某工种的小组列表接口（经理）
     *
     * @param userId 操作的用户对象id
     * @param professionType 查询的工种
     * @return 组别信息列表
     */
    public List<Group> fetchGroupListByUserIdAndProfession(Integer userId, User.Type professionType) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        switch (user.getType()) {
            case MANAGER:
                return groupMapper.selectGroupList(professionType, user.getStoreId());
            default :
                throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
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
     * 副经理增加组别信息
     * 只能新增操作用户所属门店所属工种的组别
     *
     * @param userId  操作的用户id
     * @param managerId 新增的组别负责人id
     * @param name 新增的组别名称
     */
    public void addGroupVice(Integer userId, Integer managerId, String name) {
        if (userId == null || managerId == null || name == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        User manager = userMapper.selectById(managerId);
        if (manager == null || manager.getType() != User.Type.GROUP_MANAGER) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (user.getType() != User.Type.VICE_MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
        Group groupAdd = Group.builder()
                .storeId(user.getStoreId())
                .managerId(managerId)
                .name(name)
                .type(profession.getType()).build();
        groupMapper.insert(groupAdd);

        if(manager.getType() == User.Type.GROUP_MANAGER) {
            manager.setGroupId(groupAdd.getId());
            userMapper.updateById(manager);
        }
    }

    /**
     * 经理增加组别信息
     * 只能新增操作用户所属门店所属工种的组别
     *
     * @param userId  操作的用户id
     * @param managerId 新增的组别负责人id
     * @param type 新增的组别类型
     * @param name 新增的组别名称
     */
    public void addGroup(Integer userId, Integer managerId,User.Type type, String name) {
        if (userId == null || managerId == null ||type == null || name == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        User manager = userMapper.selectById(managerId);
        if (manager == null || (manager.getType() != User.Type.GROUP_MANAGER && manager.getType() != User.Type.MANAGER)) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (user.getType() != User.Type.MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        if (type == User.Type.MANAGER) {
            if (manager.getType() != User.Type.MANAGER) {
                throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
            }
        }
        else{
            if (manager.getType() == User.Type.MANAGER) {
                throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
            }
        }
        Group groupAdd = Group.builder()
                .storeId(user.getStoreId())
                .managerId(managerId)
                .name(name)
                .type(type).build();
        groupMapper.insert(groupAdd);

        if(manager.getType() == User.Type.GROUP_MANAGER) {
            manager.setGroupId(groupAdd.getId());
            userMapper.updateById(manager);
        }
    }
    private void checkParameter(Integer userId, Group groupAdd) {
        if (userId == null || groupAdd == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        switch (user.getType()) {
            case VICE_MANAGER:
                Profession profession = professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId());
                if (!groupAdd.getType().equals(profession.getType()) || !groupAdd.getStoreId().equals(user.getStoreId())) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                break;
            case MANAGER:
                if (!groupAdd.getStoreId().equals(user.getStoreId())) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                break;
            default :
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
        Group group = groupMapper.selectById(groupIdNeedDelete);
        Integer userDeleteId = group.getManagerId();
        User user = userMapper.selectById(userDeleteId);

        groupMapper.deleteById(groupIdNeedDelete);

        if(user.getType() == User.Type.GROUP_MANAGER) {
            user.setGroupId(null);
            userMapper.updateById(user);
        }
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
        User manager = userMapper.selectById(groupUpdate.getManagerId());
        if((manager.getType() != User.Type.GROUP_MANAGER || groupUpdate.getType() == User.Type.MANAGER) && (manager.getType() != User.Type.MANAGER || groupUpdate.getType() != User.Type.MANAGER)) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }

        Group group = groupMapper.selectById(groupUpdate.getId());
        Integer userDeleteId = group.getManagerId();
        User user = userMapper.selectById(userDeleteId);

        groupMapper.updateById(groupUpdate);

        if(manager.getType() == User.Type.GROUP_MANAGER) {
            manager.setGroupId(groupUpdate.getId());
            userMapper.updateById(manager);
        }
        if(user.getType() == User.Type.GROUP_MANAGER) {
            user.setGroupId(null);
            userMapper.updateById(user);
        }

    }
}
