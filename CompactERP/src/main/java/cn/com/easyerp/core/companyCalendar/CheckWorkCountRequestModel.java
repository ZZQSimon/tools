package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class CheckWorkCountRequestModel extends FormRequestModelBase<CheckWorkCountModel> {
    private String year;
    private String month;
    private String name;
    private String aodata;

    public String getAodata() {
        return this.aodata;
    }

    public void setAodata(String aodata) {
        this.aodata = aodata;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
