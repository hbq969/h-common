package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.cache.CacheCleaner;
import com.github.hbq969.code.common.cache.CacheConfig;
import com.github.hbq969.code.common.cache.generator.ApiKeyGenerator;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存自动装配配置类
 * @createTime : 19:51:28, 2023.04.03, 周一
 */
@Slf4j
public class CacheAutoConfiguration {

//    @ConditionalOnExpression("${spring.cache.ext.enabled:true}")
//    @Bean("common-cacheConfig")
//    @Lazy
//    CacheConfig cacheConfig() {
//        return new CacheConfig();
//    }

    @Bean("common-cacheConfig")
    @Primary
    @ConditionalOnExpression("${spring.cache.ext.enabled:true}")
    public CacheManager cacheManager(SpringContextProperties conf, SpringContext context) throws Exception {
        SimpleCacheManager cacheManager = new SimpleCacheManager();

        List<Cache> caches = new ArrayList<>();
        if (conf.getCache().getExt().useJuc()) {
            caches.add(new com.github.hbq969.code.common.cache.juc.CacheImpl(context));
        }
        if (conf.getCache().getExt().useGuava()) {
            caches.add(new com.github.hbq969.code.common.cache.guava.CacheImpl(context));
        }
        if (conf.getCache().getExt().useEhcache()) {
            caches.add(new com.github.hbq969.code.common.cache.ehcache.CacheImpl(context));
        }
        if (conf.getCache().getExt().useCaffeine()) {
            caches.add(new com.github.hbq969.code.common.cache.caffeine.CacheImpl(context));
        }

        cacheManager.setCaches(caches);
        cacheManager.afterPropertiesSet();
        return cacheManager;
    }

    @Bean("common-CacheCleaner")
    @ConditionalOnExpression("${spring.cache.ext.enabled:true}")
    CacheCleaner cacheCleaner(){
        return new CacheCleaner();
    }

    @ConditionalOnExpression("${spring.cache.ext.enabled:true}")
    @Bean("apiKeyGenerator")
    @Primary
    ApiKeyGenerator apiKeyGenerator() {
        return new ApiKeyGenerator();
    }
}
