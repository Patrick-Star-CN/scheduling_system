package team.delete.scheduling_system.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.delete.scheduling_system.injector.EasySqlInjector;

import java.util.Collections;

/**
 * MybatisPlus 配置类
 *
 * @author Patrick_Star
 * @version 1.0
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 自定义注入器
     *
     * @return EasySqlInjector
     */
    @Bean
    public EasySqlInjector easySqlInjector() {
        return new EasySqlInjector();
    }

    /**
     * 全局配置
     *
     * @return GlobalConfig
     */
    @Bean
    public GlobalConfig globalConfiguration() {
        GlobalConfig conf = new GlobalConfig();
        // 自定义的注入需要在这里进行配置
        conf.setSqlInjector(easySqlInjector());
        return conf;
    }

    /**
     * 分页插件
     */
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置最大单页限制数量，默认 500 条，-1 不受限制
        paginationInterceptor.setMaxLimit(20L);
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 开启 count 的 join 优化,只针对部分 left join
        paginationInterceptor.setOptimizeJoin(true);
        return paginationInterceptor;
    }

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.setInterceptors(Collections.singletonList(paginationInnerInterceptor()));
        return interceptor;
    }
}
