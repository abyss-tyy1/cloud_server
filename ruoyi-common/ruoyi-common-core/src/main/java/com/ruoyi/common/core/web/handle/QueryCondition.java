package com.ruoyi.common.core.web.handle;


import java.io.Serializable;
import java.util.Map;

public class QueryCondition<T> implements Serializable {



    private QueryRule[]  where;

    private QueryGroup[] groups;
    private int pageSize = 20;
    private int pageNum = 1;

    private String[] orderType;

    private String[] orderBy;

    private Map<String,Object> param;

    public QueryRule[] getWhere() {
        return where;
    }

    public void setWhere(QueryRule[] where) {
        this.where = where;
    }

    public QueryGroup[] getGroups() {
        return groups;
    }

    public void setGroups(QueryGroup[] groups) {
        this.groups = groups;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String[] getOrderType() {
        return orderType;
    }

    public void setOrderType(String[] orderType) {
        this.orderType = orderType;
    }

    public String[] getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String[] orderBy) {
        this.orderBy = orderBy;
    }

    public Map<String, Object> getParam() {
        return param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
