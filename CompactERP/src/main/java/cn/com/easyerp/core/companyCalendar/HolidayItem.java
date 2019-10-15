package cn.com.easyerp.core.companyCalendar;

import java.util.Date;

public class HolidayItem {
    private Date date;
    private int is_holiday;

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIs_holiday() {
        return this.is_holiday;
    }

    public void setIs_holiday(int is_holiday) {
        this.is_holiday = is_holiday;
    }
}
