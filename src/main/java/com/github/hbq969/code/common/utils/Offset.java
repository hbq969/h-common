package com.github.hbq969.code.common.utils;

import lombok.Data;

/**
 * @author : hbq969@gmail.com
 * @description : 区间包装器
 * @createTime : 15:01:34, 2023.03.31, 周五
 */
@Data
public class Offset {

  private long beginLongValue;
  private long endLongValue;
  private int beginIntValue;
  private int endIntValue;
}
