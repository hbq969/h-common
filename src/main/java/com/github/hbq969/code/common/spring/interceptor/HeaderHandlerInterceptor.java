package com.github.hbq969.code.common.spring.interceptor;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @author : hbq969@gmail.com
 * @description : springmvc请求头信息打印拦截器
 * @createTime : 2023/8/27 13:10
 */
@Slf4j
public class HeaderHandlerInterceptor extends AbstractHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (log.isDebugEnabled()) {
            Enumeration<String> enu = request.getHeaderNames();
            String headerName;
            while (enu.hasMoreElements()) {
                headerName = enu.nextElement();
                log.debug("{}: {}", headerName, request.getHeader(headerName));
            }
        }
        return true;
    }

    @Override
    public int order() {
        return 0;
    }
}
