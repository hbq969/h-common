package com.github.hbq969.code.common.rule.symbol;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author hbq969@gmail.com
 */
@Slf4j
public class ChainRule implements Rule {

  final static private char LB = '(';

  final static private char RB = ')';

  public static final char TP = '^';

  private String rule;

  private ANDChainRule toprule = new ANDChainRule();

  public ChainRule(String rule) {
    this.rule = rule;
    if (rule == null) {
      throw new NullPointerException("规则异常，传入的规则不允许为空");
    }
    try {
      // 逻辑缓存
      Stack<Character> lg = new Stack<Character>();
      // 转义字符匹配
      StringBuilder sb = new StringBuilder();
      char[] ars = rule.toCharArray();
      for (char c : ars) {
        if (LB == c && (lg.isEmpty() || lg.peek() != LB)) {
          lg.push(c);
          sb.append(c);
          continue;
        } else if (!lg.isEmpty() && lg.peek() == LB && c != RB) {
          sb.append(c);
          continue;
        } else if (!lg.isEmpty() && lg.peek() == LB
            && RB == c && sb.charAt(sb.length() - 1) == TP) {
          sb.append(c);
          lg.pop();
          // 截取的规则内容
          String rc = sb.toString();
          log.info("解析规则中的组: {}", rc);
          if (rc.startsWith("(") && rc.endsWith(")")) {
            toprule.addRule(new ChainRuleGroup(rc));
            sb.setLength(0);
          }
          continue;
        } else if (RB == c && sb.length() > 1 && sb.charAt(sb.length() - 1) != TP) {
          sb.append(c);
        }
      }
    } catch (Exception e) {
      log.error(String.format("%s 解析异常", rule), e);
    }
  }

  @Override
  public boolean accept(Map alarm) {
    return toprule.accept(alarm);
  }

  @Override
  public boolean anyOne(Map msg) {
    if (msg == null || msg.isEmpty()) {
      return false;
    }
    Set<String> set = toprule.conditionKeySet();
    for (String key : set) {
      if (!msg.containsKey(key)) {
        msg.put(key, "\n");
      }
    }
    return accept(msg);
  }

  @Override
  public Set<String> conditionKeySet() {
    return toprule.conditionKeySet();
  }

  @Override
  public String toFilterRule() {
    return toprule.toFilterRule();
  }

  public String getRule() {
    return rule;
  }
}
