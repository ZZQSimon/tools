 package cn.com.easyerp.module.customReport.dashboard;
 
 import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.module.dao.AdidasDashboardDao;
 
 
 
 
 
 @Service
 public class AdidasDashboardService
 {
   private static final String PERIOD = "period";
   private static final String REGION = "region";
   private static final String STORE_TYPE = "store_type";
   private static final String EMPLOYEE_TYPE = "employee_type";
   private static final String STORE = "store";
   private static final String GRADE = "grade";
   DecimalFormat df = new DecimalFormat("0.00"); private static final String ONE_CHART = "one"; private static final String TWO_CHART = "two"; private static final String THREE_CHART = "three"; private static final String FOUR_CHART = "four"; private static final String FIVE_CHART = "five"; private static final String SIX_CHART = "six"; @Autowired
   private AdidasDashboardDao adidasDashboardDao;
   
   public ActionResult diversitySelect(FilterRequestModel filter) {
     Map<String, FilterDescribe> filters = filter.getFilters();
     if (filters == null || filters.get("period") == null || ((FilterDescribe)filters.get("period")).getTo() == null) {
       return new ActionResult(false, "no date selected");
     }
     FilterData filterData = getFilterData(filter);
     
     Map<String, Object> hcFteData = hcFteSelect(filterData);
     
     Map<String, Object> contractTypeData = contractTypeSelect(filterData);
     
     Map<String, Object> lengthServiceData = lengthServiceSelect(filterData);
     
     Map<String, Object> distriButtonData = distriButtonSelect(filterData);
     
     Map<String, Object> ageData = ageSelect(filterData);
     
     Map<String, Object> workYearData = workYearSelect(filterData);
     Map<String, Object> resultData = new HashMap<String, Object>();
     resultData.put("one", hcFteData);
     resultData.put("two", contractTypeData);
     resultData.put("three", lengthServiceData);
     resultData.put("four", distriButtonData);
     resultData.put("five", ageData);
     resultData.put("six", workYearData);
     return new ActionResult(true, resultData);
   }
   
   private Map<String, Object> hcFteSelect(FilterData filterData) {
     List<Map<String, Object>> data = this.adidasDashboardDao.hcFteSelect(filterData);
     Map<String, Object> resultData = new HashMap<String, Object>();
     resultData.put("Intern", Integer.valueOf(0));
     resultData.put("FTE", Integer.valueOf(0));
     if (data == null || data.size() == 0) {
       return resultData;
     }
     for (int i = 0; i < data.size(); i++) {
       if (((Map)data.get(i)).get("employee_type") != null)
       {
         resultData.put(((Map)data.get(i)).get("employee_type").toString(), ((Map)data.get(i)).get("emp_count"));
       }
     } 
     return resultData;
   }
   private Date getLastDayOfMonth(Date date) {
     Calendar cal = Calendar.getInstance();
     cal.setTime(date);
     
     int lastDay = cal.getActualMaximum(5);
     
     cal.set(5, lastDay);
     return cal.getTime();
   }
   private Date getNextMonth(Date date) {
     Calendar cal = Calendar.getInstance();
     cal.setTime(date);
     cal.add(2, 1);
     return cal.getTime();
   }
   
   private Map<String, Object> contractTypeSelect(FilterData filterData) {
     List<Map<String, Object>> data = this.adidasDashboardDao.contractTypeSelect(filterData);
     Map<String, Object> resultData = new HashMap<String, Object>();
     resultData.put("one", Integer.valueOf(0));
     resultData.put("two", Integer.valueOf(0));
     resultData.put("three", Integer.valueOf(0));
     resultData.put("Open-Ended", Integer.valueOf(100));
     Integer allCount = Integer.valueOf(0);
     if (data == null || data.size() == 0) {
       return resultData;
     }
     for (int i = 0; i < data.size(); i++) {
       if (((Map)data.get(i)).get("contract_term_count") != null) {
         allCount = Integer.valueOf(allCount.intValue() + Integer.parseInt(((Map)data.get(i)).get("contract_term_count").toString()));
       }
     } 
     for (int i = 0; i < data.size(); i++) {
       if (((Map)data.get(i)).get("contract_term_count") != null) {
 
         
         String countFormat = paseCount(Integer.parseInt(((Map)data.get(i)).get("contract_term_count").toString()), allCount.intValue());
         if ("4".equals(((Map)data.get(i)).get("contract_term"))) {
           resultData.put("one", countFormat);
         } else if ("6".equals(((Map)data.get(i)).get("contract_term"))) {
           resultData.put("two", countFormat);
         } else if ("8".equals(((Map)data.get(i)).get("contract_term"))) {
           resultData.put("three", countFormat);
         } 
       } 
     } 
     return resultData;
   }
   
   private Map<String, Object> lengthServiceSelect(FilterData filterData) {
     List<Map<String, Date>> data = this.adidasDashboardDao.lengthServiceSelect(filterData);
     Map<String, Object> resultData = new HashMap<String, Object>();
     List<String> resultList = new ArrayList<String>();
     if (data == null || data.size() == 0) {
       return resultData;
     }
     int allCount = data.size();
     Map<String, Integer> timeData = new HashMap<String, Integer>();
     timeData.put("1", Integer.valueOf(0));
     timeData.put("2", Integer.valueOf(0));
     timeData.put("3", Integer.valueOf(0));
     timeData.put("4", Integer.valueOf(0));
     timeData.put("5", Integer.valueOf(0));
     timeData.put("6", Integer.valueOf(0));
     timeData.put("7", Integer.valueOf(0));
     int allMonth = 0;
     for (int i = 0; i < data.size(); i++) {
       int differMonth = differMonth((Date)((Map)data.get(i)).get("entry_date"), filterData.getBeginDate());
       allMonth += differMonth;
       if (differMonth <= 6) {
         timeData.put("1", Integer.valueOf(((Integer)timeData.get("1")).intValue() + 1));
       } else if (differMonth <= 12) {
         timeData.put("2", Integer.valueOf(((Integer)timeData.get("2")).intValue() + 1));
       } else if (differMonth <= 24) {
         timeData.put("3", Integer.valueOf(((Integer)timeData.get("3")).intValue() + 1));
       } else if (differMonth <= 36) {
         timeData.put("4", Integer.valueOf(((Integer)timeData.get("4")).intValue() + 1));
       } else if (differMonth <= 60) {
         timeData.put("5", Integer.valueOf(((Integer)timeData.get("5")).intValue() + 1));
       } else if (differMonth <= 120) {
         timeData.put("6", Integer.valueOf(((Integer)timeData.get("6")).intValue() + 1));
       } else {
         timeData.put("7", Integer.valueOf(((Integer)timeData.get("7")).intValue() + 1));
       } 
     } 
     resultList.add(paseCount(((Integer)timeData.get("1")).intValue(), allCount));
     resultList.add(paseCount(((Integer)timeData.get("2")).intValue(), allCount));
     resultList.add(paseCount(((Integer)timeData.get("3")).intValue(), allCount));
     resultList.add(paseCount(((Integer)timeData.get("4")).intValue(), allCount));
     resultList.add(paseCount(((Integer)timeData.get("5")).intValue(), allCount));
     resultList.add(paseCount(((Integer)timeData.get("6")).intValue(), allCount));
     resultList.add(paseCount(((Integer)timeData.get("7")).intValue(), allCount));
     resultData.put("list", resultList);
     resultData.put("avg", paseCount(allMonth / data.size(), 12));
     return resultData;
   }
   
   private Map<String, Object> distriButtonSelect(FilterData filterData) {
     List<Map<String, Object>> data = this.adidasDashboardDao.distriButtonSelect(filterData);
     Map<String, Object> resultData = new HashMap<String, Object>();
     resultData.put("boy", Integer.valueOf(0));
     resultData.put("girl", Integer.valueOf(0));
     resultData.put("boyIntern", Integer.valueOf(0));
     resultData.put("boyFTE", Integer.valueOf(0));
     resultData.put("girlIntern", Integer.valueOf(0));
     resultData.put("girlFTE", Integer.valueOf(0));
     if (data == null || data.size() == 0) {
       return resultData;
     }
     int boyCount = 0, girlCount = 0, boyInternCount = 0;
     int boyFTECount = 0, girlInternCount = 0, girlFTECount = 0;
     for (int i = 0; i < data.size(); i++) {
       String type = ((Map)data.get(i)).get("type").toString();
       Long emp_count = Long.valueOf(((Long)((Map)data.get(i)).get("emp_count")).longValue());
       int countValue = emp_count.intValue();
       if ("boy".equals(type)) {
         boyCount = countValue;
       } else if ("girl".equals(type)) {
         girlCount = countValue;
       } else if ("boyIntern".equals(type)) {
         boyInternCount = countValue;
       } else if ("boyFTE".equals(type)) {
         boyFTECount = countValue;
       } else if ("girlIntern".equals(type)) {
         girlInternCount = countValue;
       } else if ("girlFTE".equals(type)) {
         girlFTECount = countValue;
       } 
     } 
     if (boyCount + girlCount == 0) {
       return resultData;
     }
     resultData.put("boy", paseCount(boyCount, boyCount + girlCount));
     resultData.put("girl", paseCount(girlCount, boyCount + girlCount));
     if (boyCount == 0) {
       resultData.put("boyIntern", Integer.valueOf(0));
       resultData.put("boyFTE", Integer.valueOf(0));
     } else {
       resultData.put("boyIntern", paseCount(boyInternCount, boyCount));
       resultData.put("boyFTE", paseCount(boyFTECount, boyCount));
     } 
     if (girlCount == 0) {
       resultData.put("girlIntern", Integer.valueOf(0));
       resultData.put("girlFTE", Integer.valueOf(0));
     } else {
       resultData.put("girlIntern", paseCount(girlInternCount, girlCount));
       resultData.put("girlFTE", paseCount(girlFTECount, girlCount));
     } 
     return resultData;
   }
   
   private Map<String, Object> ageSelect(FilterData filterData) {
     List<Map<String, Object>> boyData = this.adidasDashboardDao.ageBoySelect(filterData);
     List<Map<String, Object>> girlData = this.adidasDashboardDao.ageGirlSelect(filterData);
     int boyAllCount = (boyData == null) ? 0 : boyData.size();
     int girlAllCount = (girlData == null) ? 0 : girlData.size();
     int boyOne = 0, boyTwo = 0, boyThree = 0, boyFour = 0, boyFive = 0, boySix = 0;
     int girlOne = 0, girlTwo = 0, girlThree = 0, girlFour = 0, girlFive = 0, girlSix = 0;
     if (boyData != null && boyData.size() != 0) {
       for (int i = 0; i < boyData.size(); i++) {
         int age = Integer.parseInt(((Map)boyData.get(i)).get("how_old").toString());
         if (age <= 25) {
           boyOne++;
         } else if (age <= 30) {
           boyTwo++;
         } else if (age <= 40) {
           boyThree++;
         } else if (age <= 50) {
           boyFour++;
         } else if (age <= 60) {
           boyFive++;
         } else {
           boySix++;
         } 
       } 
     }
     if (girlData != null && girlData.size() != 0) {
       for (int i = 0; i < girlData.size(); i++) {
         int age = Integer.parseInt(((Map)girlData.get(i)).get("how_old").toString());
         if (age <= 25) {
           girlOne++;
         } else if (age <= 30) {
           girlTwo++;
         } else if (age <= 40) {
           girlThree++;
         } else if (age <= 50) {
           girlFour++;
         } else if (age <= 60) {
           girlFive++;
         } else {
           girlSix++;
         } 
       } 
     }
     Map<String, Object> resultData = new HashMap<String, Object>();
     List<String> boyChartData = new ArrayList<String>();
     List<String> girlChartData = new ArrayList<String>();
     if (boyAllCount == 0) {
       for (int i = 0; i < 6; i++) {
         boyChartData.add("0");
       }
     } else {
       boyChartData.add(paseCount(boyOne, boyAllCount));
       boyChartData.add(paseCount(boyTwo, boyAllCount));
       boyChartData.add(paseCount(boyThree, boyAllCount));
       boyChartData.add(paseCount(boyFour, boyAllCount));
       boyChartData.add(paseCount(boyFive, boyAllCount));
       boyChartData.add(paseCount(boySix, boyAllCount));
     } 
     if (girlAllCount == 0) {
       for (int i = 0; i < 6; i++) {
         girlChartData.add("0");
       }
     } else {
       girlChartData.add(paseCount(0 - girlOne, girlAllCount));
       girlChartData.add(paseCount(0 - girlTwo, girlAllCount));
       girlChartData.add(paseCount(0 - girlThree, girlAllCount));
       girlChartData.add(paseCount(0 - girlFour, girlAllCount));
       girlChartData.add(paseCount(0 - girlFive, girlAllCount));
       girlChartData.add(paseCount(0 - girlSix, girlAllCount));
     } 
     resultData.put("boy", boyChartData);
     resultData.put("girl", girlChartData);
     return resultData;
   }
   
   private Map<String, Object> workYearSelect(FilterData filterData) {
     List<Map<String, Date>> boyData = this.adidasDashboardDao.workYearBoySelect(filterData);
     List<Map<String, Date>> girlData = this.adidasDashboardDao.workYearGirlSelect(filterData);
     int boyAllCount = (boyData == null) ? 0 : boyData.size();
     int girlAllCount = (girlData == null) ? 0 : girlData.size();
     int boyOne = 0, boyTwo = 0, boyThree = 0, boyFour = 0, boyFive = 0, boySix = 0, boySeven = 0;
     int girlOne = 0, girlTwo = 0, girlThree = 0, girlFour = 0, girlFive = 0, girlSix = 0, girlSeven = 0;
     if (boyData != null && boyData.size() != 0) {
       for (int i = 0; i < boyData.size(); i++) {
         int differMonth = differMonth((Date)((Map)boyData.get(i)).get("entry_date"), filterData.getBeginDate());
         if (differMonth <= 6) {
           boyOne++;
         } else if (differMonth <= 12) {
           boyTwo++;
         } else if (differMonth <= 24) {
           boyThree++;
         } else if (differMonth <= 36) {
           boyFour++;
         } else if (differMonth <= 60) {
           boyFive++;
         } else if (differMonth <= 120) {
           boySix++;
         } else {
           boySeven++;
         } 
       } 
     }
     if (girlData != null && girlData.size() != 0) {
       for (int i = 0; i < girlData.size(); i++) {
         int differMonth = differMonth((Date)((Map)girlData.get(i)).get("entry_date"), filterData.getBeginDate());
         if (differMonth <= 6) {
           girlOne++;
         } else if (differMonth <= 12) {
           girlTwo++;
         } else if (differMonth <= 24) {
           girlThree++;
         } else if (differMonth <= 36) {
           girlFour++;
         } else if (differMonth <= 60) {
           girlFive++;
         } else if (differMonth <= 120) {
           girlSix++;
         } else {
           girlSeven++;
         } 
       } 
     }
     Map<String, Object> resultData = new HashMap<String, Object>();
     List<String> boyChartData = new ArrayList<String>();
     List<String> girlChartData = new ArrayList<String>();
     if (boyAllCount == 0) {
       for (int i = 0; i < 7; i++) {
         boyChartData.add("0");
       }
     } else {
       boyChartData.add(paseCount(boyOne, boyAllCount));
       boyChartData.add(paseCount(boyTwo, boyAllCount));
       boyChartData.add(paseCount(boyThree, boyAllCount));
       boyChartData.add(paseCount(boyFour, boyAllCount));
       boyChartData.add(paseCount(boyFive, boyAllCount));
       boyChartData.add(paseCount(boySix, boyAllCount));
       boyChartData.add(paseCount(boySeven, boyAllCount));
     } 
     if (girlAllCount == 0) {
       for (int i = 0; i < 7; i++) {
         girlChartData.add("0");
       }
     } else {
       girlChartData.add(paseCount(0 - girlOne, girlAllCount));
       girlChartData.add(paseCount(0 - girlTwo, girlAllCount));
       girlChartData.add(paseCount(0 - girlThree, girlAllCount));
       girlChartData.add(paseCount(0 - girlFour, girlAllCount));
       girlChartData.add(paseCount(0 - girlFive, girlAllCount));
       girlChartData.add(paseCount(0 - girlSix, girlAllCount));
       girlChartData.add(paseCount(0 - girlSeven, girlAllCount));
     } 
     resultData.put("boy", boyChartData);
     resultData.put("girl", girlChartData);
     return resultData;
   }
   private FilterData getFilterData(FilterRequestModel filter) {
     Map<String, FilterDescribe> filters = filter.getFilters();
     Date period = new Date(((Long)((FilterDescribe)filters.get("period")).getTo()).longValue());
     Date beginDate = getLastDayOfMonth(period);
     Date endDate = getNextMonth(period);
     List<String> region = null, store_type = null, employee_type = null, store = null, grade = null;
     if (filters.get("region") != null && ((FilterDescribe)filters.get("region")).getValue() != null) {
       region = (List)((FilterDescribe)filters.get("region")).getValue();
     }
     if (filters.get("store_type") != null && ((FilterDescribe)filters.get("store_type")).getValue() != null) {
       store_type = (List)((FilterDescribe)filters.get("store_type")).getValue();
     }
     if (filters.get("employee_type") != null && ((FilterDescribe)filters.get("employee_type")).getValue() != null) {
       employee_type = (List)((FilterDescribe)filters.get("employee_type")).getValue();
     }
     if (filters.get("store") != null && ((FilterDescribe)filters.get("store")).getValue() != null) {
       store = (List)((FilterDescribe)filters.get("store")).getValue();
     }
     if (filters.get("grade") != null && ((FilterDescribe)filters.get("grade")).getValue() != null) {
       grade = (List)((FilterDescribe)filters.get("grade")).getValue();
     }
     FilterData filterData = new FilterData();
     filterData.setBeginDate(beginDate);
     filterData.setEndDate(endDate);
     filterData.setRegion(region);
     filterData.setStore_type(store_type);
     filterData.setEmployee_type(employee_type);
     filterData.setStore(store);
     filterData.setGrade(grade);
     return filterData;
   }
   private String paseCount(int one, int allCount) {
     double count = one / allCount * 100.0D;
     if (count == 0.0D) {
       return "0";
     }
     String format = this.df.format(count);
     int indexOf = format.indexOf(".");
     if (indexOf != -1 && ".00".equals(format.substring(indexOf, format.length()))) {
       return format.substring(0, indexOf);
     }
     return format;
   }
   private String paseCount(double one, int allCount) {
     double count = one / allCount;
     if (count == 0.0D) {
       return "0";
     }
     String format = this.df.format(count);
     int indexOf = format.indexOf(".");
     if (indexOf != -1 && ".00".equals(format.substring(indexOf, format.length()))) {
       return format.substring(0, indexOf);
     }
     return format;
   }
   private int differMonth(Date begin, Date end) {
     Calendar bef = Calendar.getInstance();
     Calendar aft = Calendar.getInstance();
     bef.setTime(begin);
     aft.setTime(end);
     int result = aft.get(2) - bef.get(2);
     int month = (aft.get(1) - bef.get(1)) * 12;
     return month + result;
   }
 }


