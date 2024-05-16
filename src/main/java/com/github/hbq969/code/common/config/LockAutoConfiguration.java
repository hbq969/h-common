package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.lock.spi.LockProviderFacade;
import com.github.hbq969.code.common.lock.spi.redis.LockRedisAspect;
import com.github.hbq969.code.common.lock.spi.redis.LockRedisProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;

/**
 * @author : hbq969@gmail.com
 * @description : 分布式锁自动装配
 * @createTime : 2023/8/25 14:25
 */
public class LockAutoConfiguration {

    @ConditionalOnExpression("${lock.spi.redis.enabled:false}")
    @Bean("lock-provider-redis")
    LockRedisProvider lockRedisProvider() {
        return new LockRedisProvider();
    }

    @Bean("lock-facade")
    LockProviderFacade facade() {
        return new LockProviderFacade();
    }

    @ConditionalOnExpression("${lock.spi.redis.enabled:false}")
    @Bean("lock-aspect")
    LockRedisAspect lockRedisAspect() {
        return new LockRedisAspect();
    }
}
