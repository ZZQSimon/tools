package cn.com.easyerp.core.map;

import java.util.List;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class MapRequestModel extends FormRequestModelBase<MapModel> {
    private String attendanceId;
    private String attendanceArea;
    private String attendanceName;
    private String attendanceLon;
    private String attendanceLat;
    private String beginTime;
    private String endTime;
    private List<String> areas;

    public List<String> getAreas() {
        return this.areas;
    }

    public void setAreas(List<String> areas) {
        this.areas = areas;
    }

    public String getBeginTime() {
        return this.beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
