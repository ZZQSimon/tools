package cn.com.easyerp.core.data;

import java.util.List;

import cn.com.easyerp.core.companyCalendar.PCCalendarModel;

public class CalendarJson {
    private List<Integer> weekdays;
    private List<String> holidays;
    private List<String> workdays;
    private List<PCCalendarModel> pcCalendarModels;
    private int isSync;

    public int getIsSync() {
        return this.isSync;
    }

    public void setIsSync(int isSync) {
        this.isSync = isSync;
    }

    public List<Integer> getWeekdays() {
        return this.weekdays;
    }

    public void setWeekdays(List<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public List<String> getHolidays() {
        return this.holidays;
    }

    public void setHolidays(List<String> holidays) {
        this.holidays = holidays;
    }

    public List<String> getWorkdays() {
        return this.workdays;
    }

    public void setWorkdays(List<String> workdays) {
        this.workdays = workdays;
    }

    public List<PCCalendarModel> getPcCalendarModels() {
        return this.pcCalendarModels;
    }

    public void setPcCalendarModels(List<PCCalendarModel> pcCalendarModels) {
        this.pcCalendarModels = pcCalendarModels;
    }
}
