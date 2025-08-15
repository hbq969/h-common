package com.github.hbq969.code.common.spring.interceptor;

import com.github.hbq969.code.common.utils.UuidUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;


/**
 * @author : hbq969@gmail.com
 * @description : springmvc请求上下文
 * @createTime : 2023/8/27 13:04
 */
@Slf4j
public class MDCHandlerInterceptor extends AbstractHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String id = MDC.get("requestId");
        if (StringUtils.isEmpty(id)) {
            MDC.put("requestId", UuidUtil.uuid());
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        MDC.remove("requestId");
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }
}
