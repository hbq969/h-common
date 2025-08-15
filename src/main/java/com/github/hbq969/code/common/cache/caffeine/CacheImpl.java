package com.github.hbq969.code.common.cache.caffeine;

import cn.hutool.core.lang.Assert;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.hbq969.code.common.cache.CacheCleanUp;
import com.github.hbq969.code.common.cache.ExpireKey;
import com.github.hbq969.code.common.cache.ExpireValue;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.FormatTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author : hbq969@gmail.com
 * @description : Caffeine缓存实现
 * @createTime : 18:07:11, 2023.04.01, 周六
 */
@Slf4j
public class CacheImpl extends AbstractValueAdaptingCache implements DisposableBean, CacheCleanUp {

  private String name;

  private SpringContext context;

  private Cache nativeCache;

  public CacheImpl(String name, SpringContext context) {
    super(context.getBoolValue("spring.cache.ext.caffeine.allowNullValues", true));
    this.name = name;
    this.context = context;
    int maxCapacity = context.getIntValue("spring.cache.ext.caffeine.maxCapacity", 5000);
    int initCapacity = context.getIntValue("spring.cache.ext.caffeine.initialCapacity", (int) (maxCapacity * 0.3f));
    this.nativeCache = Caffeine.newBuilder()
        .maximumSize(maxCapacity)
        .initialCapacity(initCapacity)
        .build();
    log.debug("初始化caffeine spring接口缓存");
  }

  public CacheImpl(SpringContext context) {
    this("caffeine", context);
  }

  @Override
  public void cleanUp() {
    this.nativeCache.cleanUp();
  }

  @Override
  public void destroy() throws Exception {
    nativeCache.cleanUp();
    log.debug("清理caffeine spring接口缓存...");
  }

  @Override
  protected Object lookup(Object key) {
    ExpireValue v = (ExpireValue) this.nativeCache.getIfPresent(key);
    if (Objects.isNull(v)) {
      return null;
    }
    if (v.expire()) {
      this.evict(key);
      return null;
    }
    return v.getValue();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Object getNativeCache() {
    return this.nativeCache;
  }

  @Override
  public <T> T get(Object key, Callable<T> valueLoader) {
    try {
      ExpireValue v = (ExpireValue) this.nativeCache.getIfPresent(key);
      if (Objects.isNull(v)) {
        synchronized (this.nativeCache) {
          Object nv = valueLoader.call();
          this.nativeCache.put(key, new ExpireValue((ExpireKey) key, nv));
          return (T) nv;
        }
      } else {
        return (T) v.getValue();
      }
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  @Override
  public void put(Object key, Object value) {
    if (!isAllowNullValues()) {
      Assert.notNull(value);
    }
    this.nativeCache.put(key, new ExpireValue((ExpireKey) key, value));
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    if (!isAllowNullValues()) {
      Assert.notNull(value);
    }
    synchronized (this.nativeCache) {
      ExpireValue existingValue = (ExpireValue) this.nativeCache.getIfPresent(key);
      if (Objects.isNull(existingValue)) {
        this.nativeCache.put(key, new ExpireValue((ExpireKey) key, value));
        return null;
      } else {
        return new SimpleValueWrapper(existingValue.getValue());
      }
    }
  }

  @Override
  public void evict(Object key) {
    ExpireValue v = (ExpireValue) this.nativeCache.getIfPresent(key);
    this.nativeCache.invalidate(key);
    if (Objects.nonNull(v)) {
      ExpireKey expire = v.getExpire();
      if(log.isDebugEnabled())
        log.debug("缓存键[{}]过期, 放入时间: {}, 当前时间: {}, 过期配置: ({},{})",
                expire.getKey(), FormatTime.YYYYMMDDHHMISS.withMills(expire.getCreateTimeMills())
                , FormatTime.YYYYMMDDHHMISS.withMills(), expire.getExpire(), expire.getUnit());
    }
  }

  @Override
  public void clear() {
    this.nativeCache.invalidateAll();
  }
}
