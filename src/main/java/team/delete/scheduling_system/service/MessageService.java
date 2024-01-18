package team.delete.scheduling_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.delete.scheduling_system.constant.ErrorCode;
import team.delete.scheduling_system.exception.AppException;

import java.util.List;

/**
 * @author Patrick_Star
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackForClassName = "RuntimeException")
@CacheConfig(cacheNames = "ExpireOneMin")
public class MessageService {
    final StringRedisTemplate stringRedisTemplate;

    /**
     * 发送消息
     *
     * @param userId  接收的用户id
     * @param message 消息内容
     */
    public void sendMessage(Integer userId, String message) {
        if (userId == null || message == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        stringRedisTemplate.opsForList().leftPush(userId + "msg", message);
    }

    /**
     * 获取消息
     *
     * @param userId 接收的用户id
     * @return 消息列表
     */
    public List<String> getMessage(Integer userId) {
        if (userId == null) {
            throw new AppException(ErrorCode.PARAM_ERROR);
        }
        return stringRedisTemplate.opsForList().range(userId + "msg", 0, -1);
    }
}
