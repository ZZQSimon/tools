package cn.com.easyerp.core.data;

import java.util.Map;

public class SqlRequestModel {
    private String sql;
    private String formId;
    private Map<String, Object> param;
    private boolean map = false;
    private boolean list = false;

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFormId() {
        return this.formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public boolean isMap() {
        return this.map;
    }

    public void setMap(boolean map) {
        this.map = map;
    }

    public boolean isList() {
        return this.list;
    }

    public void setList(boolean list) {
        this.list = list;
    }
}
