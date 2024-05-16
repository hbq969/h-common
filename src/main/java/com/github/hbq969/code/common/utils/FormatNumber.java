package com.github.hbq969.code.common.utils;

import com.google.common.collect.Maps;

import java.text.NumberFormat;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author hbq969@gmail.com
 */
public final class FormatNumber {

  private static Map<Integer, NumberFormat> maximumFractionDigitsEntries = Maps.newHashMap();

  static {
    IntStream.range(0, 4).forEach(i ->
    {
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(i);
      nf.setGroupingUsed(false);
      maximumFractionDigitsEntries.put(i, nf);
    });
  }

  public static Double format(Double n, int maximumFractionDigits) {
    if (n == null) {
      return null;
    }
    NumberFormat nf = maximumFractionDigitsEntries.get(maximumFractionDigits);
    if (nf != null) {
      return Double.valueOf(nf.format(n.doubleValue()));
    } else {
      return n;
    }
  }
}
