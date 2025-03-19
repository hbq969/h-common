package com.github.hbq969.code.common.initial;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ScriptInitialProcessor implements ApplicationContextAware, InitializingBean {

    private ApplicationContext c;

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, ScriptInitialAware> map = this.c.getBeansOfType(ScriptInitialAware.class);
        if (MapUtil.isEmpty(map))
            return;
        Collection<ScriptInitialAware> values = map.values();
        List<ScriptInitialAware> sorted = values.stream().sorted(Comparator.comparingInt(ScriptInitialAware::orderOfScriptInitialAware)).collect(Collectors.toList());
        for (ScriptInitialAware aware : sorted) {
            if (log.isDebugEnabled()) {
                log.debug("< 执行表创建: {}", aware.nameOfScriptInitialAware());
            }
            try {
                aware.tableCreate();
            } catch (Exception e) {
                log.error("执行表初始化异常", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("> 执行表创建结束: {}", aware.nameOfScriptInitialAware());
            }
            if (log.isDebugEnabled()) {
                log.debug("< 执行表初始化: {}", aware.nameOfScriptInitialAware());
            }
            try {
                aware.scriptInitial();
            } catch (Exception e) {
                log.error("执行表初始化异常", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("> 执行表初始化结束: {}", aware.nameOfScriptInitialAware());
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.c = applicationContext;
    }
}
