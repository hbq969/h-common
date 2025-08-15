package com.github.hbq969.code.common.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collection;

@Slf4j
public class CacheCleaner implements DisposableBean {
    @Autowired
    private CacheManager cacheManager;
    @Value("${spring.cache.ext.clean.enabled:true}")
    private boolean enableCleanUp;

    @Override
    public void destroy() throws Exception {
        Collection<String> names = cacheManager.getCacheNames();
        names.forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache instanceof DisposableBean) {
                try {
                    ((DisposableBean) cache).destroy();
                } catch (Exception e) {
                    log.error("销毁缓存异常", e);
                }
            }
        });
    }

    @Scheduled(cron = "${spring.cache.ext.clean.cron:*/5 * * * * *}")
    public void cleanUp() {
        if (enableCleanUp) {
            Collection<String> names = cacheManager.getCacheNames();
            names.forEach(name -> {
                Cache cache = cacheManager.getCache(name);
                if (cache instanceof CacheCleanUp) {
                    try {
                        ((CacheCleanUp) cache).cleanUp();
                    } catch (Exception e) {
                        log.error("清理缓存过期内容异常", e);
                    }
                }
            });
        }
    }
}
