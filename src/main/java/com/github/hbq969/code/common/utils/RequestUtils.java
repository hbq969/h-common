package com.github.hbq969.code.common.utils;


import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {
    public static String getClientIp(HttpServletRequest request) {
        String[] headers = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP", "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR", "X-Real-IP"};

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                // 对于X-Forwarded-For取第一个IP
                if ("X-Forwarded-For".equalsIgnoreCase(header)) {
                    ip = ip.split(",")[0];
                }
                return ip;
            }
        }

        return request.getRemoteAddr();
    }
}
