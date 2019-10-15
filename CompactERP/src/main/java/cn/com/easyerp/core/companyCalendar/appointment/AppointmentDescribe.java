package cn.com.easyerp.core.companyCalendar.appointment;

import java.util.Date;

import cn.com.easyerp.auth.AuthDetails;

public class AppointmentDescribe {
    private String id;
    private String user_id;
    private String table;
    private Object data_id;
    private String memo;
    private Date begin_time;
    private Date end_time;
    private String cre_user;
    private Date cre_date;
    private String upd_user;
    private Date upd_date;
    private String owner;
    private String status;
    private String color;
    private String year;
    private AuthDetails user;
    private String type;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Object getData_id() {
        return this.data_id;
    }

    public void setData_id(Object data_id) {
        this.data_id = data_id;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Date getBegin_time() {
        return this.begin_time;
    }

    public void setBegin_time(Date begin_time) {
        this.begin_time = begin_time;
    }

    public Date getEnd_time() {
        return this.end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getCre_user() {
        return this.cre_user;
    }

    public void setCre_user(String cre_user) {
        this.cre_user = cre_user;
    }

    public Date getCre_date() {
        return this.cre_date;
    }

    public void setCre_date(Date cre_date) {
        this.cre_date = cre_date;
    }

    public String getUpd_user() {
        return this.upd_user;
    }

    public void setUpd_user(String upd_user) {
        this.upd_user = upd_user;
    }

    public Date getUpd_date() {
        return this.upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public AuthDetails getUser() {
        return this.user;
    }

    public void setUser(AuthDetails user) {
        this.user = user;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
