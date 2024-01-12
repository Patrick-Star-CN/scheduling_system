package team.delete.scheduling_system.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.system.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.constant.RegexPattern;
import team.delete.scheduling_system.dto.UserInsertDto;
import team.delete.scheduling_system.entity.Profession;
import team.delete.scheduling_system.dto.UserDto;
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.ProfessionMapper;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Patrick_Star cookie1551
 * @version 1.6
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class UserService {
    final UserMapper userMapper;
    final ProfessionMapper professionMapper;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     */
    public User.Type login(String username, String password) {
        if (username == null || password == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!user.getPassword().equals(password)) {
            throw new AppException(ErrorCode.PASSWORD_ERROR);
        }
        StpUtil.login(user.getUserId());
        return user.getType();
    }

    /**
     * 修改密码
     *
     * @param userId      用户id
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void changePassword(Integer userId, String oldPassword, String newPassword) {
        if (userId == null || oldPassword == null || newPassword == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (!Pattern.matches(RegexPattern.PASSWORD, newPassword)) {
            throw new AppException(ErrorCode.PASSWORD_FORMAT_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!user.getPassword().equals(oldPassword)) {
            throw new AppException(ErrorCode.PASSWORD_ERROR);
        }
        user.setPassword(newPassword);
        userMapper.updateById(user);
    }

    /**
     * 查询所有用户
     *
     * @param userid   操作的用户id
     * @param typeArea 查询的范围（1：门店；2：工种；3：小组）
     * @param typeId   查询的类别号
     * @return 用户信息列表
     */
    public List<User> fetchAllUser(Integer userid, Integer typeArea, Integer typeId) {
        if (userid == null || typeArea == null || typeId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userid);
        switch (typeArea) {
            case 1:
                if (user.getType() != User.Type.SUPER_ADMIN
                        && !(user.getType() == User.Type.MANAGER && user.getStoreId().equals(typeId))) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                return userMapper.selectUserListByStoreId(typeId);
            case 2:
                if (user.getType() != User.Type.SUPER_ADMIN
                        && !(user.getType() == User.Type.MANAGER && user.getStoreId().equals(typeId))
                        && !(user.getType() == User.Type.VICE_MANAGER && (professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId())).getId().equals(typeId))) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                return userMapper.selectUserListByProfession(typeId);
            case 3:
                if (user.getType() != User.Type.SUPER_ADMIN
                        && !(user.getType() == User.Type.MANAGER && user.getStoreId().equals(typeId))
                        && !(user.getType() == User.Type.VICE_MANAGER && (professionMapper.selectProfessionByStoreIdAndManagerId(user.getStoreId(), user.getUserId())).getId().equals(typeId))
                        && !(user.getType() == User.Type.GROUP_MANAGER && user.getGroupId().equals(typeId))) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                return userMapper.selectUserListByGroupId(typeId);
        }
        return userMapper.selectList(null);
    }

    /**
     * 查询用户信息
     *
     * @param userId 查询的用户对象id
     * @return 用户信息列表
     */
    public User fetchUserByUserId(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        return userMapper.selectById(userId);
    }

    /**
     * 查询用户信息
     *
     * @param userId 查询的用户对象id
     * @return 用户信息列表
     */
    public UserDto fetchUserDtoByUserId(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        return userMapper.selectUserByUserId(userId);
    }

    /**
     * 增加用户信息
     *
     * @param userId  操作的用户id
     * @param userAdd 新增的用户对象
     */
    public void addUser(Integer userId, UserInsertDto userAdd) {
        if (userId == null || userAdd == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", userAdd.getName());
        if (userMapper.selectOne(queryWrapper) != null) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }
        User build = User.builder().name(userAdd.getName())
                .groupId(userAdd.getGroupId())
                .storeId(userAdd.getStoreId())
                .password(userAdd.getName() + "123")
                .type(userAdd.getType()).build();
        checkPermission(userId, build);
        userMapper.insert(build);
    }

    /**
     * 判断权限
     *
     * @param userId         操作的用户id
     * @param userNeedChange 改动的用户对象
     */
    private void checkPermission(Integer userId, User userNeedChange) {
        if (userId == null || userNeedChange == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User userAdmin = userMapper.selectById(userId);
        if (userAdmin.getType() != User.Type.SUPER_ADMIN
                && !(userAdmin.getType() == User.Type.MANAGER
                && userAdmin.getStoreId().equals(userNeedChange.getStoreId()))
                && !(userAdmin.getType() == User.Type.VICE_MANAGER
                && userAdmin.getType().equals(userNeedChange.getType()))
                && !(userAdmin.getType() == User.Type.GROUP_MANAGER
                && userAdmin.getGroupId().equals(userNeedChange.getGroupId()))) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 删除用户信息
     *
     * @param userId 操作的用户id
     * @param userIdNeedDelete 删除的用户对象id
     */
    public void deleteUser(Integer userId, Integer userIdNeedDelete) {
        if (userId == null || userIdNeedDelete == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.SUPER_ADMIN
                && (user.getType() != User.Type.MANAGER
                && user.getStoreId().equals(fetchUserByUserId(userId).getStoreId()))
                && (user.getType() != User.Type.VICE_MANAGER
                && user.getType().equals(fetchUserByUserId(userId).getType()))
                && (user.getType() != User.Type.GROUP_MANAGER
                && user.getGroupId().equals(fetchUserByUserId(userId).getGroupId()))) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        userMapper.deleteById(userIdNeedDelete);
    }

    /**
     * 更新用户信息
     *
     * @param userId     操作的用户id
     * @param userUpdate 更新的用户对象
     */
    public void updateUser(Integer userId, User userUpdate) {
        checkPermission(userId, userUpdate);
        if (userMapper.selectById(userUpdate.getUserId()) == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userMapper.updateById(userUpdate);
    }


    /**
     * 查询可换班对象
     *
     * @param userId   操作的用户对象id
     * @return 职位信息列表
     */
    public List<String> fetchUserShift(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() == User.Type.GROUP_MANAGER) {
            return userMapper.selectUserListByUserIdStoreIdAndGroupType(userId, user.getStoreId(), userMapper.selectGroupTypeByUserId(userId));
        }
        else if (user.getType() == User.Type.CASHIER || user.getType() == User.Type.STORAGE || user.getType() == User.Type.CUSTOMER_SERVICE) {
            return userMapper.selectUserListByStoreIdAndUserType(user.getStoreId(), user.getType());
        }
        else{
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 查询可换班对象
     *
     * @param userId   操作的用户对象id
     * @return 职位信息列表
     */
    public List<UserDto> fetchUserByGroup(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        User user = userMapper.selectById(userId);
        if (user.getType() != User.Type.GROUP_MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        return userMapper.selectUserListByGroup(userId, user.getGroupId());
    }
}
