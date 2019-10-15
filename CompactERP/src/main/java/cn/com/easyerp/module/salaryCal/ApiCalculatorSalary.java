 package cn.com.easyerp.module.salaryCal;
 
 import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.auth.weixin.ToJson;
import cn.com.easyerp.core.api.ApiResult;
import cn.com.easyerp.core.api.annotations.Api;
import cn.com.easyerp.core.api.annotations.ApiParam;
import cn.com.easyerp.core.companyCalendar.AttendanceJson;
import cn.com.easyerp.core.companyCalendar.CheckWorkJson;
 
@Service
@Api
public class ApiCalculatorSalary {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdfT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   
	@Autowired
	private SalaryCalService salaryCalService;
   
	public ApiResult test(@ApiParam("UUID") List<String> uuid, @ApiParam("date") List<String> dates) {
		String date = (String)dates.get(0);
		String odate = date;
		date = date.substring(0, 4) + "-" + date.substring(4);
		AuthDetails auth = AuthService.getCurrentUser();
		ArrayList<String> messages = new ArrayList<String>();
		int flag = 0;
		try {
			Date firstDay = sdf.parse(date + "-" + "01");
			Date lastDay = getLastDayOfMonth(Integer.valueOf(date.split("-")[0]).intValue(), Integer.valueOf(date.split("-")[1]).intValue());
			int pre = getWeekOfDate(firstDay) - 1;
			String beginDate = sdf.format(firstDay);
			if (pre > 0) {
				beginDate = getIntervalDate(firstDay, -pre);
			}
			int last = 7 - getWeekOfDate(lastDay);
			String endDate = sdf.format(lastDay);
			if (last > 0) {
				endDate = getIntervalDate(lastDay, last);
			}
			List<EmpInfo> users = this.salaryCalService.selectEmpIds();
			List<CheckWorkJson> list = this.salaryCalService.selectAttendances(users, beginDate, endDate);
			int end = Integer.valueOf(sdf.format(lastDay).split("-")[2]).intValue();
			for (int i = 0; i < list.size(); i++) {
				List<AttendanceJson> attendances = ((CheckWorkJson)list.get(i)).getList();
				for (int j = 0; j < pre; j++) {
					attendances.remove(0);
				}
				for (int j = 0; j < last; j++) {
					attendances.remove(end);
				}
			} 
			String user = "";
			Map<String, Double> mmp = this.salaryCalService.selectAddWorkRule();
			Map<String, Double> sMap = this.salaryCalService.selectSecurityScale();
			double security = ((Double)sMap.get("s")).doubleValue() * ((Double)sMap.get("s1")).doubleValue() * ((Double)sMap.get("s2")).doubleValue() * ((Double)sMap.get("s3")).doubleValue() * ((Double)sMap.get("s4")).doubleValue();
       
       double accumulation = ((Double)sMap.get("c")).doubleValue() * ((Double)sMap.get("c1")).doubleValue();
       
       List<UserSalary> salaries = new ArrayList<UserSalary>();
       
       for (int i = 0; i < list.size(); i++) {
         user = ((CheckWorkJson)list.get(i)).getUser();
         List<AttendanceJson> aJsons = ((CheckWorkJson)list.get(i)).getList();
         EmpInfo empInfo = ((CheckWorkJson)list.get(i)).getEmpInfo();
         
         List<UserHoliday> holidays = this.salaryCalService.selectUserHoliday(user);
         
         double holidayMoney = 0.0D;
         double baseSalary = empInfo.getBase_salary() / 21.75D;
         for (int j = 0; j < holidays.size(); j++) {
           holidayMoney += baseSalary * ((UserHoliday)holidays.get(i)).getHow_long() * ((UserHoliday)holidays.get(i)).getPercent();
         }
 
         
         Map<String, Integer> map = getAttendanceDetail(aJsons, holidays);
         
         double allDayMoney = empInfo.getAllDaysAward();
 
         
         double lossMoney = ((Integer)map.get("loss")).intValue() * baseSalary;
         
         double lateMoney = ((Integer)map.get("lateOrEarly")).intValue() * empInfo.getLate();
         if (holidayMoney > 0.0D || lossMoney > 0.0D || lateMoney > 0.0D) {
           allDayMoney = 0.0D;
         }
         
         double subsideMoney = empInfo.getSubsidy();
         
         double performanceMoney = empInfo.getPerformance_salary();
         
         double addWorkMoney = 0.0D;
         List<Map<String, Object>> aMaps = this.salaryCalService.selectUserAddWork(user);
         String type = "";
         double times = 0.0D;
         for (int j = 0; j < aMaps.size(); j++) {
           type = (String)(aMaps.get(j)).get("type");
           times = ((Integer)(aMaps.get(j)).get("times")).intValue();
           addWorkMoney += ((Double)mmp.get(type)).doubleValue() * times;
         } 
         
         double preSalary = empInfo.getBase_salary() - lossMoney - lateMoney - holidayMoney + allDayMoney + subsideMoney + performanceMoney;
 
         
         double point = 3500.0D;
         String taxType = empInfo.getIs_company_deputy();
         
         if ("1".equals(empInfo.getIs_foreign())) {
           point = 4800.0D;
         }
         
         double tax = 0.0D;
         
         preSalary -= point;
         if (preSalary > 0.0D) {
           tax = getTax(preSalary, taxType);
           if (!"1".equals(empInfo.getIs_company_deputy())) {
             preSalary -= tax;
           }
         } 
         
         UserSalary salary = new UserSalary();
         salary.setEmp_id(empInfo.getEmp_id());
         salary.setSalary_date(odate);
         salary.setEmp_number(empInfo.getEmp_number());
         salary.setName(empInfo.getName());
         salary.setBase_salary(empInfo.getBase_salary());
         salary.setReality_salary(preSalary);
         salary.setPerformance_salary(performanceMoney);
         salary.setAdd_work_salary(addWorkMoney);
         salary.setTerm_of_validity(allDayMoney);
         salary.setSubsidy_salary(subsideMoney);
         salary.setDeputy_security(security);
         salary.setDeputy_accumulation(accumulation);
         salary.setPersonal_income_tax(tax);
         salary.setLeave_salary(lossMoney);
         salary.setAbsence_work_salary(lateMoney);
         salary.setSalary_status("01");
         salary.setCre_user(auth.getId());
         salary.setUpd_user(auth.getId());
         salary.setCre_date(sdfT.format(new Date()));
         salary.setUpd_date(sdfT.format(new Date()));
         salaries.add(salary);
         System.err.println(ToJson.toJson(salaries));
       } 
       
       if (salaries.size() > 0) {
         this.salaryCalService.deleteSalary(date);
         this.salaryCalService.savaUserSalary(salaries);
       }
     
     } catch (Exception e) {
       e.printStackTrace();
       messages.add("处理数据失败");
       flag = 1;
     } 
     
     return new ApiResult(flag, messages);
   }
   
