package com.github.hbq969.code.common.encrypt.ext.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author hbq969@gmail.com
 */
public class JsonUtils {

  private JsonUtils() {
  }

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public static JsonNode getNode(String content, String key) throws IOException {
    JsonNode jsonNode = OBJECT_MAPPER.readTree(content);
    return jsonNode.get(key);
  }

  public static String writeValueAsString(Object body) throws JsonProcessingException {
    return OBJECT_MAPPER.writeValueAsString(body);
  }
}
