package com.github.hbq969.code.common.spring.context;

/**
 * @author : hbq969@gmail.com
 * @description : Spring上下文增强类
 * @createTime : 18:11:45, 2023.03.28, 周二
 */
public class BeanInfo<T> {

  private String id;
  private T bean;

  public BeanInfo(String id, T bean) {
    this.id = id;
    this.bean = bean;
  }

  public String getId() {
    return id;
  }

  public T getBean() {
    return bean;
  }
}
