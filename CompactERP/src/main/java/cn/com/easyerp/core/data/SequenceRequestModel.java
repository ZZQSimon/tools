package cn.com.easyerp.core.data;

import java.util.Map;

public class SequenceRequestModel {
    private String table;
    private Map<String, Object> keys;

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, Object> getKeys() {
        return this.keys;
    }

    public void setKeys(Map<String, Object> keys) {
        this.keys = keys;
    }
}
