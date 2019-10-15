package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class MobileCalendarRequestModel extends FormRequestModelBase<MobileCalendarModel> {
    private String month;
    private String date;

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
