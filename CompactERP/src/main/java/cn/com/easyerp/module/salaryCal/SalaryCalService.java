package cn.com.easyerp.module.salaryCal;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.companyCalendar.AttendanceInfo;
import cn.com.easyerp.core.companyCalendar.AttendanceJson;
import cn.com.easyerp.core.companyCalendar.CheckWorkJson;
import cn.com.easyerp.core.companyCalendar.HolidayItem;
import cn.com.easyerp.core.companyCalendar.Week;
import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.dao.CheckWorkDao;
import cn.com.easyerp.core.dao.SalaryCalDao;

@Service
public class SalaryCalService {
    @Autowired
    private CalendarDao calendarDao;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private CheckWorkDao checkWorkDao;
    @Autowired
    private SalaryCalDao salaryCalDao;

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

    public List<CheckWorkJson> selectAttendances(List<EmpInfo> users, String beginDate, String endDate)
            throws ParseException {
        List<Integer> list = selectWorkPlanByDate(beginDate, endDate);
        List<CheckWorkJson> checkWorks = new ArrayList<CheckWorkJson>();
        CheckWorkJson checkWorkJson = null;
        for (EmpInfo emp : users) {
            checkWorkJson = new CheckWorkJson();
            checkWorkJson.setEmpInfo(emp);
            checkWorkJson.setUser(emp.getEmp_id());
            checkWorkJson.setList(getUserAttendance(emp.getEmp_id(), beginDate, endDate, list));
            checkWorks.add(checkWorkJson);
        }
        return checkWorks;
    }

    private List<AttendanceJson> getUserAttendance(String user_id, String beginDate, String endDate,
            List<Integer> list) {
        List<AttendanceJson> attendances = new ArrayList<AttendanceJson>();
        try {
            AttendanceJson aJson = null;
            for (int i = 0; i < list.size(); i++) {
                aJson = new AttendanceJson();
                String date = getIntervalDate(sdf.parse(beginDate), i);
                aJson.setWorkDate(date);
                if (((Integer) list.get(i)).intValue() == 1) {
                    aJson.setStartClass("loss");
                    aJson.setEndClass("loss");
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
                    aJson2.setStartClass("early");
                } else {
                    aJson2.setStartClass("common");
                }
                if (aInfo.getIs_leave() == null) {
                    aJson2.setEndClass("early");
                } else if (aInfo.getIs_leave().intValue() == 1) {
                    aJson2.setEndClass("early");
                } else {
                    aJson2.setEndClass("common");
                }
                attendances.set(m, aJson2);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public List<EmpInfo> selectEmpIds() {
        return this.salaryCalDao.selectEmpIds();
    }

    public List<UserHoliday> selectUserHoliday(String user) {
        return this.salaryCalDao.selectUserHoliday(user);
    }

    public List<Map<String, Object>> selectUserAddWork(String user) {
        return this.salaryCalDao.selectUserAddWork(user);
    }

    public Map<String, Double> selectAddWorkRule() {
        return this.salaryCalDao.selectAddWorkRule();
    }

    public Map<String, Double> selectSecurityScale() {
        return this.salaryCalDao.selectSecurityScale();
    }

    public void savaUserSalary(List<UserSalary> salaries) {
        this.salaryCalDao.savaUserSalary(salaries);
    }

    public void deleteSalary(String date) {
        this.salaryCalDao.deleteSalary(date);
    }
}