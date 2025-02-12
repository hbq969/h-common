package com.github.hbq969.code.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import com.google.common.base.Joiner;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public static void main(String[] args) {
        List<Pair<String, Object>> pairs = yamlToProperties(FileUtil.readString("/Users/hbq/Downloads/dbc-config/application.yml", StandardCharsets.UTF_8));
        for (Pair<String, Object> pair : pairs) {
            System.out.println(pair.getKey() + ": " + pair.getValue());
        }
    }
}