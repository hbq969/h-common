package com.github.hbq969.code.common.decorde;

import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.Optional;
import java.util.Set;

/**
 * @author : hbq969@gmail.com
 * @description : 服务管理Aware
 * @createTime : 2023/7/2 18:54
 */
public interface OptionalFacadeAware<KEY, SERVICE> extends InitializingBean {

    /**
     * 返回注入的服务管理器
     *
     * @return
     */
    OptionalFacade<KEY, SERVICE> getOptionalFacade();

    /**
     * 返回当前服务的标识
     *
     * @return
     */
    KEY getKey();

    /**
     * 返回当前服务的多个标识，优先使用getKey的值，如果getKey为空才使用getKeys
     *
     * @return
     */
    default Set<KEY> getKeys() {
        KEY k = getKey();
        return k == null ? null : Sets.newHashSet(k);
    }

    /**
     * 返回当前服务实例对象
     *
     * @return
     */
    default SERVICE getTarget() {
        return (SERVICE) this;
    }

    /**
     * 如果覆盖此方法需要在此方法最前面调用<br/>
     * <code>OptionalFacadeAware.super.afterPropertiesSet()</code>
     *
     * @throws Exception
     */
    @Override
    default void afterPropertiesSet() throws Exception {
        Optional.ofNullable(getOptionalFacade()).ifPresent(facade -> {
            KEY key = getKey();
            if (key != null) {
                facade.register(key, getTarget());
            } else {
                Set<KEY> ks = getKeys();
                if (CollectionUtils.isNotEmpty(ks)) {
                    ks.forEach(k -> facade.register(k, getTarget()));
                }
            }
        });
    }
}
