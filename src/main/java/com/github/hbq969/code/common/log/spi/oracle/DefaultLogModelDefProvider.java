package com.github.hbq969.code.common.log.spi.oracle;

import com.github.hbq969.code.common.config.LogProperties;
import com.github.hbq969.code.common.log.spi.AbstractLogModelDefProvider;
import com.github.hbq969.code.common.log.spi.DefaultLogData;
import com.github.hbq969.code.common.log.spi.LogData;
import com.github.hbq969.code.common.utils.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultLogModelDefProvider extends AbstractLogModelDefProvider {

    @Autowired
    private LogProperties conf;

    @Override
    protected String tableDefSql(String tableName) {
        return "create table " + tableName + "\n(\n"
                + "req_id varchar2(50) primary key,\n"
                + "oper_name varchar2(50) not null,\n"
                + "oper_time number(20),\n"
                + "url varchar2(255) not null,\n"
                + "method_name varchar2(50),\n"
                + "method_desc varchar2(500),\n"
                + "get_paras varchar2(1024),\n"
                + "post_body clob,\n"
                + "result clob\n"
                + ")";
    }

    @Override
    public TableInfo getTableInfo() {
        TableInfo ti = new TableInfo();
        ti.setTableName(conf.getTableName());
        return ti;
    }

    @Override
    public Class<? extends LogData> logDataType() {
        return DefaultLogData.class;
    }
}
