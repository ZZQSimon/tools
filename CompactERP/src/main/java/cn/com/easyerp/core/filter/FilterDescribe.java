package cn.com.easyerp.core.filter;

import java.util.List;

public class FilterDescribe {
    public static enum Type {
        in, like, between, range, eq, tree, query;
    }

    private Type type;
    private Object value;
    private Object from;
    private Object to;
    private String sql;

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Object getFrom() {
        return this.from;
    }

    public void setFrom(Object from) {
        this.from = from;
    }

    public Object getTo() {
        return this.to;
    }

    public void setTo(Object to) {
        this.to = to;
    }

    @SuppressWarnings("rawtypes")
    public boolean contain(Object value) {
        return ((List) this.value).contains(value);
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }
}
