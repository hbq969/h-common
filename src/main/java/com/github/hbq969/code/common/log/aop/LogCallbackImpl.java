package com.github.hbq969.code.common.log.aop;

import com.github.hbq969.code.common.log.api.Log;
import com.github.hbq969.code.common.log.model.PointModel;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.MethodUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Objects;

@Slf4j
public class LogCallbackImpl implements MethodInterceptor {

    private final SpringContext context;
    private final Log ret;
    private final Object target;

    public LogCallbackImpl(SpringContext context, Log ret, Object target) {
        this.context = context;
        this.ret = ret;
        this.target = target;
    }

    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy mp) throws Throwable {
        Object result = null;
        Throwable ex = null;
        try {
            result = method.invoke(target, args);
        } catch (Throwable e) {
            ex = e;
        }
        try {
            if (!method.isAnnotationPresent(Log.class) && MethodUtils.isNotObjectMethod(method)) {
                if (log.isDebugEnabled()) {
                    log.debug("[类级别拦截] 方法[{}]记录日志。", method.getName());
                }
                String[] parameterNames = LogHandlerImpl.getParameterNames(method, args);
                PointModel point = new PointModel(ret,method, parameterNames, args, result, ex, target);
                OperlogAspect.logCollect(context, ret, point);
            }
        } catch (Exception e) {
            log.error(String.format("[类级别拦截] 方法[%s]记录日志异常", method.getName()), e);
        }
        if (Objects.nonNull(ex)) {
            throw ex;
        }
        return result;
    }
}
