package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.ABatch;

@Repository
public interface ABatchDao {
  List<ABatch> selectBatch();
  
  void addBatch(@Param("list") List<ABatch> paramList);
  
  void updBatch(@Param("list") List<ABatch> paramList);
  
  void DeleteBatch(@Param("list") List<ABatch> paramList);
}


