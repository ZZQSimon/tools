package cn.com.easyerp.core.data;

import java.util.Map;

import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;

public class ReferenceRequestModel extends WidgetRequestModelBase {
    private String text;
    private ViewDataMap param;
    private Map<String, FilterDescribe> filters;
    private String tableName;
    private String columnName;

    public String getTableName() {
        return this.tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ViewDataMap getParam() {
        return this.param;
    }

    public void setParam(ViewDataMap param) {
        this.param = param;
    }

    public Map<String, FilterDescribe> getFilters() {
        return this.filters;
    }

    public void setFilters(Map<String, FilterDescribe> filters) {
        this.filters = filters;
    }
}
