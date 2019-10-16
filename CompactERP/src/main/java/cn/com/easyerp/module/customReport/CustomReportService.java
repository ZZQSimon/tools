package cn.com.easyerp.module.customReport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.module.dao.CustomReportDao;

@Service
public class CustomReportService {
    // private static final String EMP_CHANGE_TABLE_NAME = "t_employee_change";
    private static final String Period = "Period";
    // private static final String Region = "Region";
    // private static final String StoreType = "Store Type";
    // private static final String EmployeeType = "Employee Type";
    // private static final String Grade = "Grade";
    // private static final String LeavingType = "Leaving Type";
    // private static final String Store = "Store";
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
    @Autowired
    private CustomReportDao customReportDao;
    // @Autowired
    // private PrintExcelService printExcelService;
    @Autowired
    public GridService gridService;

    public List<CustomData> search(CustomReportRequestModel request, List<String> allMonth) {
        String changeCondition = buildChangeCondition(request.getFilter());
        String countCondition = buildCountCondition(request.getFilter());
        List<SumDescribe> sumCondition = buildSumCondition(request.getFilter());
        String group = buildGroup(request.getFilter());
        String leavingType = buildLeavingType(request.getFilter());
        String timeBetween = buildBetweenTime(request.getFilter());
        boolean isEmpType = buildIsEmpType(request.getFilter());
        List<CustomData> customDataDescribes = this.customReportDao.selectData(changeCondition, countCondition,
                sumCondition, group, leavingType, timeBetween, isEmpType);
        List<CustomData> customDatas = formatData(customDataDescribes, allMonth);
        Collections.sort(allMonth);
        return customDatas;
    }

    private List<CustomData> formatData(List<CustomData> datas, List<String> allMonth) {
        Map<String, CustomData> monthData = new HashMap<String, CustomData>();
        for (CustomData data : datas) {
            if (!allMonth.contains(data.getMonth()))
                allMonth.add(data.getMonth());
            String key = ((data.getRegion() == null) ? "" : data.getRegion()) + "-";
            key = key + ((data.getStore_Type() == null) ? "" : data.getStore_Type()) + "-";
            key = key + ((data.getGrade() == null) ? "" : data.getGrade()) + "-";
            key = key + ((data.getEmployeeType() == null) ? "" : data.getEmployeeType()) + "-";
            key = key + ((data.getLeavingType() == null) ? "" : data.getLeavingType()) + "-";
            key = key + ((data.getStore() == null) ? "" : data.getStore()) + "-";
            key = key + ((data.getMonth() == null) ? "" : data.getMonth()) + "-";
            monthData.put(key, data);
        }
        List<CustomData> customDatas = new ArrayList<CustomData>();
        for (CustomData data : datas) {
            boolean isInCustomData = false;
            for (CustomData customData : customDatas) {
                if ((data.getRegion() == null) ? (customData.getRegion() != null) :

                        !data.getRegion().equals(customData.getRegion())) {
                    continue;
                }
                if ((data.getStore_Type() == null) ? (customData.getStore_Type() != null) :

                        !data.getStore_Type().equals(customData.getStore_Type())) {
                    continue;
                }
                if ((data.getGrade() == null) ? (customData.getGrade() != null) :

                        !data.getGrade().equals(customData.getGrade())) {
                    continue;
                }
                if ((data.getEmployeeType() == null) ? (customData.getEmployeeType() != null) :

                        !data.getEmployeeType().equals(customData.getEmployeeType())) {
                    continue;
                }
                if ((data.getLeavingType() == null) ? (customData.getLeavingType() != null) :

                        !data.getLeavingType().equals(customData.getLeavingType())) {
                    continue;
                }
                if ((data.getStore() == null) ? (customData.getStore() != null) :

                        !data.getStore().equals(customData.getStore())) {
                    continue;
                }
                if ((data.getSum_grade() == null) ? (customData.getSum_grade() != null) :

                        !data.getSum_grade().equals(customData.getSum_grade())) {
                    continue;
                }
                isInCustomData = true;
                CustomDataDescribe oneMonthData = new CustomDataDescribe();
                oneMonthData.setMonth(data.getMonth());
                oneMonthData.setExit(data.getExit());
                oneMonthData.setMonthEndHC(data.getMonthEndHC());
                oneMonthData.setBeginningHC(beginningHC(data, monthData));

                int countPeople = oneMonthData.getBeginningHC() + oneMonthData.getMonthEndHC();
                if (countPeople == 0) {
                    oneMonthData.setAttritionRate(Double.valueOf(0.0D));
                } else {
                    oneMonthData.setAttritionRate(Double.valueOf(oneMonthData.getExit() / countPeople));
                }
                if (customData.getCustomDataDescribes() == null) {
                    customData.setCustomDataDescribes(new HashMap<>());
                }
                customData.getCustomDataDescribes().put(data.getMonth(), oneMonthData);
            }

            if (!isInCustomData) {
                customDatas.add(data);
                CustomDataDescribe oneMonthData = new CustomDataDescribe();
                oneMonthData.setMonth(data.getMonth());
                oneMonthData.setExit(data.getExit());
                oneMonthData.setMonthEndHC(data.getMonthEndHC());
                oneMonthData.setBeginningHC(beginningHC(data, monthData));

                int countPeople = oneMonthData.getBeginningHC() + oneMonthData.getMonthEndHC();
                if (countPeople == 0) {
                    oneMonthData.setAttritionRate(Double.valueOf(0.0D));
                } else {
                    oneMonthData.setAttritionRate(Double.valueOf(oneMonthData.getExit() / countPeople));
                }
                if (data.getCustomDataDescribes() == null) {
                    data.setCustomDataDescribes(new HashMap<>());
                }
                data.getCustomDataDescribes().put(data.getMonth(), oneMonthData);
            }
        }
        return customDatas;
    }

