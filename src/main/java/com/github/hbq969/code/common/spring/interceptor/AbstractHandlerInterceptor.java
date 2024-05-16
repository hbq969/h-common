package com.github.hbq969.code.common.spring.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author : hbq969@gmail.com
 * @description : 抽象拦截器
 * @createTime : 2023/10/11 17:01
 */
public abstract class AbstractHandlerInterceptor implements HandlerInterceptor, InterceptorOrder, PathPatternsAware {
}
