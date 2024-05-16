package com.github.hbq969.code.common.spring.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author : hbq969@gmail.com
 * @description : 请求信息打印
 * @createTime : 2023/12/5 16:20
 */
@Slf4j
public class InfoHandlerInterceptor extends AbstractHandlerInterceptor {
    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[{}] {}, {}", request.getMethod(), request.getRequestURI(), StringUtils.isEmpty(request.getQueryString()) ? "" : request.getQueryString());
        return true;
    }
}
