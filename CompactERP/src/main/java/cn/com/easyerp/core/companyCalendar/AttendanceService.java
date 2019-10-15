package cn.com.easyerp.core.companyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.dao.AttendanceDao;
import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.dao.CheckWorkDao;

@Service
public class AttendanceService {
    @Autowired
    private AttendanceDao attendanceDao;
    @Autowired
    private CalendarDao calendarDao;
    @Autowired
    private CheckWorkDao checkWorkDao;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    public List<Integer> selectWorkPlanByDate(String beginDate, String endDate) throws ParseException {
        List<Integer> list = new ArrayList<Integer>();
        Week week = this.calendarDao.findWeekPlan();

        int[] days = { week.getMonday(), week.getTuesday(), week.getWednesday(), week.getThursday(), week.getFriday(),
                week.getSaturday(), week.getSunday() };
        List<HolidayItem> holidays = this.calendarDao.selectHolidayDate("0000000001", beginDate, endDate);
        for (int i = 0; i < 5; i++) {
            for (int day : days) {
                list.add(Integer.valueOf(day));
            }
        }

        int m = 0;
        for (int i = 0; i < holidays.size(); i++) {
            HolidayItem holiday = (HolidayItem) holidays.get(i);
            m = getIntervalDays(sdf.parse(sdf.format(holiday.getDate())), sdf.parse(beginDate));
            list.set(m, Integer.valueOf(holiday.getIs_holiday()));
        }

        return list;
    }

    public List<AttendanceJson> selectAttendances(String user_id, String beginDate, String endDate)
            throws ParseException {
        List<Integer> list = selectWorkPlanByDate(beginDate, endDate);
        List<AttendanceJson> attendances = new ArrayList<AttendanceJson>();
        AttendanceJson aJson = null;
        Date curDate = new Date();
        for (int i = 0; i < list.size(); i++) {
            aJson = new AttendanceJson();
            String date = getIntervalDate(sdf.parse(beginDate), i);
            aJson.setWorkDate(date);

            if (getIntervalDays(sdf.parse(date), sdf.parse(sdf.format(curDate))) > 0) {
                aJson.setStartClass("");
                aJson.setEndClass("");
            } else if (((Integer) list.get(i)).intValue() == 1) {
                aJson.setStartClass("a-loss");
                aJson.setEndClass("b-loss");
                aJson.setStartTime("-:-:-");
                aJson.setEndTime("-:-:-");
            } else {
                aJson.setStartClass("");
                aJson.setEndClass("");
            }

            attendances.add(aJson);
        }

        List<AttendanceInfo> attendanceInfos = this.checkWorkDao.selectAttendances(user_id, beginDate, endDate);

        String date = "";

        int m = 0;

        for (int i = 0; i < attendanceInfos.size(); i++) {
            AttendanceInfo aInfo = (AttendanceInfo) attendanceInfos.get(i);
            date = sdf.format(aInfo.getWorkDate());
            m = getIntervalDays(sdf.parse(date), sdf.parse(beginDate));
            AttendanceJson aJson2 = (AttendanceJson) attendances.get(m);

            if (aInfo.getIs_late().intValue() == 1) {
                aJson2.setStartClass("a-early");
            } else {
                aJson2.setStartClass("a-common");
            }
            aJson2.setStartTime(sdfTime.format(aInfo.getStart_work_time()));
            if (aInfo.getIs_leave() == null) {
                aJson2.setEndClass("b-loss");
                aJson2.setEndTime("-:-:-");
            } else if (aInfo.getIs_leave().intValue() == 1) {
                aJson2.setEndClass("b-early");
                aJson2.setEndTime(sdfTime.format(aInfo.getEnd_work_time()));
            } else {
                aJson2.setEndClass("b-common");
                aJson2.setEndTime(sdfTime.format(aInfo.getEnd_work_time()));
            }

            attendances.set(m, aJson2);
        }

        return attendances;
    }

    public static int getIntervalDays(Date fromDate, Date toDate) {
        if (null == fromDate || null == toDate) {
            return -1;
        }
        return (int) ((fromDate.getTime() - toDate.getTime()) / 86400000L);
    }

    public static String getIntervalDate(Date fromDate, int interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(5, interval);
        return sdf.format(Long.valueOf(calendar.getTimeInMillis()));
    }
}
