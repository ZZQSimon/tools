package cn.com.easyerp.calendar;

import java.util.List;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.TableFormRequestModel;

public class CalendarModel<T extends FormModelBase> extends TableFormRequestModel<T> {
    private String calendar_id;
    private List<Integer> weekdays;
    private List<String> holidays;
    private List<String> workdays;

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

    public String getCalendar_id() {
        return this.calendar_id;
    }

    public void setCalendar_id(String calendar_id) {
        this.calendar_id = calendar_id;
    }
}
