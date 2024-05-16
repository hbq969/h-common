package com.github.hbq969.code.common.rule.map.func.parsetime;

import com.github.hbq969.code.common.decorde.OptionalFacade;
import com.github.hbq969.code.common.decorde.OptionalFacadeAware;
import com.github.hbq969.code.common.rule.map.ChainRuleSingle;
import com.github.hbq969.code.common.rule.map.func.RuleFuncFacade;
import com.google.common.base.Splitter;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.List;

/**
 * @author : hbq969@gmail.com
 * @description : 日期解析函数
 * @createTime : 2023/9/14 11:22
 */
public class LTTimeParseRuleFunc implements ChainRuleSingle.CMP,
        OptionalFacadeAware<String, ChainRuleSingle.CMP> {

  @Override
  public OptionalFacade<String, ChainRuleSingle.CMP> getOptionalFacade() {
    return RuleFuncFacade.RULE_FUNC_FACADE;
  }

  @Override
  public String getKey() {
    return "<parse-time";
  }

  @Override
  public boolean cmp(String cmpKey, String actual, String expected) {
    // parse-time['gatherTime','HHmmss']
    int sIdx = cmpKey.indexOf('[');
    int eIdx = cmpKey.indexOf(']');
    String paras = cmpKey.substring(sIdx + 1, eIdx);
    List<String> pList = Splitter.on(",").trimResults().splitToList(paras);
    String format = pList.get(1).substring(1, pList.get(1).length() - 1);
    String time = DateFormatUtils.format(Long.valueOf(actual) * 1000L, format);
    long sec = Long.valueOf(time);
    return sec < Long.valueOf(expected);
  }
}
