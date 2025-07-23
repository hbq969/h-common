package com.github.hbq969.code.common.utils;

import lombok.extern.slf4j.Slf4j;

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
}
