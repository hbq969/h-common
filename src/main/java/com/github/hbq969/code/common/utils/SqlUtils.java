package com.github.hbq969.code.common.utils;

import cn.hutool.core.lang.Assert;
import com.github.hbq969.code.common.spring.context.SpringContext;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author : hbq969@gmail.com
 * @description : 数据库处理工具类
 * @createTime : 14:50:19, 2023.03.31, 周五
 */
@Slf4j
public class SqlUtils {

    public static final Pattern SEM_PTN = Pattern.compile("[,;:]");

    public static final Pattern SQL_TABLE_SPEC = Pattern.compile("[A-Z,a-z,0-9,_,$,#]+");

    public static final String SEM = ";";

    /**
     * 将clob字段转换成字符串
     *
     * @param map
     * @param key
     * @return 异常或取不到值都返回<code>null</code>
     */
    public static String clobToString(Map map, String key) {
        Object obj = MapUtils.getObject(map, key);
        return formatClob(key, obj);
    }

    /**
     * 将clob字段转换成字符串
     *
     * @param r
     * @param key
     * @return 异常或取不到值都返回<code>null</code>
     */
    public static String clobToString(ResultSet r, String key) {
        Object obj = null;
        try {
            obj = r.getObject(key);
        } catch (SQLException ex) {
            log.error("", ex);
        }
        return formatClob(key, obj);
    }

    /**
     * 将clob字段转换成字符串
     *
     * @param clob
     * @return 异常或取不到值都返回<code>null</code>
     */
    public static String clobToString(Clob clob) {
        return formatClob("", clob);
    }

