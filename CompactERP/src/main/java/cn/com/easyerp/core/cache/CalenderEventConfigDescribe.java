package cn.com.easyerp.core.cache;

public class CalenderEventConfigDescribe {
    private String id;
    private String table_id;
    private String begin_time;
    private String end_time;
    private String event_name;
    private String default_color;

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

    public String getBegin_time() {
        return this.begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return this.end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getEvent_name() {
        return this.event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getDefault_color() {
        return this.default_color;
    }

    public void setDefault_color(String default_color) {
        this.default_color = default_color;
    }
}
