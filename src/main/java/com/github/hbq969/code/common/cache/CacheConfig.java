package com.github.hbq969.code.common.cache;

import com.github.hbq969.code.common.cache.config.CacheProperties;
import com.github.hbq969.code.common.cache.juc.CacheImpl;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.Data;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 缓存管理配置类
 * @createTime : 15:08:47, 2023.03.31, 周五
 */
@Data
public class CacheConfig implements FactoryBean<CacheManager>, DisposableBean {

  @Autowired
  private CacheProperties properties;

  @Autowired
  private SpringContext context;

  private Collection<DisposableBean> c1 = new ArrayList<>();

  private Collection<CacheCleanUp> c2 = new ArrayList<>();

  @Value("${spring.cache.ext.clean.enable:true}")
  private boolean enableCleanUp;

  @Override
  public void destroy() throws Exception {
    c1.forEach(cache -> {
      try {
        cache.destroy();
      } catch (Exception e) {
      }
    });
  }

  protected Collection<Cache> buildCaches()
      throws Exception {

    List c = new ArrayList();

    if (properties.useJuc()) {
      c.add(new CacheImpl(context));
    }
    if (properties.useGuava()) {
      c.add(new com.github.hbq969.code.common.cache.guava.CacheImpl(context));
    }
    if (properties.useEhcache()) {
      c.add(new com.github.hbq969.code.common.cache.ehcache.CacheImpl(context));
    }
    if (properties.useCaffeine()) {
      c.add(new com.github.hbq969.code.common.cache.caffeine.CacheImpl(context));
    }

    c.forEach(cache -> {
      c1.add((DisposableBean) cache);
      c2.add((CacheCleanUp) cache);
    });

    return c;
  }

  @Nullable
  @Override
  public CacheManager getObject()
      throws Exception {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(buildCaches());
    cacheManager.afterPropertiesSet();
    return cacheManager;
  }

  @Nullable
  @Override
  public Class<?> getObjectType() {
    return CacheManager.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Scheduled(cron = "${spring.cache.ext.clean.cron:*/5 * * * * *}")
  public void cleanUp() {
    if (enableCleanUp) {
      c2.forEach(c -> {
        try {
          c.cleanUp();
        } catch (Exception e) {
        }
      });
    }
  }
}
