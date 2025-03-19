package com.github.hbq969.code.common.log.collect;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.decorde.OptionalFacade;
import com.github.hbq969.code.common.decorde.OptionalFacadeAware;
import com.github.hbq969.code.common.log.spi.DbCol;
import com.github.hbq969.code.common.log.spi.LogData;
import com.github.hbq969.code.common.log.spi.LogModelDefProvider;
import com.github.hbq969.code.common.utils.Count;
import com.github.hbq969.code.common.utils.StrUtils;
import com.github.hbq969.code.common.utils.TableInfo;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class DbLogServiceImpl implements OptionalFacadeAware<String, LogService>, LogService {

    @Autowired
    private LogServiceFacade facade;
    @Autowired(required = false)
    private LogModelDefProvider provider;
    private List<Field> typeFields;
    private String saveSql;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        OptionalFacadeAware.super.afterPropertiesSet();
        Assert.notNull(provider, "启用日志采集功能，请先扩展实现日志采集功能，参考com.github.hbq969.code.common.log.spi.LogModelDefProvider");
        createLogTable(true);
        setTypeFields();
        createSaveSql();
    }

    @Override
    public OptionalFacade<String, LogService> getOptionalFacade() {
        return this.facade;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public Set<String> getKeys() {
        return Sets.newHashSet("mysql","oracle");
    }

    @Override
    public void deal(List<LogData> list) {
        try {
            createLogTable(false);
            transactionTemplate.execute(status -> {
                jdbcTemplate.batchUpdate(this.saveSql, new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        Object info = list.get(i);
                        setPreparedStatement(ps, info);
                    }

                    @Override
                    public int getBatchSize() {
                        return list.size();
                    }
                });
                return null;
            });
            if(log.isDebugEnabled())
                log.debug("批量保存操作日志到数据库成功, {} 条。", list.size());
        } catch (TransactionException te) {
            log.info("批量保存操作日志到数据库失败，进行回滚，并逐条保存");
            Count suc = Count.unsafe();
            Count fail = Count.unsafe();
            list.forEach(operLog -> {
                try {
                    jdbcTemplate.update(this.saveSql, ps -> setPreparedStatement(ps, operLog));
                    suc.incrementAndGet();
                } catch (DataAccessException e) {
                    fail.incrementAndGet();
                    // do nothing
                }
            });
            log.info("逐条保存结果，成功: {} 条，失败：{} 条。", suc.intValue(), fail.intValue());
        } finally {
            list.clear();
        }
    }

    private void createLogTable(boolean print) {
        TableInfo ti = provider.getTableInfo();
        try {
            String sql = provider.tableDefSql();
            if (print && log.isDebugEnabled()) {
                log.debug(sql);
            }
            jdbcTemplate.update(sql);
            if(log.isDebugEnabled())
                log.debug("日志留存采集表 {} 创建成功。", ti.getTableName());
        } catch (Exception e) {
            if(log.isTraceEnabled())
                log.trace("日志留存采集表 {} 已存在。", ti.getTableName());
        }
    }

    private void setTypeFields() {
        typeFields = new ArrayList<>();
        Class<?> type = provider.logDataType();
        Field[] fs = type.getDeclaredFields();
        for (Field f : fs) {
            if (f.isAnnotationPresent(DbCol.class)) {
                typeFields.add(f);
            }
        }
    }

    private void createSaveSql() {
        try {
            StringBuilder sb = new StringBuilder(200);
            sb.append("insert into ").append(provider.getTableInfo().getTableName()).append("(");
            String col;
            Count c = Count.unsafe();
            for (Field typeField : typeFields) {
                DbCol dbCol = AnnotationUtils.getAnnotation(typeField, DbCol.class);
                if (Objects.nonNull(dbCol)) {
                    col = dbCol.value();
                    sb.append(c.intValue() > 0 ? "," : "").append(col);
                    c.incrementAndGet();
                }
            }
            String placeholder = StrUtils.symbolSplic("?", ",", c.intValue());
            sb.append(") values(").append(placeholder).append(")");
            this.saveSql = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setPreparedStatement(PreparedStatement ps, Object info) throws SQLException {
        Field f;
        for (int i = 0; i < typeFields.size(); i++) {
            f = typeFields.get(i);
            ReflectionUtils.makeAccessible(f);
            ps.setObject(i + 1, ReflectionUtils.getField(f, info));
        }
    }
}
