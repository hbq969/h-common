package com.github.hbq969.code.common.utils;

import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.Optional;

/**
 * @author : hbq969@gmail.com
 * @description : 自定义注解工具类
 * @createTime : 2023/10/22 12:26
 */
public final class AnnotationUtils {

    /**
     * 从一组接口中查询是否存在有指定注解类型的接口
     *
     * @param inters         一组接口
     * @param annotationType 注解类型
     * @return
     */
    public static <T extends Annotation> Optional<Class<?>> getAnnotationFromInterface(Class<?>[] inters, Class<T> annotationType) {
        T type;
        for (Class<?> c : inters) {
            type = org.springframework.core.annotation.AnnotationUtils.findAnnotation(c, annotationType);
            if (Objects.nonNull(type)) {
                return Optional.of(c);
            }
        }
        return Optional.empty();
    }
}
