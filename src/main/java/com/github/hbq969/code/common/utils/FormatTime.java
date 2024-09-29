package com.github.hbq969.code.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

/**
 * @author : hbq969@gmail.com
 * @description : 时间格式化工具类
 * @createTime : 15:00:01, 2023.03.31, 周五
 */
@Slf4j
public enum FormatTime {

  /**
   * 格式化:<code>yyyy-MM-dd HH:mm:ss</code>
   */
  YYYYMMDDHHMISS("yyyy-MM-dd HH:mm:ss"),
  /**
   * 格式化:<code>yyyyMMddHHmmss</code>
   */
  YYYYMMDDHHMISS_NO_JOINER("yyyyMMddHHmmss"),

  /**
   * 格式化:<code>yyyy-MM-dd HH:mm</code>
   */
  YYYYMMDDHHMI("yyyy-MM-dd HH:mm"),
  /**
   * 格式化:<code>yyyyMMddHHmm</code>
   */
  YYYYMMDDHHMI_NO_JOINER("yyyyMMddHHmm"),

  /**
   * 格式化: <code>yyyy-MM-dd HH</code>
   */
  YYYYMMDDHH("yyyy-MM-dd HH"),

  /**
   * 格式化: <code>yyyyMMddHH</code>
   */
  YYYYMMDDHH_NO_JOINER("yyyyMMddHH"),

  /**
   * 格式化:<code>yyyy-MM-dd</code>
   */
  YYYYMMDD("yyyy-MM-dd"),

  /**
   * 格式化:<code>yyyyMMdd</code>
   */
  YYYYMMDD_NO_JOINER("yyyyMMdd"),

  /**
   * 格式化:<code>yyyy-MM</code>
   */
  YYYYMM("yyyy-MM"),
  /**
   * 格式化:<code>yyyyMM</code>
   */
  YYYYMM_NO_JOINER("yyyyMM"),

  /**
   * 格式化:<code>yyyy</code>
   */
  YYYY("yyyy"),

  /**
   * 格式化:<code>MM-dd HH:mm:ss</code>
   */
  MMDDHHMISS("MM-dd HH:mm:ss"),
  /**
   * 格式化:<code>MMddHHmmss</code>
   */
  MMDDHHMISS_NO_JOINER("MMddHHmmss"),

  /**
   * 格式化:<code>MM-dd HH:mm</code>
   */
  MMDDHHMI("MM-dd HH:mm"),
  /**
   * 格式化:<code>MMddHHmm</code>
   */
  MMDDHHMI_NO_JOINER("MMddHHmm"),

  /**
   * 格式化:<code>MM-dd HH</code>
   */
  MMDDHH("MM-dd HH"),
  /**
   * 格式化:<code>MMddHH</code>
   */
  MMDDHH_NO_JOINER("MMddHH"),

  /**
   * 格式化:<code>MM-dd</code>
   */
  MMDD("MM-dd"),
  /**
   * 格式化:<code>MMdd</code>
   */
  MMDD_NO_JOINER("MMdd"),

  /**
   * 格式化:<code>MM</code>
   */
  MM("MM"),

  /**
   * 格式化:<code>dd HH:mm:ss</code>
   */
  DDHHMISS("dd HH:mm:ss"),
  /**
   * 格式化:<code>ddHHmmss</code>
   */
  DDHHMISS_NO_JOINER("ddHHmmss"),

  /**
   * 格式化:<code>dd HH:mm</code>
   */
  DDHHMI("dd HH:mm"),
  /**
   * 格式化:<code>ddHHmm</code>
   */
  DDHHMI_NO_JOINER("ddHHmm"),

  /**
   * 格式化:<code>dd HH</code>
   */
  DDHH("dd HH"),
  /**
   * 格式化:<code>ddHH</code>
   */
  DDHH_NO_JOINER("ddHH"),

  /**
   * 格式化:<code>dd</code>
   */
  DD("dd"),

  /**
   * 格式化:<code>HH:mm:ss</code>
   */
  HHMISS("HH:mm:ss"),
  /**
   * 格式化:<code>HHmmss</code>
   */
  HHMISS_NO_JOINER("HHmmss"),

  /**
   * 格式化:<code>HH:mm</code>
   */
  HHMI("HH:mm"),
  /**
   * 格式化:<code>HHmm</code>
   */
  HHMI_NO_JOINER("HHmm"),

  /**
   * 格式化:<code>HH</code>
   */
  HH("HH"),

  /**
   * 格式化:<code>mm:ss</code>
   */
  MISS("mm:ss"),
  /**
   * 格式化:<code>mmss</code>
   */
  MISS_NO_JOINER("mmss"),

  /**
   * 格式化:<code>mm</code>
   */
  MI("mm"),

