package cn.com.easyerp.core.companyCalendar;

import java.util.List;

public class CheckWorkResult {
    private List<Integer> plan;
    private List<CheckWorkJson> list;

    public List<Integer> getPlan() {
        return this.plan;
    }

    private Object sEcho;
    private Object iTotalRecords;
    private Object iTotalDisplayRecords;

    public void setPlan(List<Integer> plan) {
        this.plan = plan;
    }

    public List<CheckWorkJson> getList() {
        return this.list;
    }

    public void setList(List<CheckWorkJson> list) {
        this.list = list;
    }

    public Object getsEcho() {
        return this.sEcho;
    }

    public void setsEcho(Object sEcho) {
        this.sEcho = sEcho;
    }

    public Object getiTotalRecords() {
        return this.iTotalRecords;
    }

    public void setiTotalRecords(Object iTotalRecords) {
        this.iTotalRecords = iTotalRecords;
    }

    public Object getiTotalDisplayRecords() {
        return this.iTotalDisplayRecords;
    }

    public void setiTotalDisplayRecords(Object iTotalDisplayRecords) {
        this.iTotalDisplayRecords = iTotalDisplayRecords;
    }
}
