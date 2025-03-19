package com.github.hbq969.code.common.cache.juc;

import com.github.hbq969.code.common.cache.CacheCleanUp;
import com.github.hbq969.code.common.cache.ExpireKey;
import com.github.hbq969.code.common.cache.ExpireValue;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.FormatTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * @author : hbq969@gmail.com
 * @description : JUC缓存扩展器
 * @createTime : 15:08:47, 2023.03.31, 周五
 */
@Slf4j
public class CacheImpl extends AbstractValueAdaptingCache implements DisposableBean, CacheCleanUp {

    public final static ValueWrapper NULL = new SimpleValueWrapper(null);
    private LinkedHashMap nativeCache;

    private SpringContext context;

    private String name;

    public CacheImpl(String name, SpringContext context) {
        super(context.getBoolValue("spring.cache.ext.juc.allowNullValues", true));
        this.name = name;
        this.context = context;
        int maxCapacity = context.getIntValue("spring.cache.ext.juc.maxCapacity", 5000);
        this.nativeCache = new LinkedHashMap() {
            @Override
            protected boolean removeEldestEntry(Entry eldest) {
                return super.size() > maxCapacity;
            }
        };
    }

    public CacheImpl(SpringContext context) {
        this("default", context);
    }

    @Override
    public void cleanUp() {
        Iterator it = this.nativeCache.values().iterator();
        ExpireValue v;
        while (it.hasNext()) {
            v = (ExpireValue) it.next();
            if (v.expire()) {
                synchronized (this.nativeCache) {
                    it.remove();
                    if (log.isDebugEnabled()) {
                        log.debug("清理KEY: {}", v.getExpire());
                    }
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        this.nativeCache.clear();
        log.debug("清空spring接口缓存。");
    }

    @Nullable
    @Override
    protected Object lookup(Object key) {
        Assert.notNull(key);
        ExpireValue v = (ExpireValue) this.nativeCache.get(key);
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
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.nativeCache;
    }

    @Nullable
    @Override
    public synchronized <T> T get(Object key, Callable<T> valueLoader) {
        Assert.notNull(key);
        try {
            ExpireValue v = (ExpireValue) this.nativeCache.get(key);
            if (v == null) {
                synchronized (this.nativeCache) {
                    Object nv = valueLoader.call();
                    this.put(key, new ExpireValue((ExpireKey) key, nv));
                    return (T) nv;
                }
            }
            return (T) v.getValue();
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    @Override
    public synchronized void put(Object key, @Nullable Object value) {
        Assert.notNull(key);
        if (!this.isAllowNullValues()) {
            Assert.notNull(value);
        }
        ExpireKey expire = (ExpireKey) key;
        this.nativeCache.put(key, new ExpireValue(expire, value));
    }

    @Nullable
    @Override
    public synchronized ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        Assert.notNull(key);
        if (!this.isAllowNullValues()) {
            Assert.notNull(value);
        }
        ExpireValue v = (ExpireValue) this.nativeCache.get(key);
        if (v == null) {
            ExpireKey expire = (ExpireKey) key;
            this.nativeCache.put(key, new ExpireValue(expire, value));
            return null;
        } else {
            return new SimpleValueWrapper(v.getValue());
        }
    }

    @Override
    public synchronized void evict(Object key) {
        ExpireValue v = (ExpireValue) this.nativeCache.remove(key);
        if (v != null) {
            ExpireKey expire = v.getExpire();
            if (log.isDebugEnabled())
                log.debug("缓存键[{}]过期, 放入时间: {}, 当前时间: {}, 过期配置: ({},{})",
                        expire.getKey(), FormatTime.YYYYMMDDHHMISS.withMills(expire.getCreateTimeMills())
                        , FormatTime.YYYYMMDDHHMISS.withMills(), expire.getExpire(), expire.getUnit());
        }
    }

    @Override
    public synchronized void clear() {
        this.nativeCache.clear();
    }
}
