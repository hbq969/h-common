package com.github.hbq969.code.common.rule;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hbq969@gmail.com
 */
public class NodeInfo implements Cloneable {

  /**
   * 枚举KEY
   */
  private String nodeKey;

  /**
   * 枚举Value
   */
  private String nodeValue;

  /**
   * 枚举操作符
   */
  private String operator;

  /**
   * 枚举是否配置
   */
  private boolean config = false;

  public NodeInfo(String nodeKey, String nodeValue, String operator) {
    this.nodeKey = nodeKey;
    this.nodeValue = nodeValue;
    this.operator = operator;
  }

  public NodeInfo(String nodeKey, String nodeValue) {
    this.nodeKey = nodeKey;
    this.nodeValue = nodeValue;
  }

  public String getNodeKey() {
    return nodeKey;
  }

  public String getNodeValue() {
    return nodeValue;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public void config() {
    this.config = true;
  }

  public boolean isConfig() {
    return config;
  }

  public Map treeMap() {
    Map map = new HashMap();
    map.put("is_conf", config ? 1 : 0);
    map.put("node_k", nodeKey);
    map.put("node_v", nodeValue);
    map.put("node_oper", operator);
    return map;
  }

  @Override
  public NodeInfo clone() throws CloneNotSupportedException {
    NodeInfo clone = new NodeInfo(nodeKey, nodeValue);
    clone.setOperator(operator);
    clone.config = false;
    return clone;
  }
}
