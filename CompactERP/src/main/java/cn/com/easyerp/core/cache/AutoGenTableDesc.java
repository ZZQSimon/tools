package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AutoGenTableDesc {
    public static enum AutoGenMode {
        single, batch;
    }

    public String id;
    private String table_id;
    private String ref_view;
    private String gen_sql;
    private String exec_condition;
    private String international_id;
    private List<String> editable = null;
    private AutoGenMode mode = AutoGenMode.batch;

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getRef_view() {
        return this.ref_view;
    }

    public void setRef_view(String ref_view) {
        this.ref_view = ref_view;
    }

    public String getGen_sql() {
        return this.gen_sql;
    }

    public void setGen_sql(String gen_sql) {
        this.gen_sql = gen_sql;
    }

    public String getExec_condition() {
        return this.exec_condition;
    }

    public void setExec_condition(String exec_condition) {
        this.exec_condition = exec_condition;
    }

    public void addEditable(String name) {
        if (this.editable == null)
            this.editable = new ArrayList<>();
        this.editable.add(name);
    }

    public List<String> getEditable() {
        return this.editable;
    }

    public AutoGenMode getMode() {
        return this.mode;
    }

    public void setMode(AutoGenMode mode) {
        this.mode = mode;
    }
}
