package cn.com.easyerp.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.module.salaryCal.EmpInfo;
import cn.com.easyerp.module.salaryCal.UserHoliday;
import cn.com.easyerp.module.salaryCal.UserSalary;

@Repository
public interface SalaryCalDao {
    List<EmpInfo> selectEmpIds();

    List<UserHoliday> selectUserHoliday(@Param("user") String paramString);

    List<Map<String, Object>> selectUserAddWork(@Param("user") String paramString);

    Map<String, Double> selectAddWorkRule();

    Map<String, Double> selectSecurityScale();

    void savaUserSalary(@Param("salaries") List<UserSalary> paramList);

    void deleteSalary(@Param("date") String paramString);
}
