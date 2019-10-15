package cn.com.easyerp.core.filter;

import java.util.Map;

public class FilterRequestModel {
    private String tableName;
    private String action;
    private Map<String, FilterDescribe> filters;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAction() {
        return this.action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, FilterDescribe> getFilters() {
        return this.filters;
    }

    public void setFilters(Map<String, FilterDescribe> filters) {
        this.filters = filters;
    }
}
