package com.github.hbq969.code.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

@Slf4j
public final class ThrowUtils {
    public static void call(String s, String f, Runnable r) {
        try {
            r.run();
            log.debug(s);
        } catch (Exception e) {
            log.info(f);
        }
    }

    public static <V> V call(Callable<V> c, Consumer<Throwable> s) {
        try {
            return c.call();
        } catch (Throwable ex) {
            s.accept(ex);
            return null;
        }
    }
}