  /**
   * 格式化:<code>yyyy_MM_dd</code>
   */
  YYYYMMDD_UNDERSCORE("yyyy_MM_dd"),

  /**
   * 格式化:<code>yyyy_MM</code>
   */
  YYYYMM_UNDERSCORE("yyyy_MM"),

  /**
   * 格式化:<code>MM_DD</code>
   */
  MMDD_UNDERSCORE("MM_dd"),

  /**
   * 格式化:<code>yyyy-MM ww</code>
   */
  YYYY_DASH_MM_SPACE_WW("yyyy-MM ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyy-MM ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyy_MM ww</code>
   */
  YYYY_MM_SPACE_WW("yyyy_MM ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyy_MM ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyy-MM-ww</code>
   */
  YYYY_DASH_MM_DASH_WW("yyyy-MM-ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyy-MM-ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyy_MM_ww</code>
   */
  YYYY_MM_WW("yyyy_MM_ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyy_MM_ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyy-ww</code>
   */
  YYYY_DASH_WW("yyyy-ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyy-ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyy_ww</code>
   */
  YYYY_WW("yyyy_ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyy_ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyyMMww</code>
   */
  YYYYMMWW("yyyyMMww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyyMMww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyyww</code>
   */
  YYYYWW("yyyyww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyyww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyyMM-ww</code>
   */
  YYYYMM_DASH_WW("yyyyMM-ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyyMM-ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyyMM_ww</code>
   */
  YYYYMM_WW("yyyyMM_ww") {
    @Override
    public String withTime(long time, TimeUnit unit) {
      DateTime dt = new DateTime(TimeUnit.MILLISECONDS.convert(time, unit)).dayOfWeek().setCopy(7);
      return FormatTime.format("yyyyMM_ww", dt.getMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public long toMills(String strTime) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long toSecs(String strTime) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * 格式化:<code>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</code>
   */
  UTC_FULL("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

  private String pattern;

  private FormatTime(String p) {
    this.pattern = p;
  }

  public String toStringPattern() {
    return this.pattern;
  }

  /**
   * 将指定时间值转换成时间字符串
   *
   * @param time 时间值
   * @param unit 时间单位 {@link TimeUnit}
   * @return
   */
  public String withTime(long time, TimeUnit unit) {
    return withMills(TimeUnit.MILLISECONDS.convert(time, unit));
  }

  /**
   * 使用毫秒进行格式化
   *
   * @param mills
   * @return
   */
  public String withMills(long mills) {
    return DateFormatUtils.format(mills, this.pattern);
  }

  /**
   * 使用当前毫秒进行格式化
   *
   * @return
   */
  public String withMills() {
    return withMills(System.currentTimeMillis());
  }

  /**
   * 使用秒进行格式化
   *
   * @param secs
   * @return
   */
  public String withSecs(long secs) {
    return withMills(secs * 1000L);
  }

  /**
   * 将时间字符串转换为毫秒，如果转换错误返回<code>Long.MIN_VALUE</code>
   *
   * @param strTime
   */
  public long toMills(String strTime) {
    try {
      return DateUtils.parseDate(strTime, new String[]{pattern}).getTime();
    } catch (ParseException e) {
      log.error(String.format("解析日期字符串[%s], 模式[%s]异常", strTime, pattern), e);
      return Long.MIN_VALUE;
    }
  }

  /**
   * 将时间字符串转换为秒，如果转换错误返回<code>Long.MIN_VALUE</code>
   *
   * @param strTime
   */
  public long toSecs(String strTime) {
    long t = toMills(strTime);
    return t == Long.MIN_VALUE ? Long.MIN_VALUE : t / 1000L;
  }

  public static String format(String p, long time, TimeUnit unit) {
    long mills = TimeUnit.MILLISECONDS.convert(time, unit);
    return DateFormatUtils.format(mills, p);
  }

  public static long nowSecs() {
    return nowMills() / 1000L;
  }

  public static long nowMills() {
    return System.currentTimeMillis();
  }

  public long parseToMills(String str) {
    try {
      return DateUtils.parseDate(str, new String[]{this.pattern}).getTime();
    } catch (ParseException e) {
      return -1;
    }
  }

  public long parseToSec(String str) {
    return parseToMills(str) / 1000L;
  }

  public static void main(String[] args) throws Exception {
    DateTime dt = DateTime.parse("2023-01-08");
    long t = dt.dayOfWeek().setCopy(7).getMillis();
    System.out.println(FormatTime.YYYYMMDDHHMISS.withMills(t));
    System.out.println(DateFormatUtils.format(t,"yyyyMM_ww"));
    long d=FormatTime.YYYYMMDDHHMISS.toMills("2023-01-8 0:1:3");
    System.out.println(FormatTime.YYYYMMDDHHMISS.withMills(d));
  }

}
