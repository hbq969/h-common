package com.github.hbq969.code.common.log.aop;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.log.api.Log;
import com.github.hbq969.code.common.log.collect.LogService;
import com.github.hbq969.code.common.log.collect.LogServiceFacade;
import com.github.hbq969.code.common.log.model.PointModel;
import com.github.hbq969.code.common.log.spi.LogCollect;
import com.github.hbq969.code.common.log.spi.LogData;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Slf4j
public class OperlogAspect {

    @Autowired
    private SpringContext context;

    @Pointcut("@annotation(com.github.hbq969.code.common.log.api.Log)")
    public void logRetPointCut() {
    }

    @Around("logRetPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Object result = null;
        Throwable ex = null;
        try {
            result = point.proceed();
        } catch (Throwable e) {
            ex = e;
        }
        try {
            Optional<Log> lop = getLogRetAnnotation(point);
            if (lop.isPresent()) {
                Log logRet = lop.get();
                if (logRet.enabled()) {
                    MethodSignature ms = (MethodSignature) point.getSignature();
                    Method m = ms.getMethod();
                    String[] parameterNames = LogHandlerImpl.getParameterNames(m, point.getArgs());
                    Object[] parameterValues = point.getArgs();
                    Object target = point.getTarget();
                    PointModel p = new PointModel(logRet, m, parameterNames, parameterValues, result, ex, target);
                    logCollect(context, logRet, p);
                }
            }
        } catch (Throwable e) {
            log.error("", e);
        }
        if (Objects.nonNull(ex)) {
            throw ex;
        }
        return result;
    }

    private Optional<Log> getLogRetAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return Optional.ofNullable(AnnotationUtils.findAnnotation(signature.getMethod(), Log.class));
    }

    public static void logCollect(SpringContext context, Log ret, PointModel point) {
        LogCollect policy = context.getBean(LogCollect.class);
        Assert.notNull(policy, "启用日志采集功能，请先扩展实现日志采集功能，参考com.github.hbq969.code.common.log.spi.LogCollect");
        LogData info = policy.collect(point);
        if (Objects.isNull(info)) {
            log.warn("未采集到日志信息，请检查采集逻辑是否存在异常。");
            return;
        }
        LogService collector = context.getBean(LogServiceFacade.class);
        if (!collector.submit(info)) {
            int capacity = context.getIntValue("operlog.queue-capacity", 5000);
            log.warn("日志采集本地队列溢出(>{})，可能原因为：大量请求，批量入库性能下降", capacity);
        }
    }
}
