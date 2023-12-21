package team.delete.scheduling_system.config;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import team.delete.scheduling_system.interceptor.LogInterceptor;

/**
 * 自定义 WebMvc 配置
 *
 * @author Patrick_Star
 * @version 1.0
 */
@Configuration
@AllArgsConstructor
public class CustomWebMvcConfig implements WebMvcConfigurer {
    private final LogInterceptor logInterceptor;

    /**
     * 添加拦截器
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        WebMvcConfigurer.super.addInterceptors(registry);
        registry.addInterceptor(logInterceptor).addPathPatterns("/**");
    }
}