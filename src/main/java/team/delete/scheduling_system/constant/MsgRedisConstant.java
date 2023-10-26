package team.delete.scheduling_system.constant;


import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 消息模块 Redis 缓存常量类
 *
 * @author patrick_star
 * @version 1.0
 */
@Component
public class MsgRedisConstant {
    /**
     * 用户接收消息队列
     */
    public static final String USER_MSG_QUEUE = "u:{%d}:m:in";
    /**
     * 消息
     */
    public static final String MSG = "msg:{%s}";
    /**
     * 用户已读消息
     */
    public static final String USER_READ_MSG = "u:{%d}:m:read";
    /**
     * 用户未读消息
     */
    public static final String USER_UNREAD_MSG = "u:{%d}:m:ur";
    /**
     * 用户发送消息
     */
    public static final String USER_SENT_MSG = "u:{%d}:m:out";
    /**
     * 用户消息队列长度
     */
    public static final String USER_MSG_QUEUE_COUNT = "u:{%d}:m:in:ct";
    /**
     * 用户未读队列长度
     */
    public static final String USER_UNREAD_MSG_COUNT = "u:{%d}:m:ur:ct";

    public static final List<Object> MESSAGE_OUTLINE_KEYS = Arrays.asList("sendTime", "title", "senderName");

    public String getKeyUserMsgQueue(int receiverId) {
        return String.format(USER_MSG_QUEUE, receiverId);
    }

    public String getKeyMsg(String msgId) {
        return String.format(MSG, msgId);
    }

    public String getKeyUserMsgRead(int userId) {
        return String.format(USER_READ_MSG, userId);
    }

    public String getKeyUserMsgUnRead(int userId) {
        return String.format(USER_UNREAD_MSG, userId);
    }

    public String getKeyUserMsgSent(int userId) {
        return String.format(USER_SENT_MSG, userId);
    }

    public String getKeyUserMsgUnReadCount(int userId) {
        return String.format(USER_UNREAD_MSG_COUNT, userId);
    }

    public String getKeyUserMsgQueueCount(int receiverId) {
        return String.format(USER_MSG_QUEUE_COUNT, receiverId);
    }

    public static final String USER_TOPIC_SET = "u:{%d}:tp";

    public String getKeyUserTopicSet(int userId) {
        return String.format(USER_TOPIC_SET, userId);
    }

}
