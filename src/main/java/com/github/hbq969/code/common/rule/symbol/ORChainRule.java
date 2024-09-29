package com.github.hbq969.code.common.rule.symbol;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author hbq969@gmail.com
 */
public class ORChainRule implements Rule {

    private List<Rule> rules;

    public ORChainRule() {
        this.rules = new ArrayList<Rule>();
    }

    public ORChainRule(List<Rule> rules) {
        this.rules = rules;
    }

    /**
     * 增加规则
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
            if (rule.accept(alarm)) {
                return true;
            }
        }
        return false;
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
        Set<String> set = new HashSet<>(rules.size());
        for (Rule rule : rules) {
            set.addAll(rule.conditionKeySet());
        }
        return set;
    }

    @Override
    public String toFilterRule() {
        return rules.stream().map(r -> r.toFilterRule()).collect(Collectors.joining("|"));
    }
}
