package com.github.hbq969.code.common.spring.cloud.feign;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : hbq969@gmail.com
 * @description : 打印Feign请求信息
 * @createTime : 2023/8/26 19:39
 */
@Slf4j
public class InfoRequestInterceptor implements RequestInterceptor {

    private Gson gson = new Gson();

    @Override
    public void apply(RequestTemplate rt) {
        if (log.isDebugEnabled()) {
            if (StrUtil.startWith(rt.path(), "http"))
                log.debug("[{}] [{}], headers: {}, variables: {}, queries: {}, content-length: {}",
                        rt.method(), rt.path(), gson.toJson(rt.headers()),
                        gson.toJson(rt.getRequestVariables()), gson.toJson(rt.queries()),
                        rt.requestBody().length());
            else
                log.debug("[{}] [{}{}], headers: {}, variables: {}, queries: {}, content-length: {}",
                        rt.method(), rt.feignTarget().url(), rt.path(), gson.toJson(rt.headers()),
                        gson.toJson(rt.getRequestVariables()), gson.toJson(rt.queries()),
                        rt.requestBody().length());
        }
    }
}
