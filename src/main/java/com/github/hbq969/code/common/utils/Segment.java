package com.github.hbq969.code.common.utils;

/**
 * @author : hbq969@gmail.com
 * @description : 分段包装器
 * @createTime : 15:03:46, 2023.03.31, 周五
 */
public class Segment {

  private boolean first = false;
  private boolean last = false;
  private long begin;
  private long end;

  public boolean isFirst() {
    return first;
  }

  public Segment first() {
    this.first = true;
    return this;
  }

  public boolean isLast() {
    return last;
  }

  public Segment last() {
    this.last = true;
    return this;
  }

  public long getBegin() {
    return begin;
  }

  public int getBeginIntValue() {
    return (int) getBegin();
  }

  public void setBegin(long begin) {
    this.begin = begin;
  }

  public long getEnd() {
    return end;
  }

  public int getEndIntValue() {
    return (int) getEnd();
  }

  public void setEnd(long end) {
    this.end = end;
  }

  @Override
  public String toString() {
    return "[" + begin + "," + end + (isLast() ? "]" : ")");
  }
}
