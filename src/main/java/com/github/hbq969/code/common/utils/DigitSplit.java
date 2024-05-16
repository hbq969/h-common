package com.github.hbq969.code.common.utils;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 分割器
 * @createTime : 14:58:23, 2023.03.31, 周五
 */
public final class DigitSplit {

  /**
   * 普通数字范围分割
   *
   * @param step
   * @return
   */
  public static ISplitter defaultStep(long step) {
    return new DefaultSplitter(step);
  }

  /**
   * 时间范围分割
   *
   * @param step
   * @return
   */
  public static ISplitter timeStep(long step) {
    return new TimeSplitter(step);
  }

  private static void markFirstLast(List<Segment> segments) {
    if (!segments.isEmpty()) {
      segments.get(0).first();
      segments.get(segments.size() - 1).last();
    }
  }

  public interface ISplitter {

    /**
     * 普通等距分割
     *
     * @param start
     * @param end
     * @return
     */
    List<Segment> split(long start, long end);

    /**
     * 将集合进行分段
     *
     * @param list
     * @param <T>
     * @return
     */
    <T> List<SubList<T>> split(List<T> list);
  }

  static class DefaultSplitter implements ISplitter {

    long step;

    public DefaultSplitter(long step) {
      this.step = step;
    }

    @Override
    public List<Segment> split(long start, long end) {
      List<Segment> fragment = new ArrayList<>();
      int count = 0;
      while (start < end) {
        Segment segment = new Segment();
        segment.setBegin(start);
        segment.setEnd(start + step > end ? end : start + step);
        if (count == 0) {
          segment.first();
        }
        fragment.add(segment);
        start += step;
        count++;
      }
      if (!fragment.isEmpty()) {
        fragment.get(count - 1).last();
      }
      return fragment;
    }

    @Override
    public <T> List<SubList<T>> split(List<T> list) {
      if (CollectionUtils.isEmpty(list)) {
        return Collections.emptyList();
      }
      int max = list.size();
      List<Segment> gs = split(0, max);
      List<SubList<T>> result = new ArrayList<>(gs.size());
      for (Segment g : gs) {
        SubList subList = new SubList();
        Offset offset = new Offset();
        offset.setBeginIntValue(g.getBeginIntValue());
        offset.setEndIntValue(g.getEndIntValue());
        offset.setBeginLongValue(g.getBegin());
        offset.setEndLongValue(g.getEnd());
        subList.setOffset(offset);
        subList.setList(list.subList(g.getBeginIntValue(), g.getEndIntValue()));
        result.add(subList);
      }
      return result;
    }
  }

  static class TimeSplitter extends DefaultSplitter {

    public TimeSplitter(long step) {
      super(step);
    }

    @Override
    public List<Segment> split(long start, long end) {
      List<Segment> segments = new ArrayList<Segment>();
      while (start < end) {
        Segment seg = new Segment();
        seg.setBegin(start);
        if (start + step > end) {
          seg.setEnd(end);
        } else {
          long tEnd = start + step;
          long remain = (tEnd - (16 * 3600)) % step;
          if (remain > 0) {
            tEnd -= remain;
          }
          seg.setEnd(tEnd);
        }
        start = seg.getEnd();
        segments.add(seg);
      }
      markFirstLast(segments);
      return segments;
    }

  }
}
