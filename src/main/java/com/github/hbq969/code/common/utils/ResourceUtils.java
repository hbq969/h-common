package com.github.hbq969.code.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : hbq969@gmail.com
 * @description : 计数器
 * @createTime : 14:50:19, 2023.03.31, 周五
 */
@Slf4j
public class ResourceUtils {

    public static final String PROFILE_DEFAULT = "default";

    public static final String BIN_VM = "/bin/vm";
    public static final String BIN_DOCKER = "/bin/docker";

    public static InputStream read(String dir, String file, String profile) {
        dir = (dir.endsWith("/") ? dir : String.join("", dir, "/"));
        String path;
        if (StringUtils.isEmpty(profile) || PROFILE_DEFAULT.equals(profile)) {
            path = String.join("", dir, file);
            log.info("读取文件: {}", path);
            return ResourceUtils.class.getResourceAsStream(path);
        } else {
            String[] array = file.split("\\.");
            if (array.length != 2) {
                throw new IllegalArgumentException();
            }
            path = String.join("", dir, array[0], "-", profile, ".", array[1]);
            InputStream in = ResourceUtils.class.getResourceAsStream(path);
            if (null == in) {
                path = String.join("", dir, file);
                log.info("读取文件: {}", path);
                return ResourceUtils.class.getResourceAsStream(path);
            } else {
                log.info("读取文件: {}", path);
                return in;
            }
        }
    }

    public static List<String> lines(String dir, String file, String profile) {
        try (InputStream in = read(dir, file, profile)) {
            return IOUtils.readLines(in, "utf-8");
        } catch (Exception e) {
            log.error(String.format("读取脚本文件[%s%s]异常", dir, file), e);
            return Collections.emptyList();
        }
    }

    public static InputStream readFile(String path) {
        Assert.notNull(path, "读取的文件路径不能为空");
        String clzPath = MessageFormat.format("classpath:{0}", path);
        log.debug("读取文件: {}", clzPath);
        try {
            File file = org.springframework.util.ResourceUtils.getFile(clzPath);
            return FileUtil.getInputStream(file);
        } catch (FileNotFoundException e) {
            if (!path.startsWith("/")) {
                path = MessageFormat.format("/{0}", path);
            }
            log.debug("读取文件: {}", path);
            return ResourceUtils.class.getResourceAsStream(path);
        }
    }

    public static Map<String, String> yml2Properties(String content) {
        final String DOT = ".";
        Map<String, String> props = new HashMap<>(500);
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLParser parser = yamlFactory.createParser(content);
            String key = "";
            String value;
            JsonToken token = parser.nextToken();
            while (token != null) {
                if (JsonToken.START_OBJECT.equals(token)) {
                    // "{" 表示字符串开头，不做解析
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    if (key.length() > 0) {
                        key = key + DOT;
                    }
                    key = key + parser.getCurrentName();
                    token = parser.nextToken();
                    if (JsonToken.START_OBJECT.equals(token)) {
                        continue;
                    }
                    value = parser.getText();
                    props.put(key, value);
//          lines.add(key + "=" + value);
                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = key.substring(0, dotOffset);
                    }
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = key.substring(0, dotOffset);
                    } else {
                        key = "";
//            lines.add("");
                    }
                }
                token = parser.nextToken();
            }
            parser.close();
            log.info("{}", GsonUtils.toJson(props));
            return props;
        } catch (Exception e) {
            log.error("解析yml内容异常", e);
            throw new RuntimeException(e);
        }

    }

    public static void properties2Yaml(String file) {
        JsonParser parser = null;
        JavaPropsFactory factory = new JavaPropsFactory();
        try {
            parser = factory.createParser(
                    "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLGenerator generator = yamlFactory.createGenerator(
                    new OutputStreamWriter(new FileOutputStream(file), Charset.forName("utf-8")));

            JsonToken token = parser.nextToken();

            while (token != null) {
                if (JsonToken.START_OBJECT.equals(token)) {
                    generator.writeStartObject();
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    generator.writeFieldName(parser.getCurrentName());
                } else if (JsonToken.VALUE_STRING.equals(token)) {
                    generator.writeString(parser.getText());
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    generator.writeEndObject();
                }
                token = parser.nextToken();
            }
            parser.close();
            generator.flush();
            generator.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
