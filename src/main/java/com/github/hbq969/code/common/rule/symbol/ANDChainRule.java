package com.github.hbq969.code.common.rule.symbol;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hbq969@gmail.com
 */
public class ANDChainRule implements Rule {

  //规则链
  private List<Rule> rules;

  public ANDChainRule() {
    rules = new ArrayList<Rule>();
  }

  public ANDChainRule(List<Rule> rules) {
    this.rules = rules;
  }

  /**
   * 增加一个规则
   *
   * @param rule
   */
  public void addRule(Rule rule) {
    rules.add(rule);
  }

  @Override
  public boolean accept(Map alarm) {
    if (rules == null || rules.isEmpty()) {
      return false;
    }
    for (Rule rule : rules) {
      if (!rule.accept(alarm)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean anyOne(Map msg) {
    return accept(msg);
  }

  @Override
  public Set<String> conditionKeySet() {
    Set<String> set = new HashSet<>(rules.size());
    for (Rule rule : rules) {
      set.addAll(rule.conditionKeySet());
    }
    return set;
  }

  @Override
  public String toFilterRule() {
    return rules.stream().map(r -> r.toFilterRule()).collect(Collectors.joining("&"));
  }
}
