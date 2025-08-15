package com.github.hbq969.code.common.cache.guava;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.cache.CacheCleanUp;
import com.github.hbq969.code.common.cache.ExpireKey;
import com.github.hbq969.code.common.cache.ExpireValue;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.FormatTime;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author : hbq969@gmail.com
 * @description : guava缓存实现
 * @createTime : 15:16:15, 2023.03.31, 周五
 */
@Slf4j
public class CacheImpl extends AbstractValueAdaptingCache implements DisposableBean, CacheCleanUp {

  private Cache<Object, Object> nativeCache;

  private SpringContext context;

  private String name;

  public CacheImpl(SpringContext context) {
    this("guava", context);
  }

  public CacheImpl(String name, SpringContext context) {
    super(context.getBoolValue("spring.cache.ext.guava.allowNullValues", true));
    this.name = name;
    this.context = context;
    int maxCapacity = context.getIntValue("spring.cache.ext.guava.maxCapacity", 5000);
    int initialCapacity = context.getIntValue("spring.cache.ext.guava.initCapacity", (int) (maxCapacity * 0.3f));
    int concurrencyLevel = context.getIntValue("spring.cache.ext.guava.concurrencyLevel", 4);
    this.nativeCache = CacheBuilder.newBuilder()
        .maximumSize(maxCapacity)
        .initialCapacity(initialCapacity)
        .concurrencyLevel(concurrencyLevel)
        .build();
    log.debug("初始化guava spring接口缓存");
  }

  @Override
  public void cleanUp() {
    this.nativeCache.cleanUp();
  }

  @Override
  public void destroy() throws Exception {
    this.nativeCache.cleanUp();
    log.debug("清理guava spring接口缓存...");
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  protected Object lookup(Object key) {
    ExpireKey expire = (ExpireKey) key;
    ExpireValue v = (ExpireValue) this.nativeCache.getIfPresent(expire);
    if (Objects.isNull(v)) {
      return null;
    }
    if (v.expire()) {
      evict(key);
      return null;
    }
    return v.getValue();
  }

  @Override
  public Object getNativeCache() {
    return this.nativeCache;
  }

  @Override
  public <T> T get(Object key, Callable<T> loader) {
    try {
      ExpireValue v = (ExpireValue) this.nativeCache.getIfPresent(key);
      if (Objects.isNull(v)) {
        synchronized (this.nativeCache) {
          Object nv = loader.call();
          this.put(key, new ExpireValue((ExpireKey) key, nv));
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
  public void put(Object key, Object val) {
    if (!isAllowNullValues()) {
      Assert.notNull(val);
    }
    this.nativeCache.put(key, new ExpireValue((ExpireKey) key, val));
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object val) {
    if (!isAllowNullValues()) {
      Assert.notNull(val);
    }
    synchronized (this.nativeCache) {
      ExpireValue existingValue = (ExpireValue) this.nativeCache.getIfPresent(key);
      if (Objects.isNull(existingValue)) {
        this.nativeCache.put(key, new ExpireValue((ExpireKey) key, val));
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
      this.nativeCache.invalidate(key);
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
