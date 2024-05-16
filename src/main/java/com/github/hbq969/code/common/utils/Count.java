package com.github.hbq969.code.common.utils;

/**
 * @author : hbq969@gmail.com
 * @description : 计数器
 * @createTime : 14:58:23, 2023.03.31, 周五
 */
public abstract class Count {

  public static Count safe() {
    return new SafeCount();
  }

  public static Count unsafe() {
    return new UnSafeCount();
  }

  /**
   * 重置
   */
  public abstract void reset();

  /**
   * 是否初始值
   *
   * @return true/false
   */
  public abstract boolean isInitValue();

  /**
   * 累加并返回计算后的值
   *
   * @return long型整数
   */
  public abstract long incrementAndGet();

  /**
   * 减值并返回计算后的值
   *
   * @return long型整数
   */
  public abstract long decrementAndGet();

  /**
   * 累加指定值并返回计算后的值
   *
   * @param c
   * @return long型整数
   */
  public abstract long incrementAndGet(long c);

  /**
   * 减少指定值并返回计算后的值
   *
   * @param c
   * @return long型整数
   */
  public abstract long decrementAndGet(long c);

  /**
   * 比较并且重置初始值
   *
   * @param actual
   * @return 比较结果
   */
  public abstract boolean compareAndInit(long actual);

  /**
   * 比较并且重置初始值
   *
   * @param actual
   * @param elapsedMills
   * @return 比较结果
   */
  public abstract boolean compareAndInit(long actual, long elapsedMills);

  /**
   * 比较余数是否为0
   *
   * @param actual
   * @return 比较结果
   */
  public abstract boolean compare(long actual);

  /**
   * 比较余数是否为0
   *
   * @param actual
   * @param elapsedMills
   * @return 比较结果
   */
  public abstract boolean compare(long actual, long elapsedMills);

  /**
   * 返回当前值
   *
   * @return long型整数
   */
  public abstract long longValue();

  /**
   * 返回当前值整数
   *
   * @return int型整数
   */
  public abstract int intValue();

  /**
   * 是否在分页范围内
   *
   * @param startRow
   * @param endRow
   * @return
   */
  public abstract boolean pageScope(int startRow, int endRow);

  private static class SafeCount extends Count {

    private long count = 0L;

    private long time = FormatTime.nowMills();

    private synchronized void updateTime() {
      this.time = FormatTime.nowMills();
    }

    private synchronized long elapsed() {
      return FormatTime.nowMills() - this.time;
    }

    @Override
    public synchronized void reset() {
      count = 0;
      updateTime();
    }

    @Override
    public boolean isInitValue() {
      return count == 0;
    }

    @Override
    public synchronized long incrementAndGet() {
      return ++count;
    }

    @Override
    public synchronized long incrementAndGet(long c) {
      count += c;
      return count;
    }

    @Override
    public synchronized long decrementAndGet() {
      --count;
      if (count < 0) {
        count = 0;
      }
      return count;
    }

    @Override
    public synchronized long decrementAndGet(long c) {
      count -= c;
      if (count < 0) {
        count = 0;
      }
      return count;
    }

    @Override
    public synchronized boolean compareAndInit(long actual) {
      if (compare(actual)) {
        count = 0;
        return true;
      } else {
        return false;
      }
    }

    @Override
    public synchronized boolean compareAndInit(long actual, long elapsed) {
      boolean r = (elapsed() >= elapsed);
      if (r) {
        updateTime();
      }
      return r || compareAndInit(actual);
    }

    @Override
    public synchronized boolean compare(long actual) {
      return count > 0 && (count % actual == 0);
    }

    @Override
    public synchronized boolean compare(long actual, long elapsed) {
      return compare(actual) || (elapsed() >= elapsed);
    }

    @Override
    public long longValue() {
      return count;
    }

    @Override
    public int intValue() {
      return (int) longValue();
    }

    @Override
    public boolean pageScope(int startRow, int endRow) {
      int c = intValue();
      return c >= startRow && c < endRow;
    }
  }

  private static class UnSafeCount extends Count {

    private long count = 0;

    private long time = FormatTime.nowMills();

    private void updateTime() {
      this.time = FormatTime.nowMills();
    }

    private long elapsed() {
      return FormatTime.nowMills() - this.time;
    }

    @Override
    public void reset() {
      count = 0;
      updateTime();
    }

    @Override
    public boolean isInitValue() {
      return count == 0;
    }

    @Override
    public long incrementAndGet() {
      return ++count;
    }

    @Override
    public long incrementAndGet(long c) {
      count += c;
      return count;
    }

    @Override
    public long decrementAndGet() {
      --count;
      if (count < 0) {
        count = 0;
      }
      return count;
    }

    @Override
    public long decrementAndGet(long c) {
      count -= c;
      if (count < 0) {
        count = 0;
      }
      return count;
    }

    @Override
    public boolean compareAndInit(long actual) {
      if (compare(actual)) {
        count = 0;
        return true;
      } else {
        return false;
      }
    }

    @Override
    public boolean compareAndInit(long actual, long elapsed) {
      boolean r = (elapsed() >= elapsed);
      if (r) {
        updateTime();
      }
      return r || compareAndInit(actual);
    }

    @Override
    public boolean compare(long actual) {
      return count > 0 && (count % actual == 0);
    }

    @Override
    public boolean compare(long actual, long elapsed) {
      return compare(actual) || (elapsed() >= elapsed);
    }

    @Override
    public long longValue() {
      return count;
    }

    @Override
    public int intValue() {
      return (int) longValue();
    }

    @Override
    public boolean pageScope(int startRow, int endRow) {
      int c = intValue();
      return c >= startRow && c < endRow;
    }
  }
}
