package com.github.hbq969.code.common.utils;

/**
 * @author : hbq969@gmail.com
 * @description : 时间区间包装器
 * @createTime : 15:02:16, 2023.03.31, 周五
 */
public class RoundInfo {

  private TimeInfo startTime;
  private TimeInfo endTime;

  public TimeInfo getStartTime() {
    return startTime;
  }

  public RoundInfo setStartTime(TimeInfo startTime) {
    this.startTime = startTime;
    return this;
  }

  public TimeInfo getEndTime() {
    return endTime;
  }

  public RoundInfo setEndTime(TimeInfo endTime) {
    this.endTime = endTime;
    return this;
  }

  @Override
  public String toString() {
    return String.format("统计调度, 开始时间(%s, %s), 结束时间(%s, %s)", startTime.getFormatTimeString(), startTime.getMills(), endTime.getFormatTimeString(),
        endTime.getMills());
  }

  public String simpleInfo() {
    return String.format("[%s, %s%s", startTime.getFormatTimeString(), endTime.getFormatTimeString(), endTime.isClosure() ? "]" : ")");
  }
}
