package com.github.hbq969.code.common.spring.interceptor;

import com.github.hbq969.code.common.config.SpringInterceptorProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author : hbq969@gmail.com
 * @description : api安全拦截器
 * @createTime : 2024/1/16 11:09
 */
@Slf4j
public class ApiSecurityInterceptor extends AbstractHandlerInterceptor {

    @Autowired
    private SpringInterceptorProperties conf;

    @Override
    public int order() {
        return 2;
    }

    @Override
    public List<String> getPathPatterns() {
        return this.conf.getApiSafe().getIncludePathPatterns();
    }

    @Override
    public List<String> getExcludedPathPatterns() {
        return this.conf.getApiSafe().getExcludePathPatterns();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String val = request.getHeader(conf.getApiSafe().getHeaderName());
        if (StringUtils.isEmpty(val)) {
            log.warn("请求 [{}] 未配置api安全请求头值，禁止访问", uri);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        if (null == conf.getApiSafe().getHeaderValueRegex()) {
            log.warn("请求 [{}] 未配置api安全请求头匹配值，禁止访问", uri);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }
        boolean permit = Pattern.matches(conf.getApiSafe().getHeaderValueRegex(), val);
        if (!permit) {
            log.warn("请求 [{}] 不满足api安全要求，禁止访问", uri);
            response.setStatus(HttpStatus.FORBIDDEN.value());
        }
        return permit;
    }
}
