package team.delete.scheduling_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

/**
 * Validator 配置类
 *
 * @author Patrick_Star
 * @version 1.0
 */
@Configuration
public class ValidatorConfig {

    /**
     * 配置方法参数校验
     * @return MethodValidationPostProcessor
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor(){
        return new MethodValidationPostProcessor();
    }
}