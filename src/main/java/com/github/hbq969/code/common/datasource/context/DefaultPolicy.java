package com.github.hbq969.code.common.datasource.context;


import com.github.hbq969.code.common.spring.context.SpringContext;

/**
 * @author : hbq969@gmail.com
 * @description : 默认数据源策略
 * @createTime : 2023/10/22 12:18
 */
public class DefaultPolicy extends AbstractLookupKeyPolicy {

    public static final String DEFAULT = "hikari";

    @Override
    protected String getDatasourceKey(SpringContext context) {
        return DEFAULT;
    }
}
