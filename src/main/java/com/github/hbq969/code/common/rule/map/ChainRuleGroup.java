
package com.github.hbq969.code.common.rule.map;


import com.github.hbq969.code.common.rule.NodeInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 判断组
 *
 * @author hbq969@gmail.com
 */
public class ChainRuleGroup implements Rule<Map> {

    private ORChainRule toprule = new ORChainRule();

    private String rule;

    public ChainRuleGroup(String rule) {
        this(rule, false);
    }

    public ChainRuleGroup(String grule, boolean nullOnNotEqual) {
        this.rule = grule;
        grule = grule.substring(1, grule.length() - 1);
        ChainRule r = new ChainRule(grule, nullOnNotEqual);
        toprule.addRule(r);
    }

    @Override
    public boolean accept(Map msg) {
        return toprule.accept(msg);
    }

    @Override
    public void accept(Map msg, Consumer<Map> suc, Consumer<Map> fail) {
        if (accept(msg)) {
            suc.andThen(suc);
        }
    }

    @Override
    public boolean anyOne(Map msg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toSql() {
        String ts = toprule.toSql();
        if (ts != null) {
            String sql = new StringBuilder(LB).append(ts).append(RB).toString();
            return sql;
        } else {
            return null;
        }
    }


    @Override
    public String toSqlIncludeKeys(Set<String> inkeys) {
        String ts = toprule.toSqlIncludeKeys(inkeys);
        if (ts != null) {
            return new StringBuilder(LB).append(ts).append(RB).toString();
        } else {
            return null;
        }
    }

    @Override
    public String toSqlExcludeKeys(Set<String> exkeys) {
        String ts = toprule.toSqlExcludeKeys(exkeys);
        if (ts != null) {
            return new StringBuilder(LB).append(ts).append(RB).toString();
        } else {
            return null;
        }
    }

    @Override
    public List<NodeInfo> toNodeInfos() {
        return toprule.toNodeInfos();
    }

    @Override
    public Set<String> conditionKeySet() {
        return toprule.conditionKeySet();
    }
}
