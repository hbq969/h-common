package com.github.hbq969.code.common.initial;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
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
        doCreateWithAnnotation();
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

    private void doCreateWithAnnotation() {
        Map<String, Object> map = this.c.getBeansWithAnnotation(EnableTableCreate.class);
        if (MapUtil.isEmpty(map))
            return;
        String clz, mn;
        for (Object target : map.values()) {
            Method[] ms = ReflectionUtils.getDeclaredMethods(target.getClass());
            clz = target.getClass().getName();
            for (Method m : ms) {
                if (AnnotationUtils.findAnnotation(m, EnableTableCreate.class) == null)
                    continue;
                mn = m.getName();
                if (log.isDebugEnabled())
                    log.debug("调用 {}.{} ", clz, mn);
                try {
                    ReflectionUtils.invokeMethod(m, target);
                } catch (Exception e) {
                    log.error("调用 {}.{} 异常 {}", clz, mn, e.getMessage());
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.c = applicationContext;
    }
}
