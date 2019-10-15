package cn.com.easyerp.core.cache;

import java.util.Map;

import cn.com.easyerp.framework.common.Common;

public class ActionEventDescribe {
    private String event_id;
    private String table_action_id;
    private int seq;
    private Integer event_type;
    private String event_name;
    private String event_param;
    private Map<String, Object> requestParam;
    private Map<String, Object> paseParam;
    private Integer is_using;
    private String _selected;

    public String get_selected() {
        return this._selected;
    }

    public void set_selected(String _selected) {
        this._selected = _selected;
    }

    public String getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTable_action_id() {
        return this.table_action_id;
    }

    public void setTable_action_id(String table_action_id) {
        this.table_action_id = table_action_id;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public Integer getEvent_type() {
        return this.event_type;
    }

    public void setEvent_type(Integer event_type) {
        this.event_type = event_type;
    }

    public String getEvent_name() {
        return this.event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_param() {
        return this.event_param;
    }

    public void setEvent_param(String event_param) {
        event_param = event_param.replace("&lt;", "<");
        event_param = event_param.replace("&gt;", ">");
        event_param = event_param.replace("&quot;", "\"");
        event_param = event_param.replace("&#39;", "'");
        this.event_param = event_param;
        this.paseParam = Common.paseEventParam(event_param);
    }

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

    public Integer getIs_using() {
        return this.is_using;
    }

    public void setIs_using(Integer is_using) {
        this.is_using = is_using;
    }
}
