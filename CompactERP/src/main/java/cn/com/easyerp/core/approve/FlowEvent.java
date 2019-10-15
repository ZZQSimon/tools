package cn.com.easyerp.core.approve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.cache.ActionEventDescribe;
import cn.com.easyerp.core.cache.ActionPrerequistieDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;

public class FlowEvent extends Object implements Comparable<FlowEvent> {
    public static class EventType {
        public static String AGREE_EVENT = "agreeEvent";
        public static String DISAGREE_EVENT = "disagreeEvent";
        public static String REJECT_EVENT = "rejectEvent";
        public static String RETAIN_EVENT = "retainEvent";
        public static String FINISH_EVENT = "finishEvent";
        public static String WORK_EVENT = "workEvent";
        public static String TERMINATION_EVENT = "terminationEvent";
    }

    private String table_id;
    private String block_id;
    private int seq;
    private String event_type;
    private int email;
    private int sms;
    private String international_id;
    private I18nDescribe i18n;
    private String flow_event_id;
    private List<ActionPrerequistieDescribe> condition;
    private Map<String, ActionEventDescribe> event;
    private int is_exec;
    private int is_index_button;
    private int is_using = 1;

    private Object data_id;

    private String approve_event_id;

    private String color;

    public int getIs_using() {
        return this.is_using;
    }

    public void setIs_using(int is_using) {
        this.is_using = is_using;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getBlock_id() {
        return this.block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getEvent_type() {
        return this.event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public int getEmail() {
        return this.email;
    }

    public void setEmail(int email) {
        this.email = email;
    }

    public int getSms() {
        return this.sms;
    }

    public void setSms(int sms) {
        this.sms = sms;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public String getFlow_event_id() {
        return this.flow_event_id;
    }

    public void setFlow_event_id(String flow_event_id) {
        this.flow_event_id = flow_event_id;
    }

    public List<ActionPrerequistieDescribe> getCondition() {
        return this.condition;
    }

    public Map<String, ActionEventDescribe> getEvent() {
        return this.event;
    }

    public void setCondition(List<ActionPrerequistieDescribe> condition) {
        this.condition = condition;
    }

    public void addCondition(ActionPrerequistieDescribe condition) {
        if (this.condition == null) {
            this.condition = new ArrayList<>();
        }
        this.condition.add(condition);
    }

    public void addEvent(ActionEventDescribe event) {
        if (this.event == null) {
            this.event = new HashMap<>();
        }
        this.event.put(event.getEvent_id(), event);
    }

    public int getIs_index_button() {
        return this.is_index_button;
    }

    public void setIs_index_button(int is_index_button) {
        this.is_index_button = is_index_button;
    }

    public int getIs_exec() {
        return this.is_exec;
    }

    public void setIs_exec(int is_exec) {
        this.is_exec = is_exec;
    }

    public Object getData_id() {
        return this.data_id;
    }

    public void setData_id(Object data_id) {
        this.data_id = data_id;
    }

    public String getApprove_event_id() {
        return this.approve_event_id;
    }

    public void setApprove_event_id(String approve_event_id) {
        this.approve_event_id = approve_event_id;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int compareTo(FlowEvent flowEvent) {
        return this.seq - flowEvent.seq;
    }
}
