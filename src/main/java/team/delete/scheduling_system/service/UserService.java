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
import team.delete.scheduling_system.entity.User;
import team.delete.scheduling_system.exception.AppException;
import team.delete.scheduling_system.mapper.UserMapper;

import java.util.regex.Pattern;

/**
 * @author Patrick_Star
 * @version 1.3
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
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("user_id", userId);
        updateWrapper.set("password", newPassword);
    }
}
