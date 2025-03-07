package com.github.hbq969.code.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import com.google.common.base.Joiner;
import org.apache.commons.collections4.CollectionUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class YamlPropertiesFileConverter {

    public static List<Pair<String, Object>> yamlToProperties(String yamlString) {
        Yaml yaml = new Yaml();
        Map<String, Object> yamlMap = yaml.load(yamlString);
        List<Pair<String, Object>> pairs = new ArrayList<>();
        flattenMap(yamlMap, "", pairs);
        return pairs;
    }

    private static void flattenMap(Map<String, Object> map, String prefix, List<Pair<String, Object>> pairs) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                flattenMap((Map<String, Object>) value, key, pairs);
            } else if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (list.get(0) instanceof String) {
                    pairs.add(new Pair<>(key, Joiner.on(',').join(list)));
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        Object item = list.get(i);
                        if (item instanceof Map) {
                            flattenMap((Map<String, Object>) item, key + "[" + i + "]", pairs);
                        } else {
                            pairs.add(new Pair<>(key, String.valueOf(item)));
                        }
                    }
                }
            } else {
                pairs.add(new Pair<>(key, String.valueOf(value)));
            }
        }
    }

    public static String propertiesToYaml(List<Pair<String, Object>> pairs) {
        if (CollectionUtils.isEmpty(pairs)) {
            return null;
        }
        Properties properties = new Properties();
        for (Pair<String, Object> pair : pairs) {
            properties.setProperty(pair.getKey(), String.valueOf(pair.getValue()));
        }

        Map<String, Object> yamlMap = convertMap(properties);

        // 将properties转换为Map
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        String yamlString = yaml.dump(yamlMap);

        return yamlString;
    }

    private static Map<String, Object> convertMap(Properties map) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry entry : map.entrySet()) {
            String[] keys = entry.getKey().toString().split("\\."); // 按照"."分割
            putValue(result, keys, 0, entry.getValue());
        }
        return result;
    }

    // 递归设置值
    private static void putValue(Map<String, Object> result, String[] keys, int index, Object value) {
        if (index == keys.length) {
            return;
        }

        String key = keys[index];
        if (key.contains("[")) {
            // 处理数组，提取出基准键和索引
            String baseKey = key.substring(0, key.indexOf("["));
            int arrayIndex = Integer.parseInt(key.substring(key.indexOf("[") + 1, key.indexOf("]")));

            // 获取或创建数组
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.computeIfAbsent(baseKey, k -> new ArrayList<>());

            // 确保数组长度
            while (list.size() <= arrayIndex) {
                list.add(new HashMap<>());
            }

            // 递归继续处理
            putValue(list.get(arrayIndex), Arrays.copyOfRange(keys, index + 1, keys.length), 0, value);
        } else {
            // 普通键值对
            if (index == keys.length - 1) {
                // 如果值是字符串且包含逗号，则拆分为列表
                if (value instanceof String && ((String) value).contains(",")) {
                    result.put(key, Arrays.asList(((String) value).split(",")));
                } else {
                    result.put(key, value);
                }
            } else {
                // 嵌套map
                Map<String, Object> subMap = (Map<String, Object>) result.computeIfAbsent(key, k -> new HashMap<>());
                putValue(subMap, Arrays.copyOfRange(keys, index + 1, keys.length), 0, value);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        List<Pair<String, Object>> pairs = yamlToProperties(FileUtil.readString("/Users/hbq/Codes/tmp/h-example/src/main/resources/application.yml", StandardCharsets.UTF_8));
        for (Pair<String, Object> pair : pairs) {
            System.out.println(pair.getKey() + ": " + pair.getValue());
        }

        String yamlString = propertiesToYaml(pairs);
        System.out.println();
        System.out.println();
        System.out.println(yamlString);
    }
}