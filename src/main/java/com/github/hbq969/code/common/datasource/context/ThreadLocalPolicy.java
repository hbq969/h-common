package com.github.hbq969.code.common.datasource.context;

/**
 * @author : hbq969@gmail.com
 * @description : 存储策略的线程上下文
 * @createTime : 16:50:37, 2023.04.02, 周日
 */
public final class ThreadLocalPolicy {

  private static ThreadLocal<ContextPolicy> ctx = new ThreadLocal<>();

  public static void set(ContextPolicy policy) {
    ctx.set(policy);
  }

  public static ContextPolicy get() {
    return ctx.get();
  }

  public static void remove() {
    ctx.remove();
  }
}
