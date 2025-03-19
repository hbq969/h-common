package com.github.hbq969.code.common.spring.advice.limit;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : restful接口限流
 * @createTime : 2023/9/8 13:47
 */
@Aspect
@Slf4j
public class RestfulLimitAdvice {

  private Map<String, RateLimiter> rates = new HashMap<>();

  @Pointcut("@annotation(com.github.hbq969.code.common.spring.advice.limit.RestfulLimit)")
  public void restfulLimitPointCut() {
  }

  @Around("restfulLimitPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    MethodSignature ms = (MethodSignature) point.getSignature();
    Method m = ms.getMethod();
    RestfulLimit rl = AnnotationUtils.findAnnotation(m, RestfulLimit.class);
    if (rl == null) {
      return point.proceed();
    }
    if (rl.value() <= 0 || rl.time() <= 0) {
      return point.proceed();
    }
    String key = String.join(".", point.getTarget().getClass().getName(), ms.getName());
    double qps = (double) rl.value() / TimeUnit.SECONDS.convert(rl.time(), rl.unit());
    RateLimiter rate;
    synchronized (rates) {
      rate = rates.get(key);
      if (rate == null) {
        rate = RateLimiter.create(qps);
        rates.put(key, rate);
        if(log.isDebugEnabled())
          log.debug("方法[{}]标记了限流策略, {}次/秒", key, qps);
      }
    }
    rate.acquire();
    return point.proceed();
  }
}
