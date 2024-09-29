package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.cache.CacheConfig;
import com.github.hbq969.code.common.cache.generator.ApiKeyGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存自动装配配置类
 * @createTime : 19:51:28, 2023.04.03, 周一
 */
@Slf4j
public class CacheAutoConfiguration {

    @ConditionalOnExpression("${spring.cache.ext.enabled:true}")
    @Bean("common-cacheConfig")
    CacheConfig cacheConfig() {
        return new CacheConfig();
    }

    @ConditionalOnExpression("${spring.cache.ext.enabled:true}")
    @Bean("apiKeyGenerator")
    @Primary
    ApiKeyGenerator apiKeyGenerator() {
        return new ApiKeyGenerator();
    }
}
