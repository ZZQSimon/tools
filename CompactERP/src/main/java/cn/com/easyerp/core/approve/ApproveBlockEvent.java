package cn.com.easyerp.core.approve;

import java.util.HashMap;
import java.util.Map;

import cn.com.easyerp.core.cache.ActionEventDescribe;

public class ApproveBlockEvent {
    public static class EventType {
        public static String AGREE_EVENT = "agreeEvent";
        public static String REJECT_EVENT = "rejectEvent";
        public static String TERMINATION_EVENT = "terminationEvent";
        public static String SUBMIT_EVENT = "submitEvent";
    }

    private String approve_event_id;
    private String block_id;
    private String event_type;
    private int email;
    private int sms;
    private String table_id;
    private Map<String, ActionEventDescribe> event;

    public String getApprove_event_id() {
        return this.approve_event_id;
    }

    public void setApprove_event_id(String approve_event_id) {
        this.approve_event_id = approve_event_id;
    }

    public String getBlock_id() {
        return this.block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
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

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public Map<String, ActionEventDescribe> getEvent() {
        return this.event;
    }

    public void setEvent(Map<String, ActionEventDescribe> event) {
        this.event = event;
    }

    public void addEvent(ActionEventDescribe blockEvent) {
        if (null == this.event)
            this.event = new HashMap<>();
        this.event.put(blockEvent.getEvent_id(), blockEvent);
    }
}
