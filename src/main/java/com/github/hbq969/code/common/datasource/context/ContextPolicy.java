package com.github.hbq969.code.common.datasource.context;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源上下文
 * @createTime : 9:00:33, 2023.03.29, 周三
 */
@FunctionalInterface
public interface ContextPolicy {

  /**
   * 返回数据源定义的关键字
   * <ul>
   *   <li>spring.datasource.[datasourceKey].jdbc-url</li>
   *   <li>spring.datasource.[datasourceKey].driver-class-name</li>
   *   <li>spring.datasource.[datasourceKey].username</li>
   *   <li>spring.datasource.[datasourceKey].password</li>
   *   <li>spring.datasource.[datasourceKey].maximumPoolSize</li>
   *   <li>spring.datasource.[datasourceKey].minimumIdle</li>
   *   <li>spring.datasource.[datasourceKey].max-lifetime</li>
   *   <li>spring.datasource.[datasourceKey].connection-test-query</li>
   * </ul>
   *
   * @return
   */
  String getDatasourceKey();
}
