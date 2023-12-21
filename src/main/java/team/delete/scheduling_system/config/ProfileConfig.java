package team.delete.scheduling_system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * Profile 配置类
 *
 * @author Patrick_Star
 * @version 1.0
 */
@Configuration
@RequiredArgsConstructor
public class ProfileConfig {

    private final ApplicationContext context;

    /**
     * 获取当前激活的 Profile
     * @return 当前激活的 Profile
     */
    public String getActiveProfile() {
        return context.getEnvironment().getActiveProfiles()[0];
    }

    /**
     * 判断当前是否为开发环境
     * @return 是否为开发环境
     */
    public boolean isDev() {
        String activeProfile = getActiveProfile();
        return "dev".equals(activeProfile) || "debug".equals(activeProfile);
    }
}