package com.ruoyi.common.core.web.handle;

import java.io.Serializable;

public class QueryGroup implements Serializable {
    public static final String OP_AND = "and";
    public static final String OP_OR = "or";
    private QueryRule[] rules;
    private String op ;

    private QueryGroup() {}

    public QueryGroup(QueryRule[] rules, String op) {
        this.rules = rules;
        this.op = op;
    }

    public QueryRule[] getRules() {
        return rules;
    }

    public void setRules(QueryRule[] rules) {
        this.rules = rules;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }
}
