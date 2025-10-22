package com.github.hbq969.code.common.log.spi;

import cn.hutool.core.util.StrUtil;
import com.github.hbq969.code.common.config.LogProperties;
import com.github.hbq969.code.common.utils.TableInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DefaultLogModelDefProvider extends AbstractLogModelDefProvider {

    @Autowired
    private LogProperties conf;

    @Autowired
    private DataSource ds;

    @Override
    protected String tableDefSql(String tableName) {
        String dialect = getDialect();
        if (StrUtil.equals("mysql", dialect)) {
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
        } else if (StrUtil.equals("oracle", dialect)) {
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
        } else {
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
                    + ")";
        }
    }

    private String getDialect() {
        Connection c = null;
        String dialect = "embedded";
        try {
            c = ds.getConnection();
            if (c != null) {
                String dcn = c.getMetaData().getDriverName();
                if (StrUtil.startWithIgnoreCase(dcn, "mysql"))
                    dialect = "mysql";
                else if (StrUtil.startWithIgnoreCase(dcn, "oracle"))
                    dialect = "oracle";
                else if (StrUtil.startWithIgnoreCase(dcn, "postgresql"))
                    dialect = "postgresql";
            }
        } catch (SQLException e) {
        } finally {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                }
            }
        }
        return dialect;
    }

    @Override
    public TableInfo getTableInfo() {
        TableInfo ti = new TableInfo();
        if (StrUtil.isNotEmpty(conf.getTablePrefix()) && StrUtil.isNotEmpty(conf.getTableSuffix())) {
            ti.setTablePrefix(conf.getTablePrefix());
            ti.setTableSuffix(conf.getTableSuffix());
        } else if (StrUtil.isNotEmpty(conf.getTableName()))
            ti.setTableName(conf.getTableName());
        else
            throw new UnsupportedOperationException("operlog.tableName 或 [operlog.tablePrefix、operlog.tableSuffix] 至少需要配置一个");
        return ti;
    }

    @Override
    public Class<? extends LogData> logDataType() {
        return DefaultLogData.class;
    }
}
