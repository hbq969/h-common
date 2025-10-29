package com.github.hbq969.code.common.spring.advice.log;

import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.spring.advice.conf.AdviceProperties;
import com.github.hbq969.code.common.spring.advice.handler.RestfulHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

@Slf4j
public class LogRestfulHandler implements RestfulHandler {

    @Autowired
    private AdviceProperties conf;
    @Override
    public void before(ProceedingJoinPoint point) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        LogSet logSet = AnnotationUtils.findAnnotation(ms.getMethod(), LogSet.class);
        if (logSet == null || logSet.printIn() && ArrayUtil.isNotEmpty(point.getArgs())) {
            StringBuilder sb = new StringBuilder(200);
            int len = point.getArgs().length;
            for (int i = 0; i < len; i++) {
                sb.append(", ").append(ms.getParameterNames()[i]).append("=").append(point.getArgs()[i]);
            }
            log.info("< {}.{}, [{}]", ms.getDeclaringType().getSimpleName(), ms.getName(), sb.length() > 0 ? sb.substring(2) : "");
        } else {
            log.info("< {}.{}, <>", ms.getDeclaringType().getSimpleName(), ms.getName());
        }
    }

    @Override
    public void after(ProceedingJoinPoint point, Object result) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        LogSet logSet = AnnotationUtils.findAnnotation(ms.getMethod(), LogSet.class);
        if (logSet == null || logSet.printResult() && logSet.printResult()) {
            if (result instanceof byte[])
                log.info("> {}.{}", ms.getDeclaringType().getSimpleName(), ms.getName());
            else
                log.info("> {}.{}, 结果: {}", ms.getDeclaringType().getSimpleName(), ms.getName(), result);
        } else {
            log.info("> {}.{}", ms.getDeclaringType().getSimpleName(), ms.getName());
        }
    }

    @Override
    public void exception(ProceedingJoinPoint point, Throwable e) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        log.error("<err> {}.{} {}", ms.getDeclaringType().getSimpleName(), ms.getName(), e);
    }

    @Override
    public boolean enabled() {
        return conf.getLog().isEnabled();
    }
}
