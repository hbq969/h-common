package com.github.hbq969.code.common.utils;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 初始化sql工具类
 * @createTime : 2024/10/13 13:56
 */
@Slf4j
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

    public static void initial(SpringContext context, String filename, Charset charset, SqlFormatter format, Runnable endCall) {
        JdbcTemplate jt = context.getBean(JdbcTemplate.class);
        Assert.notNull(filename, "脚本文件不能为空");
        if (!filename.startsWith("/")) {
            filename = "/" + filename;
        }
        if (log.isDebugEnabled()) {
            log.debug("读取脚本文件: {}", filename);
        }
        try (InputStream in = InitScriptUtils.class.getResourceAsStream(filename)) {
            if (in == null) {
                log.warn("{} 不存在，请检查。", filename);
                return;
            }
            boolean execute = false;
            List box = new ArrayList();
            String sql;
            List list = IOUtils.readLines(in, charset.name());
            for (Object line : list) {
                String str = String.valueOf(line).trim();
                if (str.endsWith(";")) {
                    str = str.substring(0, str.length() - 1);
                    execute = true;
                }
                box.add(str);
                if (execute) {
                    sql = String.join("\n", box).trim();
                    try {
                        if (format != null) {
                            sql = format.format(sql);
                        }
                        jt.update(sql);
                    } catch (DataAccessException e) {
                        // do nothing
                    }
                    box.clear();
                    execute = false;
                }
            }

        } catch (Exception e) {
            log.error(String.format("读取脚本文件[%s]异常", filename), e);
        } finally {
            if (endCall != null) {
                try {
                    endCall.run();
                } catch (Exception e) {
                    log.error(String.format("初始化脚本文件 %s 回调方法异常", filename), e);
                }
            }
        }
    }
}