   private double getTax(double salary, String type) {
     double scala = 0.0D;
     double deduct = 0.0D;
     if ("1".equals(type)) {
       if (salary <= 1455.0D) {
         scala = 0.03D;
         deduct = 0.0D;
       } else if (salary > 1455.0D && salary <= 4155.0D) {
         scala = 0.1D;
         deduct = 105.0D;
       } else if (salary > 4155.0D && salary <= 7755.0D) {
         scala = 0.2D;
         deduct = 555.0D;
       } else if (salary > 7755.0D && salary <= 27255.0D) {
         scala = 0.25D;
         deduct = 1005.0D;
       } else if (salary > 27255.0D && salary <= 41255.0D) {
         scala = 0.3D;
         deduct = 2755.0D;
       } else if (salary > 41255.0D && salary <= 57505.0D) {
         scala = 0.35D;
         deduct = 5505.0D;
       } else if (salary > 57505.0D) {
         scala = 0.45D;
         deduct = 13505.0D;
       }
     
     } else if (salary <= 1500.0D) {
       scala = 0.03D;
       deduct = 0.0D;
     } else if (salary > 1500.0D && salary <= 4500.0D) {
       scala = 0.1D;
       deduct = 105.0D;
     } else if (salary > 4500.0D && salary <= 9000.0D) {
       scala = 0.1D;
       deduct = 105.0D;
     } else if (salary > 9000.0D && salary <= 35000.0D) {
       scala = 0.2D;
       deduct = 555.0D;
     } else if (salary > 35000.0D && salary <= 55000.0D) {
       scala = 0.25D;
       deduct = 1005.0D;
     } else if (salary > 55000.0D && salary <= 80000.0D) {
       scala = 0.3D;
       deduct = 2755.0D;
     } else if (salary > 80000.0D) {
       scala = 0.45D;
       deduct = 13505.0D;
     } 
     return salary * scala - deduct;
   }
   
