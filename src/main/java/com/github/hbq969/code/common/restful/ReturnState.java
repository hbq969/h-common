package com.github.hbq969.code.common.restful;

/**
 * @author : hbq969@gmail.com
 * @description : 结果枚举
 * @createTime : 2023/6/26 14:48
 */
public enum ReturnState {
  OK("OK"), ERROR("ERROR"), EXCEPTION("EXCEPTION"), FORBIDDEN("FORBIDDEN");

  private String value;

  private ReturnState(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String toString() {
    return this.value;
  }

  public boolean ok() {
    return "OK".equals(value);
  }
}
