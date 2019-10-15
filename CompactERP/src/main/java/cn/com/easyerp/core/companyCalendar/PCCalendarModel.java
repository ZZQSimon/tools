package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.widget.WidgetModelBase;

public class PCCalendarModel extends WidgetModelBase {
    private CalendarEvent calendarEvent;

    public PCCalendarModel(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    public CalendarEvent getCalendarEvent() {
        return this.calendarEvent;
    }

    public void setCalendarEvent(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }
}
