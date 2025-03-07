package com.github.hbq969.code.common.datasource;

import com.github.hbq969.code.common.mybatis.WafMapperScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;

/**
 * @author : hbq969@gmail.com
 * @description : 动态多数据源Mapper接口扫描
 * @createTime : 13:02:13, 2023.03.30, 周四
 */
@WafMapperScan(basePackages = {"${spring.datasource.dynamic.base-packages}"})
@ConditionalOnExpression("${spring.datasource.dynamic.enabled:false}")
@Slf4j
public class DynamicMapperScan implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("初始化Mapper接口扫描器DynamicMapperScan。");
        }
    }
}
