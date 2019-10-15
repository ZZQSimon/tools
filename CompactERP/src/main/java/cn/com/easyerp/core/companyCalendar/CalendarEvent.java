package cn.com.easyerp.core.companyCalendar;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.com.easyerp.framework.common.Common;

public class CalendarEvent {
    private String calendar_id;
    private String p_calendar_event_id;
    private String event_name;
    private Date begin_date;
    private Date end_date;
    private String ref_table;
    private String ref_table_column;
    private String ref_table_data;
    private Map<String, Object> ref_table_data_map;
    private String color;
    private String owner;
    private String power;
    private String content;
    private String uploadFile;
    private String file;
    private int isPublic;
    private String memo;
    private Map<String, CalendarEventShare> calendarEventShare;
    private Map<String, Map<String, Object>> users;
    private String status;

    public String getCalendar_id() {
        return this.calendar_id;
    }

    public void setCalendar_id(String calendar_id) {
        this.calendar_id = calendar_id;
    }

    public Map<String, Object> getRef_table_data_map() {
        return this.ref_table_data_map;
    }

    public String getP_calendar_event_id() {
        return this.p_calendar_event_id;
    }

    public void setP_calendar_event_id(String p_calendar_event_id) {
        this.p_calendar_event_id = p_calendar_event_id;
    }

    public String getEvent_name() {
        return this.event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
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

    public String getRef_table() {
        return this.ref_table;
    }

    public void setRef_table(String ref_table) {
        this.ref_table = ref_table;
    }

    public String getRef_table_column() {
        return this.ref_table_column;
    }

    public void setRef_table_column(String ref_table_column) {
        this.ref_table_column = ref_table_column;
    }

    public String getRef_table_data() {
        return this.ref_table_data;
    }

    public void setRef_table_data(String ref_table_data) {
        this.ref_table_data = ref_table_data;
        this.ref_table_data_map = Common.paseEventParam(ref_table_data);
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPower() {
        return this.power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public Map<String, CalendarEventShare> getCalendarEventShare() {
        return this.calendarEventShare;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUploadFile() {
        return this.uploadFile;
    }

    public void setUploadFile(String uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getFile() {
        return this.file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getIsPublic() {
        return this.isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void addCalendarEventShare(CalendarEventShare calendarEventShare) {
        if (this.calendarEventShare == null) {
            this.calendarEventShare = new HashMap<>();
        }
        this.calendarEventShare.put(calendarEventShare.getUser_id(), calendarEventShare);
    }

    public Map<String, Map<String, Object>> getUsers() {
        return this.users;
    }

    public void setUsers(Map<String, Map<String, Object>> users) {
        this.users = users;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
