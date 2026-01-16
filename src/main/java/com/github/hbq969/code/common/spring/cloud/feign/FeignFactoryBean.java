package com.github.hbq969.code.common.spring.cloud.feign;

import cn.hutool.core.util.StrUtil;
import feign.*;
import feign.Feign.Builder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.util.Assert;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 自定义Feign工厂类
 * @createTime : 2023/8/26 16:42
 */
@Slf4j
public class FeignFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private Class<T> inter;

    private String url = "localhost:8080";

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(getObjectType(), "请配置feign代理接口");
    }

    @Override
    public T getObject() throws Exception {
        Builder builder = Feign.builder();
        builder.client(client()).retryer(feignRetry()).options(options()).contract(contract()).encoder(encoder()).decoder(decoder()).errorDecoder(errorDecoder());
        List<RequestInterceptor> ins = interceptors();
        if (CollectionUtils.isNotEmpty(ins)) {
            builder.requestInterceptors(ins);
        }
        builder.logger(new Slf4jLogger()).logLevel(level());
        apply(builder);
        T t = builder.target(getObjectType(), getUrl());
        return t;
    }

    @Override
    public Class<T> getObjectType() {
        return inter;
    }

    public void setInter(Class<T> inter) {
        this.inter = inter;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    protected Client client() {
        String url = getUrl();
        if (StrUtil.startWith(url, "https")) {
            return createTLSClient();
        } else {
            return new ApacheHttpClient();
        }
    }

    private Client createTLSClient() {
        try {
            // 1. 创建信任所有证书的 TrustManager
            X509TrustManager trustManager = new DefaultX509TrustManager();

            // 2. 创建 SSLContext，使用 TLS（自动协商最佳版本）
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());

            // 3. 创建 SSLConnectionSocketFactory，不限制密码套件（让系统自动协商）
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                    sslContext,
                    null,  // 支持所有 TLS 协议版本，让客户端和服务端自动协商
                    null,  // 支持所有密码套件，让客户端和服务端自动协商
                    new NoopHostnameVerifier()
            );

            // 4. 配置连接管理器
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslSocketFactory)
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .build();

            PoolingHttpClientConnectionManager connectionManager =
                    new PoolingHttpClientConnectionManager(registry);

            // 5. 创建 HttpClient
            HttpClient hc = HttpClientBuilder.create()
                    .setConnectionManager(connectionManager)
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setConnectTimeout(10000)
                            .setSocketTimeout(30000)
                            .build())
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))
                    .build();

            return new ApacheHttpClient(hc);

        } catch (Exception e) {
            log.error("创建 HTTPS 客户端异常", e);
            throw new RuntimeException("创建 HTTPS 客户端失败", e);
        }
    }

    /**
     * 重试配置，默认为1秒一次，最大间隔时间不得超过5秒，重试3次
     *
     * @return
     */
    protected Retryer feignRetry() {
        return new Retryer.Default(5000, 15000, 2);
    }

    protected Request.Options options() {
        return new Request.Options(5000, 60000, true);
    }

    protected Contract contract() {
        return new SpringMvcContract();
    }

    protected Encoder encoder() {
        return new JacksonEncoder();
    }

    protected Decoder decoder() {
        return new LogJacksonDecoder(canDecodeLog());
    }

    protected ErrorDecoder errorDecoder() {
        return new ErrorDecoder.Default();
    }

    protected LinkedList<RequestInterceptor> interceptors() {
        LinkedList<RequestInterceptor> list = new LinkedList<>();
        list.add(new InfoRequestInterceptor());
        return list;
    }

    protected Logger.Level level() {
        return Logger.Level.FULL;
    }

    protected void apply(Builder builder) {

    }

    /**
     * 注意：只在发序列化是LogJacksonDecoder时生效
     *
     * @return
     */
    protected boolean canDecodeLog() {
        return true;
    }
}
