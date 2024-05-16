package com.github.hbq969.code.common.decorde;


import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : hbq969@gmail.com
 * @description : 服务管理Aware
 * @createTime : 2023/7/2 18:54
 */
public class DefaultOptionalFacade<KEY, SERVICE> implements OptionalFacade<KEY, SERVICE> {

    private Map<KEY, SERVICE> services = new ConcurrentHashMap<>();

    @Override
    public void register(KEY key, SERVICE service) {
        services.put(key, service);
    }

    @Override
    public Optional<SERVICE> query(KEY key) {
        Assert.notNull(key, "KEY不能为空");
        return Optional.ofNullable(services.get(key));
    }
}
