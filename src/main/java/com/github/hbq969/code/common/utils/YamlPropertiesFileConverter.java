package com.github.hbq969.code.common.utils;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.hbq969.code.common.spring.yaml.TypePair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class YamlPropertiesFileConverter {

    private static final Pattern ARRAY_PATTERN = Pattern.compile("\\[(\\d+)\\]$");
    private static YamlPropertySourceLoader loader = new YamlPropertySourceLoader();

    public static List<TypePair> yamlToProperties(String yamlString) {
        Map<String, Object> yamlMap = new HashMap<>();
        List<PropertySource<?>> list = null;
        try {
            list = loader.load("utils", new ByteArrayResource(yamlString.getBytes()));
            if (CollectionUtils.isNotEmpty(list)) {
                yamlMap = (Map<String, Object>) list.get(0).getSource();
            }
        } catch (IOException e) {
            log.error("解析yaml内容异常", e);
        }
        List<TypePair> pairs = new ArrayList<>();
        TypePair pair;
        String key;
        if (MapUtils.isNotEmpty(yamlMap)) {
            for (Map.Entry<String, Object> e : yamlMap.entrySet()) {
                key = String.valueOf(e.getKey());
                OriginTrackedValue otv = (OriginTrackedValue) e.getValue();
                if (otv == null) {
                    pair = new TypePair(key, null, String.class.getName());
                } else {
                    Object value = otv.getValue();
                    Class<?> clz = value == null ? String.class : value.getClass();
                    pair = new TypePair(key, value, clz.getName());
                }
                pairs.add(pair);
            }
        }
        return pairs;
    }

    public static String propertiesToYaml(List<TypePair> pairs) {
        // 禁用所有自动引号生成
        YAMLFactory yamlFactory = new YAMLFactory()
                .disable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);

        ObjectMapper yamlMapper = new ObjectMapper(yamlFactory);
        Map<String, Object> yamlMap = buildStrictStructure(pairs);

        try {
            return yamlMapper.writeValueAsString(yamlMap);
        } catch (Exception e) {
            throw new RuntimeException("YAML生成异常", e);
        }
    }

    private static Map<String, Object> buildStrictStructure(List<TypePair> pairs) {
        Map<String, Object> rootMap = new LinkedHashMap<>();

        pairs.forEach(pair -> {
            String[] keys = pair.getKey().split("\\.");
            Object value = pair.getValue();

            Map<String, Object> currentMap = rootMap;
            for (int i = 0; i < keys.length - 1; i++) {
                currentMap = handleNode(currentMap, keys[i]);
            }
            assignStrictValue(currentMap, keys[keys.length - 1], value);
        });

        return rootMap;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> handleNode(Map<String, Object> map, String key) {
        Matcher m = ARRAY_PATTERN.matcher(key);
        if (m.find()) {
            String base = key.substring(0, m.start());
            int idx = Integer.parseInt(m.group(1));

            List<Map<String, Object>> list = (List<Map<String, Object>>)
                    map.computeIfAbsent(base, k -> new ArrayList<>());

            while (list.size() <= idx) list.add(new LinkedHashMap<>());
            return list.get(idx);
        }
        return (Map<String, Object>) map.computeIfAbsent(key, k -> new LinkedHashMap<>());
    }

    private static void assignStrictValue(Map<String, Object> map, String key, Object value) {
        Matcher m = ARRAY_PATTERN.matcher(key);
        if (m.find()) {
            String base = key.substring(0, m.start());
            int idx = Integer.parseInt(m.group(1));

            List<Object> list = (List<Object>)
                    map.computeIfAbsent(base, k -> new ArrayList<>());

            while (list.size() <= idx) list.add(null);
            list.set(idx, value);
        } else {
            map.put(key, value);
        }
    }

    public static void main(String[] args) throws Exception {
        List<TypePair> pairs = yamlToProperties(IoUtil.read(new ClassPathResource("application.yml").getInputStream(), StandardCharsets.UTF_8));
        for (TypePair pair : pairs) {
            System.out.println(pair.getKey() + "=" + pair.getValue() + ", " + pair.getType().getName());
        }
        System.out.println();
        System.out.println();
        String yamlString = propertiesToYaml(pairs);
        System.out.println(yamlString);
    }
}