    /**
     * 根据指定列拼接查询键值信息
     *
     * @param c        列信息
     * @param alias    别名
     * @param useAlias 是否使用别名
     * @return
     */
    public static String createSelectKey(Collection<String> c, String alias, boolean useAlias) {
        return c.stream().map(fn ->
        {
            String aliasFinal = (StrUtils.strEmpty(alias) ? "" : alias + ".");
            String r;
            if (useAlias) {
                r = String.join("", aliasFinal, fn, " AS \"", fn, "\"");
            } else {
                r = String.join("", aliasFinal, fn);
            }
            return r;
        }).collect(Collectors.joining(","));
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param jt   spring-jdbc模板
     * @param dir  在classpath下相对路径
     * @param file 脚本文件名
     */
    public static void initDataSqlWithDefaultProfile(JdbcTemplate jt, String dir, String file) {
        initDataSql(null, jt, dir, file, null, Charset.defaultCharset());
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param charset 脚本文件编码
     */
    public static void initDataSqlWithDefaultProfile(JdbcTemplate jt, String dir, String file,
                                                     Charset charset) {
        initDataSql(null, jt, dir, file, null, charset);
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param context spring上下文
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param charset 脚本文件编码
     */
    public static void initDataSqlWithDefaultProfile(SpringContext context, JdbcTemplate jt, String dir, String file,
                                                     Charset charset) {
        initDataSql(context, jt, dir, file, null, charset);
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param context spring上下文
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     */
    public static void initDataSqlWithDefaultProfile(SpringContext context, JdbcTemplate jt, String dir, String file) {
        initDataSql(context, jt, dir, file, null, Charset.defaultCharset());
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param profile 环境信息
     */
    public static void initDataSqlWithUTF8Charset(JdbcTemplate jt, String dir, String file, String profile) {
        initDataSql(null, jt, dir, file, profile, Charset.forName("utf-8"));
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param jt   spring-jdbc模板
     * @param dir  在classpath下相对路径
     * @param file 脚本文件名
     */
    public static void initDataSqlWithUTF8Charset(JdbcTemplate jt, String dir, String file) {
        initDataSql(null, jt, dir, file, null, Charset.forName("utf-8"));
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param context spring上下文
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param profile 环境信息
     */
    public static void initDataSqlWithUTF8Charset(SpringContext context, JdbcTemplate jt, String dir, String file, String profile) {
        initDataSql(context, jt, dir, file, profile, Charset.forName("utf-8"));
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param context spring上下文
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     */
    public static void initDataSqlWithUTF8Charset(SpringContext context, JdbcTemplate jt, String dir, String file) {
        initDataSql(context, jt, dir, file, null, Charset.forName("utf-8"));
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param jt      spring-jdbc模板
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param profile 环境信息
     * @param charset 脚本文件编码
     */
    public static void initDataSql(JdbcTemplate jt, String dir, String file, String profile,
                                   Charset charset) {
        initDataSql(null, jt, dir, file, profile, charset);
    }

    /**
     * 初始化xx.sql脚本数据
     *
     * @param context spring上下文，可以为空
     * @param jt      spring-jdbc模板，不能为空
     * @param dir     在classpath下相对路径
     * @param file    脚本文件名
     * @param profile 环境信息
     * @param charset 脚本文件编码
     */
    public static void initDataSql(SpringContext context, JdbcTemplate jt, String dir, String file, String profile,
                                   Charset charset) {
        Assert.notNull(jt, "JdbcTemplate 不能为空");
        try (InputStream in = ResourceUtils.read(dir, file, profile)) {
            boolean execute = false;
            List box = new ArrayList();
            String sql;
            List list = IOUtils.readLines(in, charset.name());
            for (Object line : list) {
                String str = String.valueOf(line).trim();
                if (str.endsWith(SEM)) {
                    str = str.substring(0, str.length() - 1);
                    execute = true;
                }
                box.add(str);
                if (execute) {
                    sql = String.join("\n", box).trim();
                    try {
                        jt.update(sql);
                    } catch (DataAccessException e) {
                        // do nothing
                    }
                    box.clear();
                    execute = false;
                }
            }

        } catch (Exception e) {
            log.error(String.format("读取脚本文件[%]异常", file, e));
        }
    }


    /**
     * 拼接sql的in条件
     *
     * @param ins
     * @param clz
     * @return
     */
    public static String toIns(String ins, Class<?> clz) {
        return toIns(Splitter.on(SEM_PTN).trimResults().splitToList(ins), clz);
    }

    /**
     * 拼接sql的in条件
     *
     * @param ins
     * @param clz
     * @return
     */
    public static String toIns(List<String> ins, Class<?> clz) {
        return ins.stream().map(c -> clz == String.class ? String.join("", "'", c, "'") : c)
                .collect(Collectors.joining(","));
    }

    /**
     * 表名是否符合命名的规范
     *
     * @param tableName
     * @return
     */
    public static boolean isTableNameSpec(String tableName) {
        return SQL_TABLE_SPEC.matcher(tableName).matches();
    }

    /**
     * 判断数据源是否为oracle
     *
     * @param jt
     * @return
     */
    public static boolean isOracle(JdbcTemplate jt) {
        try (Connection c = jt.getDataSource().getConnection()) {
            return c.getMetaData().getURL().contains("oracle");
        } catch (SQLException ex) {
            log.error("判断数据是否为oracle异常", ex);
            return false;
        }
    }

    /**
     * 获取mybatis配置文件
     *
     * @param jt
     * @return
     */
    public static Resource getLocationConfig(JdbcTemplate jt) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return isOracle(jt) ? resolver.getResource("classpath:jpaConfig-oracle.xml")
                : resolver.getResource("classpath:jpaConfig.xml");
    }

    /**
     * 将字符串xml转换成Resource对象
     *
     * @param xml
     * @return
     * @throws IOException
     */
    public static Resource xml2Resource(String xml) throws IOException {
        StringReader in = new StringReader(xml);
        ByteArrayOutputStream out = new ByteArrayOutputStream(8192);
        IOUtils.copy(in, out, Charset.defaultCharset().name());
        ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
        return resource;
    }

    private static String formatClob(String key, Object obj) {
        if (obj == null) {
            return null;
        }
        String txt = null;
        if (obj instanceof Clob) {
            Clob clob = Clob.class.cast(obj);
            try (Reader in = clob.getCharacterStream()) {
                txt = IOUtils.toString(in);
            } catch (Exception e) {
                log.error(String.format("解析clob字段[%s]异常", key), e);
            }
        } else {
            txt = obj.toString();
        }
        return txt;
    }

}
