package cn.com.easyerp.chart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.dao.ChartDao;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.framework.common.Common;

@SuppressWarnings("rawtypes")
@Service
public class ChartService {
    @Autowired
    private ChartDao chartDao;

    public Map getChartData(ReportModel report, String where) {
        Map<String, Object> returnData = new HashMap<String, Object>();
        String tmp_table = "`z" + UUID.randomUUID().toString().replaceAll("-", "") + "`";
        this.chartDao.createTmpTable(tmp_table);
        int type = report.getReport_type();
        try {
            if (type == 1 || type == 6 || type == 8) {
                returnData = buildBarData(report, where, tmp_table);
            } else if (type == 2 || type == 5) {
                returnData = buildPieData(report, where, tmp_table);
            } else if (type == 7) {
                returnData = buildRadarData(report, where, tmp_table);
            }
            returnData.put("type", Integer.valueOf(type));
            this.chartDao.dropTempTable(tmp_table);
        } catch (Exception e) {
            e.printStackTrace();
            this.chartDao.dropTempTable(tmp_table);
        }
        return returnData;
    }

    private Map<String, Object> buildRadarData(ReportModel report, String where, String tmp_table) {
        Map<String, Object> chartData = new HashMap<String, Object>();

        List<String> groups = this.chartDao.getGroup(report, where);
        List<DatabaseDataMap> data = new ArrayList<DatabaseDataMap>();
        for (String group : groups) {
            List<Integer> list = this.chartDao.getGroupData(report, group, where, "", "", "", tmp_table);
            DatabaseDataMap temp = new DatabaseDataMap();
            temp.put("name", group);
            temp.put("value", list);
            data.add(temp);
        }
        List<Map<String, Object>> indicator = this.chartDao.buildRadarIndicator(report, where);
        chartData.put("legend", groups);
        chartData.put("series", data);
        chartData.put("indicator", indicator);
        return chartData;
    }

    private Map<String, Object> buildPieData(ReportModel report, String where, String tmp_table) {
        Map<String, Object> chartData = new HashMap<String, Object>();
        List<String> groups = this.chartDao.getGroup(report, where);
        List<Map<String, Object>> data = this.chartDao.getPieData(report, where);
        chartData.put("legend", groups);
        chartData.put("series", data);
        return chartData;
    }

    private Map<String, Object> buildBarData(ReportModel report, String where, String tmp_table) {
        List<String> dateAxis, groups;
        if (Common.isBlank(report.getDate_column()) || Common.isBlank(report.getGroup_column())) {
            return buildBarDataNoxAxis(report, where, tmp_table);
        }
        Map<String, Object> chartData = new HashMap<String, Object>();
        String format = getDateFormat(report.getDate_type());

        String start = "";
        String end = "";

        if (!Common.isBlank(report.getDate_column())) {
            Map<String, Object> dates = this.chartDao.getDateScope(report, where, "%Y-%m-%d");

            start = (String) dates.get("start");
            end = (String) dates.get("end");
            if (start == null || end == null)
                return chartData;
            dateAxis = this.chartDao.getDateAxis(report, start, end, tmp_table);
        } else {
            dateAxis = new ArrayList<String>();
            dateAxis.add("");
        }

        if (!Common.isBlank(report.getGroup_column())) {
            groups = this.chartDao.getGroup(report, where);
        } else {
            groups = dateAxis;
        }

        List<DatabaseDataMap> data = new ArrayList<DatabaseDataMap>();
        for (String group : groups) {
            List<Integer> list = this.chartDao.getGroupData(report, group, where, format, start, end, tmp_table);
            DatabaseDataMap temp = new DatabaseDataMap();
            temp.put("name", group);
            temp.put("type", (report.getReport_type() == 1) ? "line" : "bar");
            temp.put("data", list);
            data.add(temp);
        }
        chartData.put("legend", groups);
        if (report.getGroup_column() != null && !"".equals(report.getGroup_column())) {
            chartData.put("xAxis", dateAxis);
        } else {
            List<String> temp = new ArrayList<String>();
            temp.add("");
            chartData.put("xAxis", temp);
        }

        chartData.put("series", data);

        return chartData;
    }

    private String getDateFormat(int type) {
        String format = "%Y";
        if (type == 1) {
            format = "%Y-%m-%d";
        } else if (type == 2) {
            format = "%Y-%m";
        }
        return format;
    }

    private Map<String, Object> buildBarDataNoxAxis(ReportModel report, String where, String tmp_table) {
        List<Map<String, Object>> AxisData;
        Map<String, Object> chartData = new HashMap<String, Object>();
        String format = getDateFormat(report.getDate_type());

        List<String> Axis = new ArrayList<String>();
        List<Object> data = new ArrayList<Object>();
        String start = "";
        String end = "";
        if (report.getDate_column() != null && !"".equals(report.getDate_column())) {
            Map<String, Object> dates = this.chartDao.getDateScope(report, where, format);

            start = (String) dates.get("start");
            end = (String) dates.get("end");
            if (start == null || end == null)
                return chartData;
            AxisData = this.chartDao.getAxisDataByDate(report, start, end, tmp_table, where, format);
        } else {

            AxisData = this.chartDao.getAxisDataByGroup(report, tmp_table, where);
        }
        String key = "";
        for (Map<String, Object> map : AxisData) {
            key = String.valueOf(map.get("key"));
            if (key != null) {
                Axis.add(key);
                data.add(map.get("value"));
            }
        }

        List<DatabaseDataMap> series = new ArrayList<DatabaseDataMap>();
        DatabaseDataMap serie = new DatabaseDataMap();
        serie.put("name", "");
        serie.put("type", (report.getReport_type() == 1) ? "line" : "bar");
        serie.put("data", data);
        series.add(serie);

        chartData.put("series", series);
        chartData.put("xAxis", Axis);
        return chartData;
    }
}
