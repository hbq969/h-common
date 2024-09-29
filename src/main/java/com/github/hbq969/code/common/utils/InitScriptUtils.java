package com.github.hbq969.code.common.utils;

import com.github.hbq969.code.common.spring.context.SpringContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author : hbq969@gmail.com
 * @description : 初始化sql工具类
 * @createTime : 2024/10/13 13:56
 */
public final class InitScriptUtils {
    public static void initial(SpringContext ctx, String classpathDir, String file, String profile, Charset charset, Runnable r) {
        SqlUtils.initDataSql(ctx, null, classpathDir, file, profile, charset, r);
    }

    public static void initial(JdbcTemplate jt, String classpathDir, String file, String profile, Charset charset, Runnable r) {
        SqlUtils.initDataSql(null, jt, classpathDir, file, profile, charset, r);
    }

    public static void initial(SpringContext ctx, String classpathDir, String file, Runnable r) {
        SqlUtils.initDataSql(ctx, null, classpathDir, file, null, StandardCharsets.UTF_8, r);
    }

    public static void initial(SpringContext ctx, String file, Runnable r) {
        SqlUtils.initDataSql(ctx, null, "/", file, null, StandardCharsets.UTF_8, r);
    }
}
