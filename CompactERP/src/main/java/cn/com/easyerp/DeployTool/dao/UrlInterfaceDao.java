package cn.com.easyerp.DeployTool.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableCheckRuleDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;

@Repository
public interface UrlInterfaceDao {
  int insertTable(@Param("table") TableDescribe paramTableDescribe);
  
  int insertUrl(@Param("urlInterface") UrlInterfaceDescribe paramUrlInterfaceDescribe);
  
  int updateUrl(@Param("urlInterface") UrlInterfaceDescribe paramUrlInterfaceDescribe);
  
  int deleteUrl(@Param("url_id") String paramString);
  
  int insertColumn(@Param("column") ColumnDescribe paramColumnDescribe);
  
  int updateColumn(@Param("column") ColumnDescribe paramColumnDescribe);
  
  int deleteColumn(@Param("column") ColumnDescribe paramColumnDescribe);
  
  int deleteColumnByUrlId(@Param("url_id") String paramString);
  
  int daleteTableByUrlId(@Param("url_id") String paramString);
  
  int deleteColumnI18NByUrlId(@Param("url_id") String paramString);
  
  int insertUrlCheck(@Param("tableCheckRule") TableCheckRuleDescribe paramTableCheckRuleDescribe);
  
  int updateUrlCheck(@Param("tableCheckRule") TableCheckRuleDescribe paramTableCheckRuleDescribe);
  
  int deleteUrlCheck(@Param("tableCheckRule") TableCheckRuleDescribe paramTableCheckRuleDescribe);
  
  int deleteUrlCheckByUrlId(@Param("url_id") String paramString);
  
  String getUrlIdByUrl(@Param("url") String paramString);
}