    private int beginningHC(CustomData data, Map<String, CustomData> monthData) {
        String key = ((data.getRegion() == null) ? "" : data.getRegion()) + "-";
        key = key + ((data.getStore_Type() == null) ? "" : data.getStore_Type()) + "-";
        key = key + ((data.getGrade() == null) ? "" : data.getGrade()) + "-";
        key = key + ((data.getEmployeeType() == null) ? "" : data.getEmployeeType()) + "-";
        key = key + ((data.getLeavingType() == null) ? "" : data.getLeavingType()) + "-";
        key = key + ((data.getStore() == null) ? "" : data.getStore()) + "-";
        String month = data.getMonth();
        if (month == null) {
            key = key + "";
        } else {
            try {
                Date dt = sdf.parse(month);
                Calendar rightNow = Calendar.getInstance();
                rightNow.setTime(dt);
                rightNow.add(2, -1);
                Date dt1 = rightNow.getTime();
                String reStr = sdf.format(dt1);
                key = key + reStr + "-";
            } catch (Exception e) {
            }
        }

        return (monthData.get(key) == null) ? 0 : ((CustomData) monthData.get(key)).getMonthEndHC();
    }

    private String buildBetweenTime(FilterRequestModel filter) {
        if (null == filter)
            return null;
        if (null == filter.getFilters() || filter.getFilters().size() == 0)
            return null;
        FilterDescribe period = (FilterDescribe) filter.getFilters().get(Period);
        if (null == period || (period.getFrom() == null && period.getTo() == null)) {
            return null;
        }
        String condition = "";
        if (period.getFrom() == null) {
            Date date = new Date(((Long) period.getTo()).longValue());
            String dateStr = sdf.format(date);
            condition = "t_monthly_staff_count.`month` < '" + dateStr + "'";
        } else if (period.getTo() == null) {
            Date date = new Date(((Long) period.getFrom()).longValue());
            String dateStr = sdf.format(date);
            condition = "t_monthly_staff_count.`month` > '" + dateStr + "'";
        } else {
            Date dateFrom = new Date(((Long) period.getFrom()).longValue());
            String dateFromStr = sdf.format(dateFrom);
            Date dateTo = new Date(((Long) period.getTo()).longValue());
            String dateToStr = sdf.format(dateTo);
            condition = "t_monthly_staff_count.`month` > '" + dateFromStr + "'";
            condition = condition + " AND t_monthly_staff_count.`month` < '" + dateToStr + "'";
        }

        return condition;
    }

    private String buildGroup(FilterRequestModel filter) {
        String condition = "t_monthly_staff_count.`month`";
        if (null == filter)
            return condition;
        if (null == filter.getFilters() || filter.getFilters().size() == 0)
            return condition;
        if (null != filter.getFilters().get("Region")) {
            condition = condition + ", t_monthly_staff_count.`region`";
        }
        if (null != filter.getFilters().get("Store Type")) {
            condition = condition + ", t_monthly_staff_count.`store_type`";
        }

        if (null != filter.getFilters().get("Store")) {
            condition = condition + ", t_monthly_staff_count.`store`";
        }
        return condition;
    }

