package com.github.hbq969.code.common.lock.spi;

import com.github.hbq969.code.common.decorde.DefaultOptionalFacade;
import com.github.hbq969.code.common.lock.LockProvider;
import com.github.hbq969.code.common.spring.context.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 分布式锁实现统一入口
 * @createTime : 2023/8/25 11:15
 */
public class LockProviderFacade extends DefaultOptionalFacade<String, LockProvider> implements
        LockProvider {

    @Autowired
    private SpringContext context;

    @Override
    public Optional<LockProvider> query() {
        return super.query(context.getProperty("bc.common.lock.spi.type", "redis"));
    }

    @Override
    public boolean tryLock(String lockKey, String requestId, long expire, TimeUnit unit) {
        return getService().tryLock(lockKey, requestId, expire, unit);
    }

    @Override
    public boolean getLock(String lockKey, String requestId, long expire, long checkTime,
                           TimeUnit unit) {
        return getService().getLock(lockKey, requestId, expire, checkTime, unit);
    }

    @Override
    public boolean getLock(String lockKey, String requestId, long expire, TimeUnit unit) {
        return getService().getLock(lockKey, requestId, expire, unit);
    }

    @Override
    public boolean releaseLock(String lockKey, String requestId) {
        return getService().releaseLock(lockKey, requestId);
    }

    @Override
    public String get(String lockKey) {
        return getService().get(lockKey);
    }

    @Override
    public RedisTemplate<Object, Object> getRedisTemplate() {
        return getService().getRedisTemplate();
    }
}
