package com.github.hbq969.code.common.rule.symbol;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hbq969@gmail.com
 */
@Slf4j
public class ChainRuleSingle implements Rule {

  public static final String LINE = "\n";

  private static final Map<String, String> entryOpera = new HashMap<>();

  public static final String ETP = "\\^";

  private String rule;

  final private Pattern strOpPtn = Pattern.compile("(\\^=\\^)|(\\^\\!=\\^)|(\\^like\\^)|(\\^\\!like\\^)|(\\^>\\^)|(\\^>=\\^)|(\\^<\\^)|(\\^<=\\^)");

  // 对比的key
  private String cmpKey = null;

  // 操作对象
  private CMP cmp = null;

  // 操作符
  private String cmpOper = null;

  // 对比的值
  private String cmpValue = null;

  static {
    entryOpera.put(EQ, EQ);
    entryOpera.put(NEQ, NEQ);
    entryOpera.put(LIKE, "%=");
    entryOpera.put(NOT_LIKE, "!%=");
    entryOpera.put(GT, GT);
    entryOpera.put(LT, LT);
    entryOpera.put(GET, GET);
    entryOpera.put(LET, LET);
  }

  public ChainRuleSingle(String rule) {
    if (rule == null) {
      throw new NullPointerException("规则异常，传入的规则不允许为空");
    }
    this.rule = rule;
    Matcher m = strOpPtn.matcher(rule);
    String op = null;
    if (m.find()) {
      op = m.group();
      op = StringUtils.mid(op, 1, op.length() - 2);
      this.cmpOper = op;
      if (EQ.equals(op)) {
        cmp = new StrEQ();
      } else if (NEQ.equals(op)) {
        cmp = new StrNEQ();
      } else if (LIKE.equals(op)) {
        cmp = new StrLike();
      } else if (NOT_LIKE.equals(op)) {
        cmp = new StrNLike();
      } else if (LT.equals(op)) {
        cmp = new NumberLT();
      } else if (LET.equals(op)) {
        cmp = new NumberLE();
      } else if (GT.equals(op)) {
        cmp = new NumberGT();
      } else if (GET.equals(op)) {
        cmp = new NumberGE();
      }
      String[] props = rule.split(ETP + op + ETP);
      if (props.length == 2) {
        cmpKey = StringUtils.mid(props[0], 1, props[0].length() - 1);
        cmpValue = StringUtils.mid(props[1], 0, props[1].length() - 1);
      }
    }
  }

  @Override
  public boolean accept(Map alarm) {
    if (cmp == null || cmpKey == null) {
      return false;
    }
    String actual = MapUtils.getString(alarm, cmpKey);
    return cmp.cmp(actual, cmpValue);
  }

  @Override
  public boolean anyOne(Map msg) {
    return accept(msg);
  }

  @Override
  public Set<String> conditionKeySet() {
    Set<String> set = new HashSet<>();
    set.add(cmpKey);
    return set;
  }

  @Override
  public String toFilterRule() {
    return cmp.toFilterRule(cmpKey, cmpValue, cmpOper);
  }

  interface CMP {

    /**
     * 比较器
     *
     * @param actual
     * @param expected
     * @return
     */
    boolean cmp(String actual, String expected);

    /**
     * 转换为过滤器规则字符串
     *
     * @param key
     * @param value
     * @param opera
     * @return
     */
    String toFilterRule(String key, String value, String opera);
  }


  /**
   * 字符串相等比较器
   */
  public static class StrEQ implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (actual == null && expected == null) {
        return true;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return StringUtils.equals(actual, expected);
    }

    @Override
    public String toFilterRule(String key, String value, String opera) {
      return String.join("", key, entryOpera.get(opera), "\"", value, "\"");
    }
  }

  /**
   * 字符串不相等比较器
   */
  public static class StrNEQ extends StrEQ {

    @Override
    public boolean cmp(String actual, String expected) {
      if (actual == null && expected == null) {
        return false;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return !StringUtils.equals(actual, expected);
    }
  }

  /**
   * 字符串包含比较器
   */
  public static class StrLike implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return StringUtils.contains(actual, expected);
    }

    @Override
    public String toFilterRule(String key, String value, String opera) {
      return String.join("", key, entryOpera.get(opera), "\"", value, "\"");
    }
  }

  /**
   * 字符串不包含比较器
   */
  public static class StrNLike extends StrLike {

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return !StringUtils.contains(actual, expected);
    }
  }

  /**
   * 数值大于比较器
   */
  public static class NumberGT implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      if (actual == null || expected == null) {
        return false;
      }
      return NumberUtils.toDouble(actual) > NumberUtils.toDouble(expected);
    }

    @Override
    public String toFilterRule(String key, String value, String opera) {
      return String.join("", key, entryOpera.get(opera), value);
    }
  }

  /**
   * 数值大于等于比较器
   */
  public static class NumberGE implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      if (actual == null || expected == null) {
        return false;
      }
      try {
        return Double.parseDouble(actual) >= Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }

    @Override
    public String toFilterRule(String key, String value, String opera) {
      return String.join("", key, entryOpera.get(opera), value);
    }
  }

  public static class NumberEQ implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (actual == null || actual == null) {
        return true;
      } else {
        if (StringUtils.equals(LINE, actual)) {
          return true;
        }
        try {
          return Double.parseDouble(actual) == Double.parseDouble(expected);
        } catch (NumberFormatException e) {
          return false;
        }
      }
    }

    @Override
    public String toFilterRule(String key, String value, String opera) {
      return String.join("", key, entryOpera.get(opera), value);
    }
  }

  /**
   * 数值小于比较器
   */
  public static class NumberLT implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      if (actual == null || expected == null) {
        return false;
      }
      return NumberUtils.toDouble(actual) < NumberUtils.toDouble(expected);
    }

    @Override
    public String toFilterRule(String key, String value, String opera) {
      return String.join("", key, entryOpera.get(opera), value);
    }
  }

  /**
   * 数值小于比较器
   */
  public static class NumberLE implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      if (actual == null || expected == null) {
        return false;
      }
      try {
        return Double.parseDouble(actual) <= Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }

    @Override
    public String toFilterRule(String key, String value, String opera) {
      return String.join("", key, entryOpera.get(opera), value);
    }
  }

}
