package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.chart.ReportModel;

@Repository
public interface ChartReportDao {
  List<ReportModel> getReportsByType(@Param("report_type") int paramInt);
  
  void saveReport(@Param("report") ReportModel paramReportModel);
  
  void updateReport(@Param("report") ReportModel paramReportModel);
  
  void deleteReport(@Param("report") ReportModel paramReportModel);
  
  void deleteI18n(@Param("report") ReportModel paramReportModel);
}


