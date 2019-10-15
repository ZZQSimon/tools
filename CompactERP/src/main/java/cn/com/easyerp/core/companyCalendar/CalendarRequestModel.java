package cn.com.easyerp.core.companyCalendar;

import java.util.Map;

import cn.com.easyerp.core.companyCalendar.appointment.AppointmentDescribe;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class CalendarRequestModel extends FormRequestModelBase<CalendarModel> {
    private String type;
    private String sourceTable;
    private String begin_time;
    private String end_time;
    private String group;
    private String color;
    private String filter;
    private String eventDate;
    private String calendar_id;
    private String p_calendar_event_id;
    private Map<String, CalendarEvent> calendarEvents;
    private CalendarEvent calendarEvent;
    private AppointmentDescribe appointmentParam;

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSourceTable() {
        return this.sourceTable;
    }

    public void setSourceTable(String sourceTable) {
        this.sourceTable = sourceTable;
    }

    public String getBegin_time() {
        return this.begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return this.end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFilter() {
        return this.filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public CalendarEvent getCalendarEvent() {
        return this.calendarEvent;
    }

    public String getEventDate() {
        return this.eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public void setCalendarEvent(CalendarEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    public String getCalendar_id() {
        return this.calendar_id;
    }

    public void setCalendar_id(String calendar_id) {
        this.calendar_id = calendar_id;
    }

    public String getP_calendar_event_id() {
        return this.p_calendar_event_id;
    }

    public void setP_calendar_event_id(String p_calendar_event_id) {
        this.p_calendar_event_id = p_calendar_event_id;
    }

    public Map<String, CalendarEvent> getCalendarEvents() {
        return this.calendarEvents;
    }

    public void setCalendarEvents(Map<String, CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }

    public AppointmentDescribe getAppointmentParam() {
        return this.appointmentParam;
    }

    public void setAppointmentParam(AppointmentDescribe appointmentParam) {
        this.appointmentParam = appointmentParam;
    }
}
