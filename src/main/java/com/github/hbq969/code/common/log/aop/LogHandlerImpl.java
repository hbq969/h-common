package com.github.hbq969.code.common.log.aop;

import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.log.api.Log;
import com.github.hbq969.code.common.log.model.PointModel;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public class LogHandlerImpl implements InvocationHandler {

    private SpringContext context;
    private Log ret;
    private Object target;

    public LogHandlerImpl(SpringContext context, Log ret, Object target) {
        this.context = context;
        this.ret = ret;
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Throwable ex = null;
        try {
            result = method.invoke(target, args);
        } catch (Throwable e) {
            ex = e;
        }
        try {
            if (!method.isAnnotationPresent(Log.class)) {
                if (log.isDebugEnabled()) {
                    log.debug("[接口级别拦截] 方法[{}]记录日志。", method.getName());
                }
                String[] parameterNames = getParameterNames(method, args);
                PointModel point = new PointModel(ret, method, parameterNames, args, result, ex, target);
                OperlogAspect.logCollect(context, ret, point);
            }
        } catch (Exception e) {
            log.error(String.format("[接口级别拦截] 方法[%s]记录日志异常", method.getName()), e);
        }
        if (Objects.nonNull(ex)) {
            throw ex;
        }
        return result;
    }

    public static String[] getParameterNames(Method method, Object[] args) {
        String[] parameterNames = null;
        if (ArrayUtil.isNotEmpty(args)) {
            parameterNames = new String[args.length];
            for (int i = 0; i < method.getParameters().length; i++) {
                parameterNames[i] = method.getParameters()[i].getName();
            }
        }
        return parameterNames;
    }
}
