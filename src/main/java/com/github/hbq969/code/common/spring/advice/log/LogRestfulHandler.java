package com.github.hbq969.code.common.spring.advice.log;

import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.spring.advice.handler.RestfulHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

@Slf4j
public class LogRestfulHandler implements RestfulHandler {
    @Override
    public void before(ProceedingJoinPoint point) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        if (ArrayUtil.isNotEmpty(point.getArgs())) {
            StringBuilder sb = new StringBuilder(200);
            int len = point.getArgs().length;
            for (int i = 0; i < len; i++) {
                sb.append(", ").append(ms.getParameterNames()[i]).append("=").append(point.getArgs()[i]);
            }
            log.info("begin call ...  {}, <{}>", ms.getName(), sb.length() > 0 ? sb.substring(2) : "");
        } else {
            log.info("begin call ...  {}, <>", ms.getName());
        }
    }

    @Override
    public void after(ProceedingJoinPoint point, Object result) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        log.info("end call ... {}, 结果: {}", ms.getName(), result);
    }

    @Override
    public void exception(ProceedingJoinPoint point, Throwable e) {
        MethodSignature ms = (MethodSignature) point.getSignature();
        log.error(String.format("call exception ... %s", ms.getName()), e);
    }
}
