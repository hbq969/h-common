package com.github.hbq969.code.common.decorde;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : hbq969@gmail.com
 * @description : 服务管理Aware
 * @createTime : 2023/7/2 18:54
 */
@Slf4j
public class DefaultOptionalFacade<KEY, SERVICE> implements OptionalFacade<KEY, SERVICE> {

    private Map<KEY, SERVICE> services = new ConcurrentHashMap<>();

    @Override
    public void register(KEY key, SERVICE service) {
        log.debug("注册实现，<{}, {}>", key, service.getClass().getSimpleName());
        services.put(key, service);
    }

    /**
     * 清空注册的服务
     */
    protected void clear() {
        this.services.clear();
    }

    @Override
    public Optional<SERVICE> query(KEY key) {
        Assert.notNull(key, "KEY不能为空");
        return Optional.ofNullable(services.get(key));
    }

    @Override
    public Map<KEY, SERVICE> getRegisterServices() {
        return Collections.unmodifiableMap(services);
    }

    @Override
    public Set<KEY> getKeys() {
        return Collections.unmodifiableSet(services.keySet());
    }

    @Override
    public Collection<SERVICE> getServiceInstances() {
        return Collections.unmodifiableCollection(services.values());
    }
}
