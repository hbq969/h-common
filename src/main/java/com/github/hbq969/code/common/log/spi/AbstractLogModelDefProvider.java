package com.github.hbq969.code.common.log.spi;

import com.github.hbq969.code.common.utils.TableInfo;
import org.springframework.util.Assert;

public abstract class AbstractLogModelDefProvider implements LogModelDefProvider {
    @Override
    public String tableDefSql() {
        TableInfo ti = getTableInfo();
        Assert.notNull(ti, "未实现getTableInfo方法。");
        ti.format();
        return tableDefSql(ti.getTableName());
    }

    protected abstract String tableDefSql(String tableName);
}
