package com.ruoyi.common.core.web.handle;

import java.io.Serializable;

public class QueryRule implements Serializable {

    private String field;

    private String op;

    private Object value;

    public QueryRule(String field, String op, Object value) {
        this.field = field;
        this.op = op;
        this.value = value;
    }

    public QueryRule() {
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
