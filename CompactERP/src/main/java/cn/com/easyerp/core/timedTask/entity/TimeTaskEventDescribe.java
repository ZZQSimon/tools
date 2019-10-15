package cn.com.easyerp.core.timedTask.entity;

import java.util.Map;

import cn.com.easyerp.framework.common.Common;

public class TimeTaskEventDescribe {
    private String task_id;
    private String event_id;
    private String event_type;
    private String event_url_id;
    private String event_param;
    private Map<String, Object> param;
    private int is_using;
    private String _selected;

    public String get_selected() {
        return this._selected;
    }

    public void set_selected(String _selected) {
        this._selected = _selected;
    }

    public String getTask_id() {
        return this.task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_type() {
        return this.event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public String getEvent_url_id() {
        return this.event_url_id;
    }

    public void setEvent_url_id(String event_url_id) {
        this.event_url_id = event_url_id;
    }

    public String getEvent_param() {
        return this.event_param;
    }

    public void setEvent_param(String event_param) {
        this.event_param = event_param;
        this.param = Common.paseEventParam(event_param);
    }

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public int getIs_using() {
        return this.is_using;
    }

    public void setIs_using(int is_using) {
        this.is_using = is_using;
    }
}
