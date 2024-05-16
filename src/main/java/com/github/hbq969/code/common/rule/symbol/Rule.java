package com.github.hbq969.code.common.rule.symbol;

import java.util.Map;
import java.util.Set;

/**
 * @author hbq969@gmail.com
 */
public interface Rule {

  String EQ = "=";

  String NEQ = "!=";

  String LIKE = "like";

  String NOT_LIKE = "!like";

  String GT = ">";

  String LT = "<";

  String GET = ">=";

  String LET = "<=";

  String OR = " or ";

  /**
   * 过滤告警
   *
   * @param alarm
   * @return
   */
  boolean accept(Map alarm);

  /**
   * 任意一个key匹配即匹配
   *
   * @param msg
   * @return
   */
  boolean anyOne(Map msg);

  /**
   * 原始规则内容
   *
   * @return
   */
  Set<String> conditionKeySet();

  /**
   * 转换为过滤器规则形式
   *
   * @return
   */
  String toFilterRule();
}
