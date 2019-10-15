package cn.com.easyerp.core.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.companyCalendar.CalendarEvent;
import cn.com.easyerp.core.companyCalendar.CalendarEventShare;
import cn.com.easyerp.core.companyCalendar.HolidayItem;
import cn.com.easyerp.core.companyCalendar.MobileCalendarRequestModel;
import cn.com.easyerp.core.companyCalendar.PagingDesctibe;
import cn.com.easyerp.core.companyCalendar.Week;
import cn.com.easyerp.core.companyCalendar.WorkCalendarDescribe;
import cn.com.easyerp.core.companyCalendar.appointment.AppointmentDescribe;

@Repository
public interface CalendarDao {
    Map<String, Object> selectCalendar(@Param("id") String paramString);

    Map<String, Object> selectRule(@Param("id") String paramString);

    List<Map<String, Object>> selectSet(@Param("calendar_id") String paramString);

    int selectSetcount(@Param("calendar_id") String paramString1, @Param("date") String paramString2);

    void updateRule(@Param("calendarId") String paramString, @Param("monday") int paramInt1,
            @Param("tuesday") int paramInt2, @Param("wednesday") int paramInt3, @Param("thursday") int paramInt4,
            @Param("friday") int paramInt5, @Param("saturday") int paramInt6, @Param("sunday") int paramInt7);

    void deleteSet(@Param("currentDate") String paramString);

    void saveWorkday(@Param("calendarId") String paramString, @Param("dates") List<String> paramList);

    void saveHoliday(@Param("calendarId") String paramString, @Param("dates") List<String> paramList);

    List<String> selectDates(@Param("calendarId") String paramString1, @Param("type") int paramInt,
            @Param("currentDate") String paramString2);

    void updateSync(@Param("calendarId") String paramString, @Param("sync") int paramInt);

    Map<String, Object> selectSync(@Param("calendarId") String paramString);

    List<WorkCalendarDescribe> getEventsByDate(@Param("queryDate") String paramString1,
            @Param("users") String paramString2);

    List<WorkCalendarDescribe> getEventDaysByMonth(@Param("users") String paramString);

    List<WorkCalendarDescribe> findEventsListByNum(@Param("queryDate") String paramString1,
            @Param("desctibe") PagingDesctibe paramPagingDesctibe, @Param("users") String paramString2);

    ArrayList<Week> selectWorkPlanByDate(@Param("beginDate") String paramString1, @Param("endDate") String paramString2,
            @Param("users") String paramString3);

    List<Map<String, Object>> findDateByCalendarId();

    Integer findIsHolidayByDateFromCalendarSet(String paramString);

    Week findDateById();

    Week findWeekPlan();

    List<HolidayItem> selectHolidayDate(@Param("calendarId") String paramString1,
            @Param("beginDate") String paramString2, @Param("endDate") String paramString3);

    List<CalendarEventShare> selectCalendarEventShare(@Param("calendarId") String paramString1,
            @Param("userId") String paramString2);

    List<CalendarEvent> selectCalendarEvent(@Param("calendarId") String paramString1,
            @Param("userId") String paramString2, @Param("eventDate") String paramString3);

    List<Map<String, Object>> selectAppointmentEvent(
            @Param("appointment") AppointmentDescribe paramAppointmentDescribe);

    List<CalendarEventShare> selectCalendarEventShareById(@Param("calendarEventId") String paramString);

    CalendarEvent selectCalendarEventById(@Param("calendarId") String paramString1,
            @Param("calendarEventId") String paramString2);

    List<CalendarEvent> selectCalendarEventByTable(@Param("calendarId") String paramString1,
            @Param("ref_table") String paramString2, @Param("ref_table_data") String paramString3);

    int updateCalendarEvents(@Param("calendarEvent") CalendarEvent paramCalendarEvent);

    int addCalendarEvents(@Param("calendarEvent") CalendarEvent paramCalendarEvent);

    int updateCalendarEventsByTable(@Param("calendarEvent") CalendarEvent paramCalendarEvent);

    int deleteCalendarEventsByTable(@Param("calendarEvent") CalendarEvent paramCalendarEvent);

    int deleteBatchCalendarEventsByTable(@Param("calendarEvents") List<CalendarEvent> paramList);

    int updateCalendarEventShare(@Param("calendarEventShare") CalendarEventShare paramCalendarEventShare);

    int deleteCalendarEventShare(@Param("calendarEvent") CalendarEvent paramCalendarEvent,
            @Param("users") Map<String, Object> paramMap);

    int addCalendarEventShare(@Param("calendarEventShare") CalendarEventShare paramCalendarEventShare);

    String getMaxId(@Param("calendarEventId") String paramString);

    String getCalendarEventMaxId();

    List<Map<String, Object>> selectRefTableData();

    List<Map<String, Object>> getEventTag(@Param("request") MobileCalendarRequestModel paramMobileCalendarRequestModel,
            @Param("user") AuthDetails paramAuthDetails);

    Map<String, Object> getCommonRule(@Param("request") MobileCalendarRequestModel paramMobileCalendarRequestModel);

    List<Map<String, Object>> getSpecialRule(
            @Param("request") MobileCalendarRequestModel paramMobileCalendarRequestModel);
}
