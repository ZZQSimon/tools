package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.FunctionPoint;
import cn.com.easyerp.DeployTool.service.Url;

@Repository
public interface FunctionPointDao {
    List<FunctionPoint> selectFunctionPoint();

    List<FunctionPoint> selectDimFunctionPoint(@Param("params") String paramString);

    void addFunctionPoint(@Param("list") List<FunctionPoint> paramList);

    int countFunctionPoint();

    void updateFunctionPointById(@Param("list") List<FunctionPoint> paramList);

    void deleteFunctionPoint(@Param("list") List<FunctionPoint> paramList);

    List<Url> selectUrl();
}
