package com.github.hbq969.code.common.rule.map.func.regex;


import com.github.hbq969.code.common.decorde.OptionalFacade;
import com.github.hbq969.code.common.decorde.OptionalFacadeAware;
import com.github.hbq969.code.common.rule.map.ChainRuleSingle;
import com.github.hbq969.code.common.rule.map.func.RuleFuncFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author : hbq969@gmail.com
 * @description : 正则表达式函数规则
 * @createTime : 2023/9/14 13:50
 */
public class LikeRegexRuleFunc implements ChainRuleSingle.CMP, OptionalFacadeAware<String, ChainRuleSingle.CMP> {

  private Map<String, Pattern> ps = new HashMap<>();

  @Override
  public OptionalFacade<String, ChainRuleSingle.CMP> getOptionalFacade() {
    return RuleFuncFacade.RULE_FUNC_FACADE;
  }

  @Override
  public String getKey() {
    return "%=regex";
  }

  @Override
  public boolean cmp(String cmpKey, String actual, String expected) {
    // regex['fieldKey']%=$foo.*
    Pattern p;
    synchronized (ps) {
      p = ps.get(expected);
      if (p == null) {
        p = Pattern.compile(expected);
        ps.put(expected, p);
      }
    }
    return p.matcher(actual).find();
  }
}
