package com.github.hbq969.code.common.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CookieUtils {
    public static String getCookieValue(HttpServletRequest request, String key) {
        Cookie cookie = getCookie(request, key);
        return cookie == null ? null : cookie.getValue();
    }

    public static Cookie getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (StringUtils.equals(key, cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }

    public static void invalidateCookie(HttpServletResponse response, String cookieKey, String cookiePath, int httpStatusCode) {
        Cookie jsessionCookie = new Cookie(cookieKey, null);
        jsessionCookie.setMaxAge(0);
        jsessionCookie.setPath(cookiePath);
        jsessionCookie.setHttpOnly(true);
        response.addCookie(jsessionCookie);
        response.setStatus(httpStatusCode);
        if (log.isDebugEnabled())
            log.debug("设置Cookie[name={},Path={}]失效", cookieKey, cookiePath);
    }

    public static String getSetCookie(Cookie c) {
        String expiresHeader = FormatTime.UTC_FULL.withMills(System.currentTimeMillis() + c.getMaxAge() * 1000L);
        return StrUtil.format("{}={};  Max-Age={}; Expires={}; Path={}; SameSite=Strict; {} {}",
                c.getName(), c.getValue(), c.getMaxAge(), expiresHeader, c.getPath(), c.getSecure() ? "Secure;" : "", c.isHttpOnly() ? "HttpOnly;" : "");
    }
}
