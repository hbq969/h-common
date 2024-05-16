package com.github.hbq969.code.common.encrypt.model;

import lombok.Data;

/**
 * @author : hbq969@gmail.com
 * @description : 加密信息
 * @createTime : 2023/6/25 21:33
 */
@Data
public class EncryptInfo {

  private String key;
  private String data;
}
