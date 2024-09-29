
package com.github.hbq969.code.common.rule.map;

import com.github.hbq969.code.common.rule.NodeInfo;
import com.github.hbq969.code.common.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

/**
 * @author hbq969@gmail.com
 */
@Slf4j
public class ChainRule implements Rule<Map> {

    final static private char LB = '(';

    final static private char RB = ')';

    final static private char DQ = '"';

    final static private char AND = '&';

    final static private char OR = '|';

    final static private char DG = '\\';

    private ORChainRule toprule = new ORChainRule();

    private String rule;

    private boolean nullOnNotEqual = false;

    public ChainRule(String rule) {
        this(rule, false);
    }

    public ChainRule(String rule, boolean nullOnNotEqual) {
        this.nullOnNotEqual = nullOnNotEqual;
        this.rule = rule;
        if (rule == null || rule.startsWith("||") || rule.startsWith("&&")
                || rule.endsWith("||") || rule.endsWith("&&")) {
            throw new RuntimeException("规则异常，传入的规则不允许为空，或者以||、&&开头或者结束");
        }
        try {
            ANDChainRule unitrule = new ANDChainRule();
            // 逻辑缓存
            Stack<Character> lg = new Stack<Character>();
            // 转义字符匹配
            Stack<Character> tm = new Stack<Character>();
            StringBuilder sb = new StringBuilder();
            char[] ars = rule.toCharArray();
            for (char c : ars) {
                if (!lg.isEmpty() && lg.peek() == DQ) {
                    if (tm.isEmpty() && c == DG) {
                        //  转义字符
                        tm.push(DG);
                        continue;
                    } else if (!tm.isEmpty()) {
                        if (c == DG) {
                            sb.append(DG);
                        } else if (c == DQ) {
                            sb.append(DQ);
                        } else {
                            throw new RuntimeException("错误的规则格式，字符串内部的特殊字符\\或者\"没有正确转义");
                        }
                        tm.pop();
                        continue;
                    } else if (c == DQ) {
                        sb.append(c);
                        lg.pop();
                        continue;
                    }
                    sb.append(c);
                } else if (LB == c || DQ == c) {
                    lg.push(c);
                    sb.append(c);
                    continue;
                } else if (!lg.isEmpty() && lg.peek() == LB && c != RB) {
                    sb.append(c);
                    continue;
                } else if (!lg.isEmpty() && lg.peek() == LB && RB == c) {
                    sb.append(c);
                    lg.pop();
                    continue;
                } else if (AND == c || OR == c) {
                    // 做逻辑的地方
                    // 截取的规则内容
                    String rc = sb.toString();
                    if (rc.startsWith("(") && rc.endsWith(")")) {
                        unitrule.addRule(new ChainRuleGroup(rc, nullOnNotEqual));
                    } else {
                        ChainRuleSingle chainRuleSingle = new ChainRuleSingle(rc, nullOnNotEqual);
                        unitrule.addRule(chainRuleSingle);
                    }
                    if (OR == c) {
                        toprule.addRule(unitrule);
                        unitrule = new ANDChainRule();
                    }
                    sb = new StringBuilder();
                    continue;
                } else {
                    sb.append(c);
                }
            }
            // 截取的规则内容
            String rc = sb.toString();
            if (rc.startsWith("(") && rc.endsWith(")")) {
                unitrule.addRule(new ChainRuleGroup(rc, nullOnNotEqual));
            } else {
                ChainRuleSingle chainRuleSingle = new ChainRuleSingle(rc, nullOnNotEqual);
                unitrule.addRule(chainRuleSingle);
            }
            toprule.addRule(unitrule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean accept(Map msg) {
        return toprule.accept(msg);
    }

    @Override
    public void accept(Map msg, Consumer<Map> suc, Consumer<Map> fail) {
        if (accept(msg)) {
            suc.accept(msg);
        } else {
            fail.accept(msg);
        }
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
        if (log.isDebugEnabled()) {
            log.debug("补齐消息字段后: {}", GsonUtils.toJson(msg));
        }
        return accept(msg);
    }

    @Override
    public String toSql() {
        String sql = toprule.toSql();
        return sql == null ? null : sql;
    }

    @Override
    public String toSqlIncludeKeys(Set<String> inkeys) {
        String sql = toprule.toSqlIncludeKeys(inkeys);
        return sql == null ? null : sql;
    }

    @Override
    public String toSqlExcludeKeys(Set<String> exkeys) {
        String sql = toprule.toSqlExcludeKeys(exkeys);
        return sql == null ? null : sql;
    }

    @Override
    public List<NodeInfo> toNodeInfos() {
        return toprule.toNodeInfos();
    }

    @Override
    public Set<String> conditionKeySet() {
        return toprule.conditionKeySet();
    }

    public String getRule() {
        return rule;
    }

    public boolean isNullOnNotEqual() {
        return nullOnNotEqual;
    }
}
