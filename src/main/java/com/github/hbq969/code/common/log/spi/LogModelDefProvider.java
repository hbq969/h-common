package com.github.hbq969.code.common.log.spi;

import com.github.hbq969.code.common.utils.TableInfo;

public interface LogModelDefProvider {
    String tableDefSql();

    TableInfo getTableInfo();

    Class<? extends LogData> logDataType();
}
