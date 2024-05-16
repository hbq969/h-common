package com.github.hbq969.code.common.decorde;

import org.springframework.beans.factory.InitializingBean;

import java.util.Optional;

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
    Optional.ofNullable(getOptionalFacade())
        .ifPresent(facade -> facade.register(getKey(), getTarget()));
  }
}
