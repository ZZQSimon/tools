package cn.com.easyerp.core.view.form.index;

import cn.com.easyerp.core.widget.WidgetModelBase;

public class MatterModel extends WidgetModelBase {
    private String id;
    private String table_id;
    private String status_col;
    private String status_now;
    private String status_name;
    private String key_value;

    public MatterModel() {
        super("");
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getStatus_col() {
        return this.status_col;
    }

    public void setStatus_col(String status_col) {
        this.status_col = status_col;
    }

    public String getStatus_now() {
        return this.status_now;
    }

    public void setStatus_now(String status_now) {
        this.status_now = status_now;
    }

    public String getStatus_name() {
        return this.status_name;
    }

    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }

    public String getKey_value() {
        return this.key_value;
    }

    public void setKey_value(String key_value) {
        this.key_value = key_value;
    }
}
