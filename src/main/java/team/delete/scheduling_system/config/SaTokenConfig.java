package team.delete.scheduling_system.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 接口路径拦截器配置类
 *
 * @author Patrick_Star
 * @version 1.0
 */
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    /**
     * 注册Sa-Token的注解拦截器，打开注解式鉴权功能
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册注解拦截器，并排除不需要注解鉴权的接口地址 (与登录拦截器无关)
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 根据路由划分模块，不同模块不同鉴权
            SaRouter.match("/api/**", StpUtil::checkLogin);
        })).addPathPatterns("/api/**")
           .excludePathPatterns("/api/user/login/**",
                                "/api/user/register",
                                "/api/user/test/login/**");

        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }
}
