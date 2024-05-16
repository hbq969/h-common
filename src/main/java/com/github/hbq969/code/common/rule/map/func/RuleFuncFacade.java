package com.github.hbq969.code.common.rule.map.func;


import com.github.hbq969.code.common.decorde.DefaultOptionalFacade;
import com.github.hbq969.code.common.rule.map.ChainRuleSingle;
import com.github.hbq969.code.common.rule.map.func.parsetime.*;
import com.github.hbq969.code.common.rule.map.func.regex.EQRegexRuleFunc;
import com.github.hbq969.code.common.rule.map.func.regex.LikeRegexRuleFunc;

import java.util.Optional;

/**
 * @author : hbq969@gmail.com
 * @description : 规则函数解析门面实现
 * @createTime : 2023/9/14 11:20
 */
public class RuleFuncFacade extends DefaultOptionalFacade<String, ChainRuleSingle.CMP> implements
    ChainRuleSingle.CMP {

  public final static RuleFuncFacade RULE_FUNC_FACADE = new RuleFuncFacade();

  static {
    try {
      new EQTimeParseRuleFunc().afterPropertiesSet();
      new GETimeParseRuleFunc().afterPropertiesSet();
      new GTTimeParseRuleFunc().afterPropertiesSet();
      new LETimeParseRuleFunc().afterPropertiesSet();
      new LTTimeParseRuleFunc().afterPropertiesSet();
      new EQRegexRuleFunc().afterPropertiesSet();
      new LikeRegexRuleFunc().afterPropertiesSet();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Optional<ChainRuleSingle.CMP> query() {
    throw new UnsupportedOperationException("不支持默认方法");
  }

  @Override
  public Optional<ChainRuleSingle.CMP> query(String cmpKey) {
    // >=parse-time['gatherTime','HHmmss']
    int sIdx = cmpKey.indexOf('[');
    String key = cmpKey.substring(0, sIdx);
    return super.query(key);
  }

  @Override
  public boolean cmp(String cmpKey, String actual, String expected) {
    return getService().cmp(cmpKey, actual, expected);
  }
}
