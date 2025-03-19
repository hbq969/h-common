package com.github.hbq969.code.common.spring.context;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : Spring上下文增强类
 * @createTime : 18:10:23, 2023.03.28, 周二
 */
@Slf4j
public class SpringContext {

    private ApplicationContext context;

    private Environment environment;

    @Qualifier("common-springEnv")
    @Autowired
    private SpringEnv sei;

    public SpringContext(@NonNull ApplicationContext context, @NonNull Environment environment) {
        this.context = context;
        this.environment = environment;
    }

    public <T> Optional<T> getOptionalBean(@NonNull Class<T> requiredType) {
        return Optional.ofNullable(this.context.getBean(requiredType));
    }

    public <T> T getBean(@NonNull Class<T> requiredType) {
        return this.context.getBean(requiredType);
    }

    public <T> T getBean(@NonNull String id, @NonNull Class<T> requiredType) {
        return requiredType.cast(this.context.getBean(id));
    }

    public <T> Optional<T> getOptionalBean(@NonNull String id, @NonNull Class<T> requiredType) {
        return Optional.ofNullable(requiredType.cast(this.context.getBean(id)));
    }

    public <T> Map<String, T> getBeanMapOfType(@NonNull Class<T> requiredType) {
        return this.context.getBeansOfType(requiredType);
    }

    public <T> List<BeanInfo<T>> getBeanListOfType(@NonNull Class<T> requiredType) {
        Map<String, T> map = getBeanMapOfType(requiredType);
        if (map == null) {
            return Collections.emptyList();
        }
        List<BeanInfo<T>> list = new ArrayList<>(map.size());
        map.entrySet().forEach(e -> list.add(new BeanInfo<>(e.getKey(), e.getValue())));
        return list;
    }

    public <T> Optional<T> optional(String key, Class<T> valueType) {
        return Optional.ofNullable(this.environment.getProperty(key, valueType));
    }

    public String getProperty(String key) {
        return this.environment.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return this.environment.getProperty(key, defaultValue);
    }

    public short getShortValue(String key, short defaultValue) {
        String v = this.environment.getProperty(key);
        return Objects.isNull(v) ? defaultValue : Short.valueOf(v).shortValue();
    }

    public int getIntValue(String key, int defaultValue) {
        String v = this.environment.getProperty(key);
        return Objects.isNull(v) ? defaultValue : Integer.valueOf(v).intValue();
    }

    public long getLongValue(String key, long defaultValue) {
        String v = this.environment.getProperty(key);
        return Objects.isNull(v) ? defaultValue : Long.valueOf(v).longValue();
    }

    public float getFloatValue(String key, float defaultValue) {
        String v = this.environment.getProperty(key);
        return Objects.isNull(v) ? defaultValue : Float.valueOf(v).floatValue();
    }

    public double getDoubleValue(String key, double defaultValue) {
        String v = this.environment.getProperty(key);
        return Objects.isNull(v) ? defaultValue : Double.valueOf(v).doubleValue();
    }

    public boolean getBoolValue(String key, boolean defaultValue) {
        String v = this.environment.getProperty(key);
        return Objects.isNull(v) ? defaultValue : Boolean.valueOf(v).booleanValue();
    }

    /**
     * 返回spring所有配置属性key，包括SystemProperties和系统env
     *
     * @return
     */
    public Set<String> getEnvironmentPropertyKeys() {
        return this.sei.getEnvironmentPropertyKeys();
    }

    /**
     * 等待指定的类型实例化bean初始化完成
     *
     * @param clzList 类型列表
     */
    public void awaitBeanInitWithType(List<Class<?>> clzList, InitCompletedCallBack c) {
        awaitBeanInitWithType(clzList, 0, null, c);
    }

    /**
     * 等待指定的类型实例化bean初始化完成
     *
     * @param clzList 类型列表
     * @param time    超时等待时间值
     * @param unit    超时等待时间单位
     */
    public void awaitBeanInitWithType(List<Class<?>> clzList, long time, TimeUnit unit, InitCompletedCallBack c) {
        CompletableFuture.runAsync(() -> {
            if (CollectionUtils.isEmpty(clzList)) {
                throw new IllegalArgumentException("提供的类型列表为空");
            }
            int size = CollectionUtils.size(clzList);
            List<CompletableFuture> fs = new ArrayList<>(size);
            clzList.forEach(clz -> {
                CompletableFuture f = CompletableFuture.runAsync(() -> {
                    Object obj = getBean(clz);
                    if(log.isDebugEnabled())
                        log.debug("{}, {} 等待初始化完成。", clz, obj);
                });
                fs.add(f);
            });
            awaitAll(fs, time, unit);
            c.completed();
        });
    }

    private static void awaitAll(List<CompletableFuture> fs, long time, TimeUnit unit) {
        CompletableFuture[] array = new CompletableFuture[fs.size()];
        fs.toArray(array);
        try {
            if (time > 0 && Objects.nonNull(unit)) {
                CompletableFuture.allOf(array).get(time, unit);
            } else {
                CompletableFuture.allOf(array).get();
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

    /**
     * 等待指定的id的实例化bean初始化完成
     *
     * @param ids 对应实例的id
     */
    public void awaitBeanInitWithId(List<String> ids, InitCompletedCallBack c) {
        awaitBeanInitWithId(ids, 0, null, c);
    }

    /**
     * 等待指定的id的实例化bean初始化完成
     *
     * @param ids  对应实例的id
     * @param time 超时等待时间值
     * @param unit 超时等待时间单位
     */
    public void awaitBeanInitWithId(List<String> ids, long time, TimeUnit unit, InitCompletedCallBack c) {
        CompletableFuture.runAsync(() -> {
            if (CollectionUtils.isEmpty(ids)) {
                throw new IllegalArgumentException("提供的bean id列表为空");
            }
            int size = CollectionUtils.size(ids);
            List<CompletableFuture> fs = new ArrayList<>(size);
            ids.forEach(id -> {
                CompletableFuture f = CompletableFuture.runAsync(() -> {
                    Object obj = context.getBean(id);
                    log.debug("{}, {} 等待初始化完成。", id, obj);
                });
                fs.add(f);
            });
            awaitAll(fs, time, unit);
            c.completed();
        });
    }

    public ApplicationContext getContext() {
        return context;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
