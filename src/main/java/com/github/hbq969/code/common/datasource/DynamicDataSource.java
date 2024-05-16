package com.github.hbq969.code.common.datasource;

import com.github.hbq969.code.common.datasource.context.ContextPolicy;
import com.github.hbq969.code.common.datasource.context.ThreadLocalPolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.util.Objects;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源
 * @createTime : 15:38:48, 2023.02.20, 周二
 */
@Slf4j
public class DynamicDataSource extends AbstractRoutingDataSource {

    private String defaultLookupKey = "default";

    public DynamicDataSource() {
    }

    public DynamicDataSource(String defaultLookupKey) {
        this.defaultLookupKey = defaultLookupKey;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        ContextPolicy ctx = ThreadLocalPolicy.get();
        if (Objects.isNull(ctx)) {
            return this.defaultLookupKey;
        }
        return ctx.getDatasourceKey();
    }
}