    private String buildChangeCondition(FilterRequestModel filter) {
        if (null == filter)
            return null;
        if (null == filter.getFilters() || filter.getFilters().size() == 0)
            return null;
        String condition = "";
        if (null != filter.getFilters().get("Region")) {
            condition = condition + "m_department.`shop_area` in(" + conditionInStr(filter, "Region");
        }
        if (null != filter.getFilters().get("Store Type")) {
            condition = condition + "m_department.`type` in(" + conditionInStr(filter, "Store Type");
        }
        if (null != filter.getFilters().get("Employee Type")) {
            condition = condition + "t_emp.`employee_type` in(" + conditionInStr(filter, "Employee Type");
        }
        if (null != filter.getFilters().get("Grade")) {
            condition = condition + "t_emp.`rank` in(" + conditionInStr(filter, "Grade");
        }
        if (null != filter.getFilters().get("Leaving Type")) {
            condition = condition + "t_employee_change.`type` in(" + conditionInStr(filter, "Leaving Type");
        } else {
            condition = condition + "t_employee_change.`type` in('2', '3') and ";
        }
        if (null != filter.getFilters().get("Store")) {
            condition = condition + "t_employee_change.`shop_id` in(" + conditionInStr(filter, "Store");
        }
        if (condition.length() > 5) {
            condition = condition.substring(0, condition.length() - 5);
        }
        return condition;
    }

