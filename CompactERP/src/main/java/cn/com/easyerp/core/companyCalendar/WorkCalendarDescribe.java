package cn.com.easyerp.core.companyCalendar;

public class WorkCalendarDescribe {
    private String date_id;
    private String table_id;
    private String column_name;
    private String eventDate;
    private String owner;
    private String memo;

    public String getDate_id() {
        return this.date_id;
    }

    public void setDate_id(String date_id) {
        this.date_id = date_id;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getEventDate() {
        return this.eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
