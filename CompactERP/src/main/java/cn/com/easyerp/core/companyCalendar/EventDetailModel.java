package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("eventDetail")
public class EventDetailModel extends FormModelBase {
    private CalendarEvent calendarEvent;
    private String type;

    public CalendarEvent getCalendarEvent() {
        return this.calendarEvent;
    }

    public void setCalendarEvent(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    protected EventDetailModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "eventDetail";
    }
}
