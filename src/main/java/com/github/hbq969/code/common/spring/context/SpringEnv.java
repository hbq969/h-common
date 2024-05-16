package com.github.hbq969.code.common.spring.context;

import java.util.Set;


/**
 * @author : hbq969@gmail.com
 * @description : Spring环境变量增强接口
 * @createTime : 18:06:03, 2023.03.28, 周二
 */
public interface SpringEnv {

  /**
   * 返回spring所有配置属性key
   *
   * @return
   */
  Set<String> getEnvironmentPropertyKeys();
}
