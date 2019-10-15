package cn.com.easyerp.core.cache;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SqlMap {
    private String sqlId;
    private String sql;
    private List<String> param;

    @JsonIgnore
    public String getSqlId() {
        return this.sqlId;
    }

    public void setSqlId(String sqlId) {
        this.sqlId = sqlId;
    }

    @JsonIgnore
    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<String> getParam() {
        return this.param;
    }

    public void setParam(List<String> param) {
        this.param = param;
    }
}
