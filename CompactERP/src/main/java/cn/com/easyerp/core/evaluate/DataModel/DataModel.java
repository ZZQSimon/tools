package cn.com.easyerp.core.evaluate.DataModel;

import java.util.Map;

import cn.com.easyerp.core.widget.FieldModelBase;

public class DataModel {
    private String id;
    private String parent;
    private String status;
    private Map<String, FieldModelBase> data;

    public DataModel(String id, String parent) {
        this.id = id;
        this.parent = parent;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, FieldModelBase> getData() {
        return this.data;
    }

    public void setData(Map<String, FieldModelBase> data) {
        this.data = data;
    }
}
