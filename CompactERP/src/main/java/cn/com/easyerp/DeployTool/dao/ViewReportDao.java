package cn.com.easyerp.DeployTool.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.ViewReportDetails;

@Repository
public interface ViewReportDao {
    List<ViewReportDetails> selectViewReport();

    List<Map<String, Object>> selectTable();

    List<Map<String, Object>> selectReportType();

    List<Map<String, Object>> selectReportDispType();

    List<Map<String, Object>> selectReportFileType();

    boolean addViewReport(@Param("viewReport") ViewReportDetails paramViewReportDetails);

    boolean addInternational(@Param("international") ViewReportDetails paramViewReportDetails);

    boolean deleteViewReport(@Param("id") String paramString);
}
