package com.github.hbq969.code.common.lock;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 锁规范接口
 * @createTime : 2023/8/25 11:09
 */
public interface LockProvider {

    /**
     * 获取分布式锁
     *
     * @param lockKey   锁KEY
     * @param requestId 唯一ID
     * @param expire    锁失效时间值
     * @param unit      锁失效时间单位
     * @return
     */
    boolean tryLock(String lockKey, String requestId, long expire, TimeUnit unit);

    /**
     * 阻塞方式获取锁
     *
     * @param lockKey
     * @param requestId
     * @param expire
     * @param checkTime
     * @param unit
     * @return
     */
    boolean getLock(String lockKey, String requestId, long expire, long checkTime, TimeUnit unit);

    /**
     * 阻塞方式获取锁
     *
     * @param lockKey
     * @param requestId
     * @param expire
     * @param unit
     * @return
     */
    boolean getLock(String lockKey, String requestId, long expire, TimeUnit unit);

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁KEY
     * @param requestId 唯一ID
     * @return
     */
    boolean releaseLock(String lockKey, String requestId);

    /**
     * 获取锁的值
     *
     * @param lockKey
     * @return
     */
    String get(String lockKey);

    RedisTemplate<Object, Object> getRedisTemplate();
}
