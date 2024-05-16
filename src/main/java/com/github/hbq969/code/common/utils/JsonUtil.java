/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT China Mobile (SuZhou) Software Technology Co.,Ltd. 2016
 *
 * The copyright to the computer program(s) herein is the property of
 * CMSS Co.,Ltd. The programs may be used and/or copied only with written
 * permission from CMSS Co.,Ltd. or in accordance with the terms and conditions
 * stipulated in the agreement/contract under which the program(s) have been
 * supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.github.hbq969.code.common.utils;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * JSON处理工具类
 */
public class JsonUtil {

  /**
   * 通用ObjectMapper
   */
  private static ObjectMapper mapper = JsonMapper.builder()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // json->bean ,忽略bean中不存在的属性
      .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES) // 解析器支持解析单引号
      .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS) // 解析器支持解析特殊字符
      .build();

  private static ObjectMapper mapperChange = JsonMapper.builder()
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // json->bean ,忽略bean中不存在的属性
      .enable(JsonReadFeature.ALLOW_SINGLE_QUOTES) // 解析器支持解析单引号
      .enable(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS) // 解析器支持解析特殊字符
      .build();

  /**
   * Bean->JSON bytes
   *
   * @param bean
   * @return JSON bytes
   */
  public static byte[] toJsonBytes(final Object bean) {

    try {
      return mapper.writeValueAsBytes(bean);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }

  }

  /**
   * Bean->JSON
   *
   * @param bean
   * @return JSON
   */
  public static String toJson(final Object bean) {
    if (bean == null) {
      return null;
    }
    try {
      return mapper.writeValueAsString(bean);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }

  }

  /**
   * JSON bytes->BEAN
   *
   * @param json  bytes
   * @param clazz
   * @return bean
   */
  public static <T> T toBean(final byte[] json, final Class<T> clazz) {

    if (null == json) {
      return null;
    }
    try {
      return mapper.readValue(json, clazz);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }

  }


  /**
   * JSON JsonNode->BEAN
   *
   * @param json  bytes
   * @param clazz
   * @return bean
   */
  public static <T> T toBean(JsonNode json, final Class<T> clazz) {

    if (null == json) {
      return null;
    }
    try {
      return mapper.treeToValue(json, clazz);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * JSON JsonNode->BEAN
   *
   * @param json bytes
   * @param type
   * @return bean
   */
  public static <T> T toBean(JsonNode json, final Type type) {

    if (null == json) {
      return null;
    }
    try {
      return mapper.readValue(toJson(json), TypeFactory.defaultInstance().constructType(type));
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * JSON->BEAN
   *
   * @param json
   * @param clazz
   * @return bean
   */
  public static <T> T toBean(final String json, final Class<T> clazz) {

    if (null == json) {
      return null;
    }
    try {
      return mapper.readValue(json, clazz);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }

  }

  /**
   * JSON->List
   *
   * @param json
   * @param clazz
   * @return list
   */
  public static <T> List<T> toList(final String json, final Class<T> clazz) {

    if (null == json) {
      return null;
    }
    try {
      return mapper.readValue(json,
          mapper.getTypeFactory().constructCollectionType(List.class, clazz));
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * JSON->List
   *
   * @param json
   * @param clazz
   * @return list
   */
  public static <T> List<T> toListUnderscore(final String json, final Class<T> clazz) {

    if (null == json) {
      return null;
    }
    try {
      mapperChange.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
      return mapperChange.readValue(json,
          mapperChange.getTypeFactory().constructCollectionType(List.class, clazz));
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * Bean->Bean
   *
   * @param source
   * @param target
   * @return target bean
   */
  public static <T> T convertBean(final Object source, final Class<T> target) {

    return toBean(toJson(source), target);
  }

  /**
   * JSON->JsonNode
   *
   * @param json
   * @return JsonNode
   */
  public static JsonNode toJsonNode(final String json) {

    if (null == json) {
      return null;
    }
    try {

      return mapper.readTree(json);
    } catch (final Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static Type type(final Class<?> raw, final Type... args) {
    return new ParameterizedType() {
      @Override
      public Type getRawType() {
        return raw;
      }

      @Override
      public Type[] getActualTypeArguments() {
        return args;
      }

      @Override
      public Type getOwnerType() {
        return null;
      }
    };
  }

  public static ObjectMapper getObjectMapper() {
    return mapper;
  }
}
