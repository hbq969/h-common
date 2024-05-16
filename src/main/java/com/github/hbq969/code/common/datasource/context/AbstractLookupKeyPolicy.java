package com.github.hbq969.code.common.datasource.context;

import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.Setter;

/**
 * @author : hbq969@gmail.com
 * @description : 查询数据库key抽象模版类
 * @createTime : 2023/8/11 15:19
 */
public abstract class AbstractLookupKeyPolicy implements ContextPolicy {

  @Setter
  private SpringContext context;

  @Override
  public String getDatasourceKey() {
    return getDatasourceKey(context);
  }

  abstract protected String getDatasourceKey(SpringContext context);
}
