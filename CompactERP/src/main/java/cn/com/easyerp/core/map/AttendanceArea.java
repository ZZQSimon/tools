package cn.com.easyerp.core.map;

import java.util.Date;

public class AttendanceArea {
    private String attendanceId;
    private String attendanceArea;
    private String attendanceName;
    private String attendanceLon;
    private String attendanceLat;
    private Date beginDate;
    private Date endDate;

    public Date getBeginDate() {
        return this.beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAttendanceId() {
        return this.attendanceId;
    }

    public void setAttendanceId(String attendanceId) {
        this.attendanceId = attendanceId;
    }

    public String getAttendanceArea() {
        return this.attendanceArea;
    }

    public void setAttendanceArea(String attendanceArea) {
        this.attendanceArea = attendanceArea;
    }

    public String getAttendanceName() {
        return this.attendanceName;
    }

    public void setAttendanceName(String attendanceName) {
        this.attendanceName = attendanceName;
    }

    public String getAttendanceLon() {
        return this.attendanceLon;
    }

    public void setAttendanceLon(String attendanceLon) {
        this.attendanceLon = attendanceLon;
    }

    public String getAttendanceLat() {
        return this.attendanceLat;
    }

    public void setAttendanceLat(String attendanceLat) {
        this.attendanceLat = attendanceLat;
    }
}
