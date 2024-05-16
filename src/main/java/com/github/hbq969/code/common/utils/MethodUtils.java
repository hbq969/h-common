package com.github.hbq969.code.common.utils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author : hbq969@gmail.com
 * @description : 方法工具类
 * @createTime : 2023/10/19 22:39
 */
public final class MethodUtils {
    private static final Set<String> OBJECT_METHOD_NAMES = new HashSet<>();

    static {
        Method[] ms = Object.class.getDeclaredMethods();
        for (Method m : ms) {
            OBJECT_METHOD_NAMES.add(m.getName());
        }
    }

    public static boolean isObjectMethod(String name) {
        if (Objects.isNull(name)) {
            return false;
        }
        return OBJECT_METHOD_NAMES.contains(name);
    }

    public static boolean isObjectMethod(Method m) {
        if (Objects.isNull(m)) {
            return false;
        }
        return OBJECT_METHOD_NAMES.contains(m.getName());
    }

    public static boolean isNotObjectMethod(String name) {
        return !isObjectMethod(name);
    }

    public static boolean isNotObjectMethod(Method m) {
        return !isObjectMethod(m);
    }
}
