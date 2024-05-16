package com.github.hbq969.code.common.lock.spi.redis;

import com.github.hbq969.code.common.lock.LockProvider;
import com.github.hbq969.code.common.lock.annotation.DistLock;
import com.github.hbq969.code.common.utils.StrUtils;
import com.github.hbq969.code.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.AnnotationUtils;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * @author : hbq969@gmail.com
 * @description : redis分布式锁切面增强器
 * @createTime : 2023/8/25 14:21
 */
@Aspect
@Slf4j
public class LockRedisAspect {

    @Qualifier("bc-lock-facade")
    @Autowired
    private LockProvider facade;

    @Pointcut("@annotation(com.github.hbq969.code.common.lock.annotation.DistLock)")
    public void distributedLockPointCut() {

    }

    @Around("distributedLockPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        DistLock ds = AnnotationUtils.findAnnotation(methodSignature.getMethod(), DistLock.class);
        if (Objects.isNull(ds)) {
            return joinPoint.proceed();
        }
        String lockKey = ds.lockKey();
        if (StrUtils.strEmpty(lockKey)) {
            lockKey = String.join(".", joinPoint.getTarget().getClass().getName(),
                    joinPoint.getSignature().getName());
        }
        int retryCount = ds.retryCount();
        int initRetryCount = retryCount;
        String requestId = UuidUtil.uuid();
        try {
            while (!facade.tryLock(lockKey, requestId, ds.expire(), ds.unit())) {
                if (retryCount-- == 0) {
                    throw new UnsupportedOperationException(
                            MessageFormat.format("重试了{0}次都未获取到锁, key: {}, requestId: {}",
                                    initRetryCount, lockKey,
                                    requestId));
                }
                try {
                    ds.retryUnit().sleep(ds.retryCycle());
                } catch (InterruptedException e) {
                    break;
                }
            }
            if (log.isDebugEnabled()) {
                if (initRetryCount - retryCount > 0) {
                    log.debug("第{}次重试获取到锁, key: {}, requestId: {}", initRetryCount - retryCount,
                            lockKey,
                            requestId);
                } else {
                    log.debug("首次获取到锁, key: {}, requestId: {}", lockKey, requestId);
                }
            }
            return joinPoint.proceed();
        } finally {
            try {
                facade.releaseLock(lockKey, requestId);
            } catch (Exception e) {
            }
        }
    }

}