    private boolean buildIsEmpType(FilterRequestModel filter) {
        if (null == filter)
            return false;
        if (null == filter.getFilters() || filter.getFilters().size() == 0) {
            return false;
        }
        if (null == filter.getFilters().get("Employee Type") && null == filter.getFilters().get("Grade")) {
            return false;
        }
        if (null != filter.getFilters().get("Grade")) {
            return false;
        }
        return true;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<SumDescribe> buildSumCondition(FilterRequestModel filter) {
        List<SumDescribe> condition = new ArrayList<SumDescribe>();
        SumDescribe allCount = new SumDescribe();
        allCount.setGrade("all");
        allCount.setSum(
                "SUM(IFNULL(t_monthly_staff_count.M2_actual, 0) + IFNULL(t_monthly_staff_count.M3_actual, 0) + IFNULL(t_monthly_staff_count.M4_actual, 0) + IFNULL(t_monthly_staff_count.P1_actual, 0) + IFNULL(t_monthly_staff_count.P2_actual, 0) + IFNULL(t_monthly_staff_count.P3_actual, 0) + IFNULL(t_monthly_staff_count.P4_actual, 0) + IFNULL(t_monthly_staff_count.PT_actual, 0))");

        condition.add(allCount);
        if (null == filter)
            return condition;
        if (null == filter.getFilters() || filter.getFilters().size() == 0) {
            return condition;
        }
        if (null == filter.getFilters().get("Employee Type") && null == filter.getFilters().get("Grade")) {
            return condition;
        }

        if (null != filter.getFilters().get("Grade")) {
            condition = new ArrayList<SumDescribe>();
            FilterDescribe filterDescribe = (FilterDescribe) filter.getFilters().get("Grade");
            List<String> values = (List) filterDescribe.getValue();
            for (int i = 0; i < values.size(); i++) {
                SumDescribe PTGrade;
                SumDescribe P4Grade;
                SumDescribe P3Grade;
                SumDescribe P2Grade;
                SumDescribe P1Grade;
                SumDescribe m4Grade;
                SumDescribe m3Grade;
                SumDescribe m2Grade;
                switch ((String) values.get(i)) {
                case "M2":
                    m2Grade = new SumDescribe();
                    m2Grade.setGrade("M2");
                    m2Grade.setSum("SUM(t_monthly_staff_count.M2_actual)");
                    condition.add(m2Grade);
                    break;
                case "M3":
                    m3Grade = new SumDescribe();
                    m3Grade.setGrade("M3");
                    m3Grade.setSum("SUM(t_monthly_staff_count.M3_actual)");
                    condition.add(m3Grade);
                    break;
                case "M4":
                    m4Grade = new SumDescribe();
                    m4Grade.setGrade("M4");
                    m4Grade.setSum("SUM(t_monthly_staff_count.M4_actual)");
                    condition.add(m4Grade);
                    break;
                case "P1":
                    P1Grade = new SumDescribe();
                    P1Grade.setGrade("P1");
                    P1Grade.setSum("SUM(t_monthly_staff_count.P1_actual)");
                    condition.add(P1Grade);
                    break;
                case "P2":
                    P2Grade = new SumDescribe();
                    P2Grade.setGrade("P2");
                    P2Grade.setSum("SUM(t_monthly_staff_count.P2_actual)");
                    condition.add(P2Grade);
                    break;
                case "P3":
                    P3Grade = new SumDescribe();
                    P3Grade.setGrade("P3");
                    P3Grade.setSum("SUM(t_monthly_staff_count.P3_actual)");
                    condition.add(P3Grade);
                    break;
                case "P4":
                    P4Grade = new SumDescribe();
                    P4Grade.setGrade("P4");
                    P4Grade.setSum("SUM(t_monthly_staff_count.P4_actual)");
                    condition.add(P4Grade);
                    break;
                case "PT":
                    PTGrade = new SumDescribe();
                    PTGrade.setGrade("PT");
                    PTGrade.setSum("SUM(t_monthly_staff_count.PT_actual)");
                    condition.add(PTGrade);
                    break;
                }
            }
            return condition;
        }

        FilterDescribe filterDescribe = (FilterDescribe) filter.getFilters().get("Employee Type");
        List<String> values = (List) filterDescribe.getValue();
        if (values.size() == 2) {
            condition = new ArrayList<SumDescribe>();
            SumDescribe FTEType = new SumDescribe();
            FTEType.setGrade("FTE");
            FTEType.setSum(
                    "SUM(IFNULL(t_monthly_staff_count.M2_actual, 0) + IFNULL(t_monthly_staff_count.M3_actual, 0) + IFNULL(t_monthly_staff_count.M4_actual, 0) + IFNULL(t_monthly_staff_count.P1_actual, 0) + IFNULL(t_monthly_staff_count.P2_actual, 0) + IFNULL(t_monthly_staff_count.P3_actual, 0) + IFNULL(t_monthly_staff_count.P4_actual, 0))");

            condition.add(FTEType);
            SumDescribe InternType = new SumDescribe();
            InternType.setGrade("Intern");
            InternType.setSum("SUM(t_monthly_staff_count.PT_actual)");
            condition.add(InternType);
            return condition;
        }
        if ("FTE".equals(values.get(0))) {
            condition = new ArrayList<SumDescribe>();
            SumDescribe FTEType = new SumDescribe();
            FTEType.setGrade("FTE");
            FTEType.setSum(
                    "SUM(t_monthly_staff_count.M2_actual + t_monthly_staff_count.M3_actual + t_monthly_staff_count.M4_actual + t_monthly_staff_count.P1_actual + t_monthly_staff_count.P2_actual + t_monthly_staff_count.P3_actual + t_monthly_staff_count.P4_actual)");

            condition.add(FTEType);
            return condition;
        }
        if ("Intern".equals(values.get(0))) {
            condition = new ArrayList<SumDescribe>();
            SumDescribe InternType = new SumDescribe();
            InternType.setGrade("Intern");
            InternType.setSum("SUM(t_monthly_staff_count.PT_actual)");
            condition.add(InternType);
            return condition;
        }

        return condition;
    }

    private String buildCountCondition(FilterRequestModel filter) {
        if (null == filter)
            return null;
        if (null == filter.getFilters() || filter.getFilters().size() == 0)
            return null;
        String condition = "";
        if (null != filter.getFilters().get("Region")) {
            condition = condition + "t_monthly_staff_count.`region` in(" + conditionInStr(filter, "Region");
        }
        if (null != filter.getFilters().get("Store Type")) {
            condition = condition + "t_monthly_staff_count.`store_type` in(" + conditionInStr(filter, "Store Type");
        }

        if (null != filter.getFilters().get("Store")) {
            condition = condition + "t_monthly_staff_count.`store` in(" + conditionInStr(filter, "Store");
        }
        if (condition.length() > 5) {
            condition = condition.substring(0, condition.length() - 5);
        }
        return condition;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String conditionInStr(FilterRequestModel filter, String type) {
        String condition = "";
        FilterDescribe filterDescribe = (FilterDescribe) filter.getFilters().get(type);
        List<String> values = (List) filterDescribe.getValue();
        for (int i = 0; i < values.size(); i++) {
            condition = condition + "'" + (String) values.get(i) + "',";
        }
        condition = condition.substring(0, condition.length() - 1);
        return condition + ") and ";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String buildLeavingType(FilterRequestModel filter) {
        String condition = "all";
        if (null == filter)
            return null;
        if (null == filter.getFilters() || filter.getFilters().size() == 0)
            return null;
        if (null != filter.getFilters().get("Leaving Type")) {
            List<String> values = (List) ((FilterDescribe) filter.getFilters().get("Leaving Type")).getValue();
            if (values.size() == 2) {
                return condition;
            }
            condition = (String) values.get(0);
        }
        return condition;
    }
}
