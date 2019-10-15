package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class CheckWorkRequestModel extends FormRequestModelBase<CheckWorkModel> {
    private String date;
    private String beginDate;
    private String endDate;

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBeginDate() {
        return this.beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return this.endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
