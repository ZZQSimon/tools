package cn.com.easyerp.module.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.module.customReport.CustomData;
import cn.com.easyerp.module.customReport.SumDescribe;

@Repository
public interface CustomReportDao {
    List<CustomData> selectData(@Param("changeCondition") String paramString1,
            @Param("countCondition") String paramString2, @Param("sumCondition") List<SumDescribe> paramList,
            @Param("group") String paramString3, @Param("leavingType") String paramString4,
            @Param("timeBetween") String paramString5, @Param("isEmpType") boolean paramBoolean);
}
