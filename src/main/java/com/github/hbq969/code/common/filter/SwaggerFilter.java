package com.github.hbq969.code.common.filter;

import cn.hutool.core.util.ObjectUtil;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author : hbq969@gmail.com
 * @description : swagger拦截器
 * @createTime : 2024/11/27 12:50
 */
@Slf4j
public class SwaggerFilter implements Filter {

    private String apiSafeHeaderName;
    private String apiSafeHeaderValue;
    private SpringContext sc;
    private boolean loginEnabled = false;

    public SwaggerFilter(SpringContext sc, String apiSafeHeaderName, String apiSafeHeaderValue) {
        this.sc = sc;
        this.loginEnabled = sc.getBoolValue("spring.mvc.interceptors.login.enabled", false);
        this.apiSafeHeaderName = apiSafeHeaderName;
        this.apiSafeHeaderValue = apiSafeHeaderValue;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String uri = request.getRequestURI();
        if (!StringUtils.endsWith(uri, "/v2/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }
        if (loginEnabled) {
            HttpSession session = request.getSession(false);
            if (null == session) {
                log.warn("会话为空，禁止访问/v2/api-docs");
                response.setStatus(org.springframework.http.HttpStatus.FORBIDDEN.value());
                return;
            }
            Object user = session.getAttribute("h-sm-user");
            if (ObjectUtil.isNotEmpty(user)) {
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                log.warn("会话失效或注销，禁止访问/v2/api-docs");
                response.setStatus(org.springframework.http.HttpStatus.FORBIDDEN.value());
            }
        } else {
            String actualHeaderValue = request.getHeader(apiSafeHeaderName);
            if (!StringUtils.equals(apiSafeHeaderValue, actualHeaderValue)) {
                log.warn("禁止访问/v2/api-docs");
                response.setStatus(org.springframework.http.HttpStatus.FORBIDDEN.value());
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }
}