   private UserHoliday getHoliday(String date, List<UserHoliday> holidays) {
     for (UserHoliday holiday : holidays) {
       if (date.compareTo(holiday.getBegin_date()) >= 0 && date.compareTo(holiday.getEnd_date()) <= 0) {
         return holiday;
       }
     } 
     return null;
   }
   
   private Map<String, Integer> getAttendanceDetail(List<AttendanceJson> attendances, List<UserHoliday> holidays) {
     Map<String, Integer> map = new HashMap<String, Integer>();
     int loss = 0;
     int lateOrEarly = 0;
     for (int i = 0; i < attendances.size(); i++) {
       AttendanceJson aJson = (AttendanceJson)attendances.get(i);
       UserHoliday holiday = getHoliday(aJson.getWorkDate(), holidays);
       if (holiday == null) {
         if ("loss".equals(aJson.getStartClass())) {
           loss = loss++;
         } else if ("early".equals(aJson.getStartClass())) {
           lateOrEarly++;
           if ("loss".equals(aJson.getEndClass())) {
             loss++;
           } else if ("early".equals(aJson.getEndClass())) {
             lateOrEarly++;
           }
         
         } else if ("loss".equals(aJson.getEndClass())) {
           loss++;
         } else if ("early".equals(aJson.getEndClass())) {
           lateOrEarly++;
         } 
       } else {
         
         int type = 0;
         
         if (aJson.getWorkDate().equals(holiday.getBegin_date()) || aJson
           .getWorkDate().equals(holiday.getEnd_date())) {
           if ("01".equals(holiday.getBegin_time()) && "01".equals(holiday.getEnd_time())) {
             type = 1;
           } else if ("01".equals(holiday.getBegin_time()) && "02".equals(holiday.getEnd_time())) {
             type = 2;
           } else if ("02".equals(holiday.getBegin_time()) && "02".equals(holiday.getEnd_time())) {
             type = 3;
           } 
         } else if (aJson.getWorkDate().compareTo(holiday.getBegin_date()) > 0 && aJson
           .getWorkDate().compareTo(holiday.getBegin_date()) < 0) {
           type = 4;
         } 
         
         if ("loss".equals(aJson.getStartClass())) {
           
           if (type != 2 && type != 4) {
             loss = loss++;
           }
         } else if ("early".equals(aJson.getStartClass())) {
           if (type != 1 && type != 2 && type != 4) {
             lateOrEarly++;
           }
           if ("loss".equals(aJson.getEndClass())) {
             if (type != 2 && type != 3 && type != 4) {
               loss = loss++;
             }
           } else if ("early".equals(aJson.getEndClass()) && 
             type != 2 && type != 3 && type != 4) {
             lateOrEarly++;
           }
         
         }
         else if ("loss".equals(aJson.getEndClass())) {
           if (type != 2 && type != 3 && type != 4) {
             loss = loss++;
           }
         } else if ("early".equals(aJson.getEndClass()) && 
           type != 2 && type != 3 && type != 4) {
           lateOrEarly++;
         } 
       } 
     } 
     
     map.put("loss", Integer.valueOf(loss));
     map.put("lateOrEarly", Integer.valueOf(lateOrEarly));
     return map;
   }
   
   private static int getWeekOfDate(Date dt) {
     Calendar cal = Calendar.getInstance();
     cal.setTime(dt);
     int w = cal.get(7) - 1;
     if (w == 0)
       w = 7; 
     return w;
   }
   
   private static Date getLastDayOfMonth(int year, int month) {
     Calendar calendar = Calendar.getInstance();
     calendar.set(1, year);
     calendar.set(2, month - 1);
     calendar.set(5, calendar.getActualMaximum(5));
     return calendar.getTime();
   }
   
   private static String getIntervalDate(Date fromDate, int interval) {
     Calendar calendar = Calendar.getInstance();
     calendar.setTime(fromDate);
     calendar.add(5, interval);
     return sdf.format(Long.valueOf(calendar.getTimeInMillis()));
   }
 }