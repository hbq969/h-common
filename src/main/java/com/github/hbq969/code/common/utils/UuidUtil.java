package com.github.hbq969.code.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * @author : hbq969@gmail.com
 * @description : UUID工具类
 * @createTime : 2023/6/26 14:50
 */
public class UuidUtil {

  public static synchronized String uuid() {
    return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
  }
}
