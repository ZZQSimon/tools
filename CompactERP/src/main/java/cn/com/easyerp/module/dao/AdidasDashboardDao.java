package cn.com.easyerp.module.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.module.customReport.dashboard.EmployeeType;
import cn.com.easyerp.module.customReport.dashboard.FilterData;

@Repository
public interface AdidasDashboardDao {
  List<EmployeeType> countBycontion(@Param("region") List<String> paramList1, @Param("store_type") List<String> paramList2, @Param("employee_type") List<String> paramList3, @Param("grade") List<String> paramList4, @Param("store") String paramString1, @Param("begin_dates") String paramString2, @Param("end_dates") String paramString3);
  
  List<Map<String, Object>> hcFteSelect(@Param("filterData") FilterData paramFilterData);
  
  List<Map<String, Object>> contractTypeSelect(@Param("filterData") FilterData paramFilterData);
  
  List<Map<String, Date>> lengthServiceSelect(@Param("filterData") FilterData paramFilterData);
  
  List<Map<String, Object>> distriButtonSelect(@Param("filterData") FilterData paramFilterData);
  
  List<Map<String, Object>> ageBoySelect(@Param("filterData") FilterData paramFilterData);
  
  List<Map<String, Object>> ageGirlSelect(@Param("filterData") FilterData paramFilterData);
  
  List<Map<String, Date>> workYearBoySelect(@Param("filterData") FilterData paramFilterData);
  
  List<Map<String, Date>> workYearGirlSelect(@Param("filterData") FilterData paramFilterData);
}


