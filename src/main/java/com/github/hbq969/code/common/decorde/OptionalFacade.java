package com.github.hbq969.code.common.decorde;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author : hbq969@gmail.com
 * @description : 服务管理Aware
 * @createTime : 2023/7/2 18:54
 */
public interface OptionalFacade<KEY, SERVICE> {

    /**
     * 注册服务信息
     *
     * @param key
     * @param service
     */
    void register(KEY key, SERVICE service);

    /**
     * 根据服务key查询服务
     *
     * @return
     */
    default Optional<SERVICE> query() {
        return query((KEY) "default");
    }

    /**
     * 根据默认条件查询服务
     *
     * @param key
     * @return
     */
    Optional<SERVICE> query(KEY key);

    /**
     * 返回指定Key值对应的服务实例
     *
     * @param key
     * @return
     */
    default SERVICE getService(KEY key) {
        return query(key).orElseThrow(() -> new UnsupportedOperationException(MessageFormat.format("不支持的类型[{0}]", key)));
    }

    /**
     * 返回缺省Key值对应的服务实例
     *
     * @return
     */
    default SERVICE getService() {
        return query().orElseThrow(() -> new UnsupportedOperationException("不支持的类型"));
    }

    Map<KEY, SERVICE> getRegisterServices();

    Set<KEY> getKeys();

    Collection<SERVICE> getServiceInstances();
}
