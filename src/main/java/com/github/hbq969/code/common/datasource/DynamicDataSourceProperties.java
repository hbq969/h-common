package com.github.hbq969.code.common.datasource;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源属性配置
 * @createTime : 18:18:23, 2023.03.29, 周三
 */
@ConfigurationProperties(prefix = "spring.datasource.dynamic")
@Data
public class DynamicDataSourceProperties {

  /**
   * 是否启用动态多数据源
   */
  private boolean enabled = false;

  /**
   * 缺省使用的数据源KEY
   */
  private String defaultLookupKey = "default";

  /**
   * 动态多数据源扫描Mapper接口包路径
   */
  private String[] basePackages = {"com.github.hbq969"};
}
