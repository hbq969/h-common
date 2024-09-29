package com.github.hbq969.code.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    public SwaggerFilter(String apiSafeHeaderName, String apiSafeHeaderValue) {
        this.apiSafeHeaderName = apiSafeHeaderName;
        this.apiSafeHeaderValue = apiSafeHeaderValue;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) servletRequest;
        String uri = r.getRequestURI();
        String actualHeaderValue = r.getHeader(apiSafeHeaderName);
        if (StringUtils.endsWith(uri, "/v2/api-docs") && !StringUtils.equals(apiSafeHeaderValue, actualHeaderValue)) {
            log.warn("禁止访问/v2/api-docs");
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.setStatus(HttpStatus.FORBIDDEN_403);
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
