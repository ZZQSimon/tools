package cn.com.easyerp.DeployTool.dao;

import org.apache.ibatis.annotations.Param;

import cn.com.easyerp.DeployTool.sqlInterceptor.DeploySql;

public interface DeploySqlDao {
  void deploySql(@Param("deploySql") DeploySql paramDeploySql);
}


