package com.github.hbq969.code.common.cache.ehcache;

import com.github.hbq969.code.common.cache.CacheCleanUp;
import com.github.hbq969.code.common.cache.ExpireKey;
import com.github.hbq969.code.common.cache.ExpireValue;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.FormatTime;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.RegisteredEventListeners;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.ResourceUtils;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : Ehcache缓存实现
 * @createTime : 13:58:27, 2023.04.01, 周六
 */
@Slf4j
public class CacheImpl extends AbstractValueAdaptingCache implements DisposableBean, CacheCleanUp {

  private SpringContext context;

  private String name;

  private CacheManager manager;

  private Cache nativeCache;

  public CacheImpl(SpringContext context) {
    this("ehcache", context);
  }

  public CacheImpl(String name, SpringContext context) {
    super(context.getBoolValue("spring.cache.ext.ehcache.allowNullValues", true));
    this.name = name;
    this.context = context;
    String path = context.getProperty("spring.cache.ext.ehcache.xml-config-file", "classpath:ehcache.xml");
    String cacheName = context.getProperty("spring.cache.ext.ehcache.name", "ehcache");
    URL url = null;
    try {
      url = ResourceUtils.getURL(path);
      manager = CacheManager.create(url);
      nativeCache = manager.getCache(cacheName);
      RegisteredEventListeners listeners = nativeCache.getCacheEventNotificationService();
      listeners.registerListener(new EventListener());
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    log.debug("初始化ehcache spring接口缓存");
  }

  @Override
  public void cleanUp() {
    this.nativeCache.evictExpiredElements();
  }

  @Override
  public void destroy() throws Exception {
    this.manager.shutdown();
    log.debug("ehcache spring接口缓存安全退出...");
  }

  @Override
  protected Object lookup(Object key) {
    Element element = this.nativeCache.get(key);
    if (Objects.isNull(element)) {
      return null;
    }
    ExpireValue v = (ExpireValue) element.getObjectValue();
    if (v.expire()) {
      evict(key);
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
      Element element = this.nativeCache.get(key);
      if (Objects.isNull(element)) {
        synchronized (this.nativeCache) {
          Object nv = valueLoader.call();
          this.put(key, new ExpireValue((ExpireKey) key, nv));
          return (T) nv;
        }
      } else {
        ExpireValue v = (ExpireValue) element.getObjectValue();
        return (T) v.getValue();
      }
    } catch (Exception e) {
      log.error("", e);
      return null;
    }
  }

  @Override
  public void put(Object key, Object value) {
    ExpireKey expire = (ExpireKey) key;
    int timeToLiveSeconds = (int) TimeUnit.SECONDS.convert(expire.getExpire(), expire.getUnit());
    this.nativeCache.put(new Element(key, new ExpireValue(expire, value), 5, timeToLiveSeconds));
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    ExpireKey expire = (ExpireKey) key;
    int timeToLiveSeconds = (int) TimeUnit.SECONDS.convert(expire.getExpire(), expire.getUnit());
    Element result = this.nativeCache.putIfAbsent(new Element(key, new ExpireValue(expire, value), 5, timeToLiveSeconds));
    if (result == null) {
      return null;
    } else {
      ExpireValue v = (ExpireValue) result.getObjectValue();
      return v == null ? null : new SimpleValueWrapper(v.getValue());
    }
  }

  @Override
  public void evict(Object key) {
    Element element = this.nativeCache.get(key);
    this.nativeCache.remove(key);
    if (Objects.nonNull(element)) {
      ExpireValue v = (ExpireValue) element.getObjectValue();
      ExpireKey expire = v.getExpire();
      if(log.isDebugEnabled())
        log.debug("缓存键[{}]过期, 放入时间: {}, 当前时间: {}, 过期配置: ({},{})",
                expire.getKey(), FormatTime.YYYYMMDDHHMISS.withMills(expire.getCreateTimeMills())
                , FormatTime.YYYYMMDDHHMISS.withMills(), expire.getExpire(), expire.getUnit());
    }
  }

  @Override
  public void clear() {
    this.nativeCache.removeAll();
  }
}
