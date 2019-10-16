package cn.com.easyerp.core.timedTask.entity;

import java.util.Date;

public class TimeTaskSysTimeDescribe {
    private String task_id;
    private String sys_time_id;
    private Date begin_date;
    private Date end_date;
    private int lead;
    private String lead_type;
    private int is_loop;
    private int space;
    private String loop_type;
    private int is_using;

    public int getIs_using() {
        return this.is_using;
    }

    public void setIs_using(int is_using) {
        this.is_using = is_using;
    }

    public String getTask_id() {
        return this.task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getSys_time_id() {
        return this.sys_time_id;
    }

    public void setSys_time_id(String sys_time_id) {
        this.sys_time_id = sys_time_id;
    }

    public Date getBegin_date() {
        return this.begin_date;
    }

    public void setBegin_date(Date begin_date) {
        this.begin_date = begin_date;
    }

    public Date getEnd_date() {
        return this.end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public int getLead() {
        return this.lead;
    }

    public void setLead(int lead) {
        this.lead = lead;
    }

    public String getLead_type() {
        return this.lead_type;
    }

    public void setLead_type(String lead_type) {
        this.lead_type = lead_type;
    }

    public int getIs_loop() {
        return this.is_loop;
    }

    public void setIs_loop(int is_loop) {
        this.is_loop = is_loop;
    }

    public int getSpace() {
        return this.space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public String getLoop_type() {
        return this.loop_type;
    }

    public void setLoop_type(String loop_type) {
        this.loop_type = loop_type;
    }
}
