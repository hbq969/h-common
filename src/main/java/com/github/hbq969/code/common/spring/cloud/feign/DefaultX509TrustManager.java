package com.github.hbq969.code.common.spring.cloud.feign;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class DefaultX509TrustManager implements X509TrustManager {
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // 返回空数组，但不能返回 null
        return new X509Certificate[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) {
        // 信任所有客户端证书
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) {
        // 信任所有服务器证书（包括自签名）
    }
}
