package cn.com.easyerp.core.cache;

import java.util.Map;

public class TableApproveEventDescribe {
    private String table_id;
    private int approve_event_type;
    private String event_type;
    private String event_id;
    private String event_param;
    private int is_using;
    private int seq;
    private String _selected;
    private Map<String, Object> requestParam;
    private Map<String, Object> paseParam;

    public Map<String, Object> getRequestParam() {
        return this.requestParam;
    }

    public void setRequestParam(Map<String, Object> requestParam) {
        this.requestParam = requestParam;
    }

    public Map<String, Object> getPaseParam() {
        return this.paseParam;
    }

    public void setPaseParam(Map<String, Object> paseParam) {
        this.paseParam = paseParam;
    }

    public String get_selected() {
        return this._selected;
    }

    public void set_selected(String _selected) {
        this._selected = _selected;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public int getApprove_event_type() {
        return this.approve_event_type;
    }

    public void setApprove_event_type(int approve_event_type) {
        this.approve_event_type = approve_event_type;
    }

    public String getEvent_type() {
        return this.event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_param() {
        return this.event_param;
    }

    public void setEvent_param(String event_param) {
        this.event_param = event_param;
    }

    public int getIs_using() {
        return this.is_using;
    }

    public void setIs_using(int is_using) {
        this.is_using = is_using;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
