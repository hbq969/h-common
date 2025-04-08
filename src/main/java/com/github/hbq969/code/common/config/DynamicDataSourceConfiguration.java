package com.github.hbq969.code.common.config;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.hbq969.code.common.datasource.DynamicDataSource;
import com.github.hbq969.code.common.datasource.DynamicDataSourceAspect;
import com.github.hbq969.code.common.datasource.DynamicDataSourceBeanProcessor;
import com.github.hbq969.code.common.datasource.monitor.PoolMonitor;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.github.hbq969.code.common.utils.GsonUtils;
import com.github.hbq969.code.common.utils.StrUtils;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : hbq969@gmail.com
 * @description : 动态数据源自动装配配置类
 * @createTime : 19:41:30, 2023.04.03, 周一
 */
@Slf4j
@ConditionalOnClass({SqlSessionFactory.class, SqlSessionFactoryBean.class})
public class DynamicDataSourceConfiguration {

    public static final String KEY_SPRING_DATASOURCE = "spring.datasource.";

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
    DataSource dynamicDataSource(SpringContext context, SpringContextProperties properties) {
        Set<String> pKeys = context.getEnvironmentPropertyKeys();
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
        dbKeySet.forEach(dbKey -> dataSourceMap.put(dbKey, createBasicDataSource(context, dbKey)));
        String defaultLookupKey = properties.getDatasource().getDynamic().getDefaultLookupKey();
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

    private HikariDataSource createBasicDataSource(SpringContext context, String dbKey) {
        String jdbcUrl = context.getProperty(KEY_SPRING_DATASOURCE + dbKey + ".jdbc-url");
        String driverClassName = context.getProperty(
                KEY_SPRING_DATASOURCE + dbKey + ".driver-class-name");
        String user = context.getProperty(KEY_SPRING_DATASOURCE + dbKey + ".username");
        String pwd = context.getProperty(KEY_SPRING_DATASOURCE + dbKey + ".password");
        int maxPoolSize = context.getIntValue(KEY_SPRING_DATASOURCE + dbKey + ".maximum-pool-size", 50);
        int minIdle = context.getIntValue(KEY_SPRING_DATASOURCE + dbKey + ".minimum-idle", 5);
        long maxLifeTime = context.getLongValue(KEY_SPRING_DATASOURCE + dbKey + ".max-lifetime",
                300000);
        String conTestQuery = context.getProperty(
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

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @ConditionalOnMissingBean
    @Primary
    public SqlSessionFactory sqlSessionFactory(
            @Qualifier("common-dynamicdatasource-DynamicDataSource") DataSource dataSource,
            MybatisProperties properties) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> rl = new ArrayList<>();
        Arrays.asList(properties.getMapperLocations()).forEach(ml -> {
            try {
                Resource[] tr = resolver.getResources(ml);
                if (ArrayUtils.isNotEmpty(tr)) {
                    rl.addAll(Arrays.asList(tr));
                }
            } catch (Exception e) {
                log.warn("未扫描到路径下[{}]下的资源文件", ml);
            }
        });
        if (!rl.isEmpty()) {
            bean.setMapperLocations(rl.stream().toArray(Resource[]::new));
        }
        if (StrUtils.strNotEmpty(properties.getConfigLocation())) {
            bean.setConfigLocation(resolver.getResource(properties.getConfigLocation()));
        }
        if (ArrayUtil.isNotEmpty(properties.getTypeAliasesPackage())) {
            bean.setTypeAliasesPackage(properties.getTypeAliasesPackage());
        }
        return bean.getObject();
    }

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @ConditionalOnMissingBean
    @Primary
    public DataSourceTransactionManager dataSourceTransactionManager(
            @Qualifier("common-dynamicdatasource-DynamicDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @ConditionalOnMissingBean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @ConditionalOnMissingBean
    @Primary
    public JdbcTemplate jdbcTemplate(
            @Qualifier("common-dynamicdatasource-DynamicDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
    @ConditionalOnMissingBean
    @Primary
    public TransactionTemplate transactionTemplate(DataSourceTransactionManager dataSourceTransactionManager) {
        return new TransactionTemplate(dataSourceTransactionManager);
    }

    @ConditionalOnExpression("#{ ${spring.datasource.dynamic.enabled:false} && ${spring.datasource.dynamic.monitor.enabled:false}}")
    @Bean("common-dynamicdatasource-PoolMonitor")
    PoolMonitor poolMonitor(@Qualifier("common-dynamicdatasource-DynamicDataSource") DataSource dataSource) {
        return new PoolMonitor(dataSource);
    }
}
