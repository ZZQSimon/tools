package cn.com.easyerp.core.companyCalendar;

public class AttendanceJson {
    private String workDate;
    private String startClass;
    private String endClass;
    private String startTime;
    private String endTime;
    private String startState;
    private String endState;

    public String getStartClass() {
        return this.startClass;
    }

    public void setStartClass(String startClass) {
        this.startClass = startClass;
    }

    public String getEndClass() {
        return this.endClass;
    }

    public void setEndClass(String endClass) {
        this.endClass = endClass;
    }

    public String getWorkDate() {
        return this.workDate;
    }

    public void setWorkDate(String workDate) {
        this.workDate = workDate;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartState() {
        return this.startState;
    }

    public void setStartState(String startState) {
        this.startState = startState;
    }

    public String getEndState() {
        return this.endState;
    }

    public void setEndState(String endState) {
        this.endState = endState;
    }
}
