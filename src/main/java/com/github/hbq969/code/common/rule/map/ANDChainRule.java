
package com.github.hbq969.code.common.rule.map;


import com.github.hbq969.code.common.rule.NodeInfo;

import java.util.*;

/**
 * 规则判断  与逻辑实现，有一个为false则为false不通过
 *
 * @author hbq969@gmail.com
 */
public class ANDChainRule implements Rule<Map> {

  //规则链
  private List<Rule> rules;

  public ANDChainRule() {
    rules = new ArrayList<Rule>();
  }

  public ANDChainRule(List<Rule> rules) {
    this.rules = rules;
  }

  @Override
  public boolean accept(Map msg) {
    for (Rule r : rules) {
      if (!r.accept(msg)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean anyOne(Map msg) {
    throw new UnsupportedOperationException();
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
  public String toSql() {
    StringBuilder sb = new StringBuilder();
    boolean isfirst = true;
    for (Rule rule : rules) {
      String ssql = rule.toSql();
      if (ssql != null) {
        if (isfirst) {
          sb.append(ssql);
          isfirst = false;
        } else {
          sb.append(AND).append(ssql);
        }
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      return sb.toString();
    }
  }

  @Override
  public String toSqlIncludeKeys(Set<String> inkeys) {
    StringBuilder sb = new StringBuilder();
    boolean isfirst = true;
    for (Rule rule : rules) {
      String ssql = rule.toSqlIncludeKeys(inkeys);
      if (ssql != null) {
        if (isfirst) {
          sb.append(ssql);
          isfirst = false;
        } else {
          sb.append(AND).append(ssql);
        }
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      return sb.toString();
    }
  }

  @Override
  public String toSqlExcludeKeys(Set<String> exkeys) {
    StringBuilder sb = new StringBuilder();
    boolean isfirst = true;
    for (Rule rule : rules) {
      String ssql = rule.toSqlExcludeKeys(exkeys);
      if (ssql != null) {
        if (isfirst) {
          sb.append(ssql);
          isfirst = false;
        } else {
          sb.append(AND).append(ssql);
        }
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      return sb.toString();
    }
  }

  @Override
  public List<NodeInfo> toNodeInfos() {
    List<NodeInfo> nodes = new ArrayList<>();
    for (Rule rule : rules) {
      nodes.addAll(rule.toNodeInfos());
    }
    return nodes;
  }

  @Override
  public Set<String> conditionKeySet() {
    Set<String> set = new HashSet<>(rules.size());
    for (Rule rule : rules) {
      set.addAll(rule.conditionKeySet());
    }
    return set;
  }
}
