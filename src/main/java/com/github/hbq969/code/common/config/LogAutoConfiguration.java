package com.github.hbq969.code.common.config;

import com.github.hbq969.code.common.log.aop.LogBeanProcessor;
import com.github.hbq969.code.common.log.aop.OperlogAspect;
import com.github.hbq969.code.common.log.collect.LogServiceFacade;
import com.github.hbq969.code.common.log.collect.DbLogServiceImpl;
import com.github.hbq969.code.common.log.spi.DefaultLogCollect;
import com.github.hbq969.code.common.log.spi.DefaultLogModelDefProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@Slf4j
public class LogAutoConfiguration {
    @Bean("common-LogProperties")
    LogProperties logConf() {
        return new LogProperties();
    }

    @ConditionalOnProperty(prefix = "operlog", name = "enabled", havingValue = "true")
    @Bean("common-LogBeanProcessor")
    LogBeanProcessor logBeanProcessor() {
        return new LogBeanProcessor();
    }

    @ConditionalOnProperty(prefix = "operlog", name = "enabled", havingValue = "true")
    @Bean("common-OperlogAspect")
    OperlogAspect operlogAspect() {
        return new OperlogAspect();
    }

    @ConditionalOnProperty(prefix = "operlog", name = "enabled", havingValue = "true")
    @Bean("common-LogServiceFacade")
    LogServiceFacade logServiceFacade() {
        return new LogServiceFacade();
    }

    @ConditionalOnProperty(prefix = "operlog", name = "enabled", havingValue = "true")
    @Bean("common-MysqlLogServiceImpl")
    DbLogServiceImpl mysqlLogService() {
        return new DbLogServiceImpl();
    }

    @ConditionalOnExpression("#{ ${operlog.enabled:false} && ${operlog.use-default-collect-policy:false} && '${operlog.policy}'.equals('mysql')}")
    @Bean("common-DefaultLogModelDefProvider")
    DefaultLogModelDefProvider defaultLogModelDefProvider() {
        return new DefaultLogModelDefProvider();
    }

    @ConditionalOnExpression("#{ ${operlog.enabled:false} && ${operlog.use-default-collect-policy:false} && '${operlog.policy}'.equals('oracle')}")
    @Bean("common-oracle-DefaultLogModelDefProvider")
    com.github.hbq969.code.common.log.spi.oracle.DefaultLogModelDefProvider defaultOracleLogModelDefProvider() {
        return new com.github.hbq969.code.common.log.spi.oracle.DefaultLogModelDefProvider();
    }

    @ConditionalOnExpression("#{ ${operlog.enabled:false} && ${operlog.use-default-collect-policy:false} && ('${operlog.policy}'.equals('embedded') || '${operlog.policy}'.equals('postgresql'))}")
    @Bean("common-embedded-DefaultLogModelDefProvider")
    com.github.hbq969.code.common.log.spi.embedded.DefaultLogModelDefProvider defaultEmbeddedLogModelDefProvider() {
        return new com.github.hbq969.code.common.log.spi.embedded.DefaultLogModelDefProvider();
    }

    @ConditionalOnExpression("#{ ${operlog.enabled:false} && ${operlog.use-default-collect-policy:false}}")
    @Bean("common-DefaultLogCollect")
    DefaultLogCollect defaultLogCollect() {
        return new DefaultLogCollect();
    }
}
