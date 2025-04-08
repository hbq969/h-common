package com.github.hbq969.code.common.utils;

import org.slf4j.MDC;

public final class MDCUtils {
    public static void set(String key, String value) {
        MDC.put(key, value);
    }

    public static String rmAndGet(String key) {
        String value = MDC.get(key);
        MDC.remove(key);
        return value;
    }
}
