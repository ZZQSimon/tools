package cn.com.easyerp.core.companyCalendar;

public class CalendarEventShare {
    private String p_calendar_event_id;
    private String p_calendar_event_share_id;
    private String user_id;
    private String user_name;
    private String power;
    private String memo;

    public String getP_calendar_event_id() {
        return this.p_calendar_event_id;
    }

    public void setP_calendar_event_id(String p_calendar_event_id) {
        this.p_calendar_event_id = p_calendar_event_id;
    }

    public String getP_calendar_event_share_id() {
        return this.p_calendar_event_share_id;
    }

    public void setP_calendar_event_share_id(String p_calendar_event_share_id) {
        this.p_calendar_event_share_id = p_calendar_event_share_id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return this.user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPower() {
        return this.power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
