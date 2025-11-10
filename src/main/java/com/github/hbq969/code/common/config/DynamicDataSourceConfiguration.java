package com.github.hbq969.code.common.config;

import cn.hutool.core.collection.CollectionUtil;
import com.github.hbq969.code.common.datasource.DynamicDataSource;
import com.github.hbq969.code.common.datasource.DynamicDataSourceAspect;
import com.github.hbq969.code.common.datasource.DynamicDataSourceBeanProcessor;
import com.github.hbq969.code.common.datasource.monitor.PoolMonitor;
import com.github.hbq969.code.common.spring.context.SpringEnvImpl;
import com.github.hbq969.code.common.utils.GsonUtils;
import com.github.hbq969.code.common.utils.StrUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源自动装配配置类
 * @createTime : 19:41:30, 2023.04.03, 周一
 */
@Slf4j
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
public class DynamicDataSourceConfiguration implements ApplicationContextAware {

    private ApplicationContext context;

    public static final String KEY_SPRING_DATASOURCE = "spring.datasource.";

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @Bean("common-dynamicdatasource-DynamicDataSourceBeanProcessor")
    DynamicDataSourceBeanProcessor dynamicDataSourceBeanProcessor() {
        return new DynamicDataSourceBeanProcessor();
    }

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @Bean("common-dynamicdatasource-DynamicDataSourceAspect")
    DynamicDataSourceAspect dynamicDataSourceAspect() {
        return new DynamicDataSourceAspect();
    }

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @Bean("common-dynamicdatasource-DynamicDataSource")
    @Primary
    DataSource dynamicDataSource() {
        Set<String> pKeys = context.getBean(SpringEnvImpl.class).getEnvironmentPropertyKeys();
        Set<String> dbKeySet = pKeys.stream().filter(k -> {
            boolean r = (k.startsWith("spring.datasource.") && k.endsWith(".jdbc-url"));
            return r;
        }).map(k -> {
            int startIndex = "spring.datasource.".length();
            String dbKey = k.substring(startIndex, k.length() - ".jdbc-url".length());
            return dbKey;
        }).collect(Collectors.toSet());
        String dbs = GsonUtils.toJson(dbKeySet);
        if (log.isDebugEnabled()) {
            log.debug("解析到多数据源信息: {}", dbs);
        }
        if (CollectionUtil.isEmpty(dbKeySet)) {
            throw new IllegalArgumentException("请配置动态多数据源");
        }
        Map<Object, Object> dataSourceMap = new HashMap<>(dbKeySet.size());
        dbKeySet.forEach(dbKey -> dataSourceMap.put(dbKey, createBasicDataSource(dbKey)));
        String defaultLookupKey = context.getBean(SpringContextProperties.class).getDatasource().getDynamic().getDefaultLookupKey();
        DynamicDataSource dynamicDataSource = new DynamicDataSource(defaultLookupKey);
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        Object defaultDatasource = dataSourceMap.get(defaultLookupKey);
        if (log.isDebugEnabled()) {
            log.debug("缺省数据源: {}", defaultLookupKey);
        }
        if (!dbKeySet.contains(defaultLookupKey)) {
            throw new IllegalArgumentException(
                    MessageFormat.format("spring.datasource.dynamic.default-lookup-key需要从{0}中取", dbs));
        }
        dynamicDataSource.setDefaultTargetDataSource(defaultDatasource);
        return dynamicDataSource;
    }

    private HikariDataSource createBasicDataSource(String dbKey) {
        String jdbcUrl = context.getEnvironment().getProperty(KEY_SPRING_DATASOURCE + dbKey + ".jdbc-url");
        String driverClassName = context.getEnvironment().getProperty(
                KEY_SPRING_DATASOURCE + dbKey + ".driver-class-name");
        String user = context.getEnvironment().getProperty(KEY_SPRING_DATASOURCE + dbKey + ".username");
        String pwd = context.getEnvironment().getProperty(KEY_SPRING_DATASOURCE + dbKey + ".password");
        int maxPoolSize = Integer.valueOf(context.getEnvironment().getProperty(KEY_SPRING_DATASOURCE + dbKey + ".maximum-pool-size", "50"));
        int minIdle = Integer.valueOf(context.getEnvironment().getProperty(KEY_SPRING_DATASOURCE + dbKey + ".minimum-idle", "5"));
        long maxLifeTime = Long.valueOf(context.getEnvironment().getProperty(KEY_SPRING_DATASOURCE + dbKey + ".max-lifetime",
                "300000"));
        String conTestQuery = context.getEnvironment().getProperty(
                KEY_SPRING_DATASOURCE + dbKey + ".connection-test-query");
        if (StrUtils.strEmpty(conTestQuery)) {
            conTestQuery = driverClassName.contains("oracle") ? "select 1 from dual" : "select 1";
        }
        if (log.isTraceEnabled()) {
            log.trace(
                    "构建动态数据源: {}, 驱动类: {}, url: {}, 连接总数: {}, 最小空闲: {}, 等待超时: {} ms, 测试sql: {}",
                    dbKey, driverClassName, jdbcUrl, maxPoolSize, minIdle, maxLifeTime, conTestQuery);
        }
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(user);
        ds.setPassword(pwd);
        ds.setMaximumPoolSize(maxPoolSize);
        ds.setMinimumIdle(minIdle);
        ds.setMaxLifetime(maxLifeTime);
        ds.setConnectionTestQuery(conTestQuery);
        return ds;
    }

    @ConditionalOnExpression("#{ ${spring.datasource.dynamic.enabled:false} && ${spring.datasource.dynamic.monitor.enabled:false}}")
    @Bean("common-dynamicdatasource-PoolMonitor")
    PoolMonitor poolMonitor(@Qualifier("common-dynamicdatasource-DynamicDataSource") DataSource dataSource) {
        return new PoolMonitor(dataSource);
    }
}
