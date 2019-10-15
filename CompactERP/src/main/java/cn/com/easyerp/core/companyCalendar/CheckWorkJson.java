package cn.com.easyerp.core.companyCalendar;

import java.util.List;

import cn.com.easyerp.module.salaryCal.EmpInfo;

public class CheckWorkJson {
    private String user;
    private List<AttendanceJson> list;
    private EmpInfo empInfo;

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<AttendanceJson> getList() {
        return this.list;
    }

    public void setList(List<AttendanceJson> list) {
        this.list = list;
    }

    public EmpInfo getEmpInfo() {
        return this.empInfo;
    }

    public void setEmpInfo(EmpInfo empInfo) {
        this.empInfo = empInfo;
    }
}
