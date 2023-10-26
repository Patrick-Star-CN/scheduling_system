package team.delete.scheduling_system.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 缓存枚举类
 *
 * @author patrick_star
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum CacheType {
    /** 永久 */
    NoExpire(100, 3000, false,-1),
    /** 一分钟存活期 */
    ExpireOneMin(100, 1000, true, 60),
    /** 验证码 */
    Captcha(100, 3000, true, 60);

    private final int initialCapacity;
    private final int maximumSize;
    private final boolean expires;
    private final int expireTime;
}
