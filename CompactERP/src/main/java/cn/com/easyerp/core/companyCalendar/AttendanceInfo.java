package cn.com.easyerp.core.companyCalendar;

import java.util.Date;

public class AttendanceInfo {
    private Date workDate;
    private Date start_work_time;
    private Date end_work_time;
    private Integer is_late;
    private Integer is_leave;

    public Date getWorkDate() {
        return this.workDate;
    }

    public void setWorkDate(Date workDate) {
        this.workDate = workDate;
    }

    public Date getStart_work_time() {
        return this.start_work_time;
    }

    public void setStart_work_time(Date start_work_time) {
        this.start_work_time = start_work_time;
    }

    public Date getEnd_work_time() {
        return this.end_work_time;
    }

    public void setEnd_work_time(Date end_work_time) {
        this.end_work_time = end_work_time;
    }

    public Integer getIs_late() {
        return this.is_late;
    }

    public void setIs_late(Integer is_late) {
        this.is_late = is_late;
    }

    public Integer getIs_leave() {
        return this.is_leave;
    }

    public void setIs_leave(Integer is_leave) {
        this.is_leave = is_leave;
    }
}
