package cn.com.easyerp.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DatabaseDataMap;

@SuppressWarnings("rawtypes")
@Repository
public interface ChartDao {
    List<ReportModel> selectChart();

    List<DatabaseDataMap> selectEffectedRecord(@Param("table") TableDescribe paramTableDescribe,
            @Param("where") String paramString);

    List<String> getGroup(@Param("report") ReportModel paramReportModel, @Param("where") String paramString);

    List<String> getDateAxis(@Param("report") ReportModel paramReportModel, @Param("start") String paramString1,
            @Param("end") String paramString2, @Param("tmp_table") String paramString3);

    List<Integer> getGroupData(@Param("report") ReportModel paramReportModel, @Param("group") String paramString1,
            @Param("where") String paramString2, @Param("format") String paramString3,
            @Param("start") String paramString4, @Param("end") String paramString5,
            @Param("tmp_table") String paramString6);

    List<Map<String, Object>> getPieData(@Param("report") ReportModel paramReportModel,
            @Param("where") String paramString);

    List<Map<String, Object>> buildRadarIndicator(@Param("report") ReportModel paramReportModel,
            @Param("where") String paramString);

    Map<String, Object> getDateScope(@Param("report") ReportModel paramReportModel, @Param("where") String paramString1,
            @Param("format") String paramString2);

    void createTmpTable(@Param("tmp_table") String paramString);

    void dropTempTable(@Param("tmp_table") String paramString);

    ReportModel getChartById(@Param("report_id") String paramString);

    List<Map<String, Object>> getAxisDataByDate(@Param("report") ReportModel paramReportModel,
            @Param("start") String paramString1, @Param("end") String paramString2,
            @Param("tmp_table") String paramString3, @Param("where") String paramString4,
            @Param("format") String paramString5);

    List<Map<String, Object>> getAxisDataByGroup(@Param("report") ReportModel paramReportModel,
            @Param("tmp_table") String paramString1, @Param("where") String paramString2);
}
