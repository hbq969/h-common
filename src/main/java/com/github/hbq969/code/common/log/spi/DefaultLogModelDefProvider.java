package com.github.hbq969.code.common.log.spi;

import com.github.hbq969.code.common.config.LogProperties;
import com.github.hbq969.code.common.utils.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultLogModelDefProvider extends AbstractLogModelDefProvider {

    @Autowired
    private LogProperties conf;

    @Override
    protected String tableDefSql(String tableName) {
        return "create table " + tableName + "\n(\n"
                + "req_id varchar(50) primary key,\n"
                + "oper_name varchar(50) not null,\n"
                + "oper_time numeric(20),\n"
                + "url varchar(255) not null,\n"
                + "method_name varchar(50),\n"
                + "method_desc varchar(500),\n"
                + "get_paras varchar(1024),\n"
                + "post_body text,\n"
                + "result text\n"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb3";
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
