package com.github.hbq969.code.common.rule.symbol;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author hbq969@gmail.com
 */
public class ChainRuleGroup implements Rule {

    public static final String OP_NEQ = "^!=^";

    public static final String OP_NLIKE = "^!like^";

    private ORChainRule orule = new ORChainRule();

    private ANDChainRule arule = new ANDChainRule();

    private boolean AND = false;

    public ChainRuleGroup(String grule) {
        grule = grule.substring(1, grule.length() - 1);
        String[] rules = grule.split(OR);
        if (grule.contains(OP_NEQ) || grule.contains(OP_NLIKE)) {
            this.AND = true;
            for (String rule : rules) {
                arule.addRule(new ChainRuleSingle(rule));
            }
        } else {
            for (String rule : rules) {
                orule.addRule(new ChainRuleSingle(rule));
            }
        }
    }

    @Override
    public boolean accept(Map alarm) {
        return AND ?
                arule.accept(alarm) :
                orule.accept(alarm);
    }

    @Override
    public void accept(Map msg, Consumer<Map> suc, Consumer<Map> fail) {
        if (accept(msg)) {
            suc.andThen(suc);
        }
    }

    @Override
    public boolean anyOne(Map msg) {
        return accept(msg);
    }

    @Override
    public Set<String> conditionKeySet() {
        return AND ?
                arule.conditionKeySet() :
                orule.conditionKeySet();
    }

    @Override
    public String toFilterRule() {
        String rule = AND ?
                arule.toFilterRule() :
                orule.toFilterRule();
        return String.join("", "(", rule, ")");
    }
}
