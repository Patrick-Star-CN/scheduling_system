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
import team.delete.scheduling_system.entity.Store;
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Patrick_Star cookie1551
 * @version 1.4
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class UserService {
    final UserMapper userMapper;

    public void login(String username, String password) {
        if (username == null || password == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        if (!user.getPassword().equals(password)) {
            throw new AppException(ErrorCode.PASSWORD_ERROR);
        }
        StpUtil.login(user.getUserId());
    }

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
     * 判断权限
     *
     * @param user 需要判断权限的用户对象
     */
    public void judgePermission(User user) {
        if (user == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (user.getType() != User.Type.SUPER_ADMIN || user.getType() != User.Type.MANAGER || user.getType() != User.Type.VICE_MANAGER) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
    }

    /**
     * 查询所有用户
     *
     * @param user     操作的用户
     * @param typeArea 查询的范围（1：门店；2：工种；3：小组）
     * @param typeId   查询的类别号
     * @return 用户信息列表
     */
    public List<User> fetchAllUser(User user, Integer typeArea, Integer typeId) {
        if (user == null || typeArea == null || typeId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        switch (typeArea) {
            case 1:
                if (user.getType() != User.Type.SUPER_ADMIN && !(user.getType() == User.Type.MANAGER && user.getStoreId().equals(typeId))) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                return userMapper.selectUserListByStoreId(typeId);
            case 2:
                if (user.getType() != User.Type.SUPER_ADMIN && !(user.getType() == User.Type.MANAGER && user.getStoreId().equals(typeId)) && !(user.getType() == User.Type.VICE_MANAGER && user.getType().equals(typeId))) {
                    throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
                }
                return userMapper.selectUserListByProfession(typeId);
            case 3:
                if (user.getType() != User.Type.SUPER_ADMIN && !(user.getType() == User.Type.MANAGER && user.getStoreId().equals(typeId)) && !(user.getType() == User.Type.VICE_MANAGER && user.getType().equals(typeId)) && !(user.getType() == User.Type.GROUP_MANAGER && user.getGroupId().equals(typeId))) {
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
     * 增加用户信息
     *
     * @param userAdmin 操作的用户
     * @param userAdd   新增的用户对象
     */
    public void addUser(User userAdmin, User userAdd) {
        if (userAdmin == null || userAdd == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userAdmin.getType() != User.Type.SUPER_ADMIN
                && !(userAdmin.getType() == User.Type.MANAGER && userAdmin.getStoreId().equals(userAdd.getStoreId()))
                && !(userAdmin.getType() == User.Type.VICE_MANAGER && userAdmin.getType().equals(userAdd.getType()))
                && !(userAdmin.getType() == User.Type.GROUP_MANAGER && userAdmin.getGroupId().equals(userAdd.getGroupId()))) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        userMapper.insert(userAdd);
    }

    /**
     * 删除用户信息
     *
     * @param user   操作的用户
     * @param userId 删除的用户对象id
     */
    public void deleteUser(User user, Integer userId) {
        if (user == null || userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (user.getType() != User.Type.SUPER_ADMIN
                && (user.getType() != User.Type.MANAGER && user.getStoreId().equals(fetchUserByUserId(userId).getStoreId()))
                && (user.getType() != User.Type.VICE_MANAGER && user.getType().equals(fetchUserByUserId(userId).getType()))
                && (user.getType() != User.Type.GROUP_MANAGER && user.getGroupId().equals(fetchUserByUserId(userId).getGroupId()))) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        userMapper.deleteById(userId);
    }

    /**
     * 更新用户信息
     *
     * @param userAdmin 操作的用户
     * @param userUpdate   更新的用户对象
     */
    public void updateUser(User userAdmin, User userUpdate) {
        if (userAdmin == null || userUpdate == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        if (userAdmin.getType() != User.Type.SUPER_ADMIN
                && !(userAdmin.getType() == User.Type.MANAGER && userAdmin.getStoreId().equals(userUpdate.getStoreId()))
                && !(userAdmin.getType() == User.Type.VICE_MANAGER && userAdmin.getType().equals(userUpdate.getType()))
                && !(userAdmin.getType() == User.Type.GROUP_MANAGER && userAdmin.getGroupId().equals(userUpdate.getGroupId()))) {
            throw new AppException(ErrorCode.USER_PERMISSION_ERROR);
        }
        if(fetchUserByUserId(userUpdate.getUserId()) == null){
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        userMapper.updateById(userUpdate);
    }
}
