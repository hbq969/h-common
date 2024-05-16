package com.github.hbq969.code.common.utils;


import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 时间信息包装器
 * @createTime : 10:18:33, 2023.03.31, 周五
 */
public class TimeInfo {

  private long mills;

  private long secs;

  private String formatTimeString;

  private boolean closure = false;

  public TimeInfo() {
  }

  public TimeInfo(long time, TimeUnit unit) {
    setMills(TimeUnit.MILLISECONDS.convert(time, unit));
  }

  public TimeInfo format() {
    this.formatTimeString = FormatTime.YYYYMMDDHHMISS.withMills(mills);
    return this;
  }

  public long getMills() {
    return mills;
  }

  public TimeInfo setMills(long mills) {
    this.mills = mills;
    this.secs = mills / 1000L;
    return this;
  }

  public long getSecs() {
    return secs;
  }

  public TimeInfo setSecs(long secs) {
    return setMills(secs * 1000L);
  }

  public String getFormatTimeString() {
    return formatTimeString;
  }

  public boolean isClosure() {
    return closure;
  }

  public TimeInfo closure() {
    this.closure = true;
    return this;
  }

}
