package com.github.hbq969.code.common.spring.cloud.feign;

import feign.*;
import feign.Feign.Builder;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.httpclient.ApacheHttpClient;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 自定义Feign工厂类
 * @createTime : 2023/8/26 16:42
 */
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
        return new ApacheHttpClient();
    }

    /**
     * 重试配置，默认为1秒一次，最大间隔时间不得超过5秒，重试3次
     *
     * @return
     */
    protected Retryer feignRetry() {
        return new Retryer.Default(1000, 5000, 3);
    }

    protected Request.Options options() {
        return new Request.Options(1000, 60000, true);
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
