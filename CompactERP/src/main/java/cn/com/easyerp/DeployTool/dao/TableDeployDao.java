package cn.com.easyerp.DeployTool.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.AddTableDeploy;
import cn.com.easyerp.DeployTool.service.AutoGenDeploy;
import cn.com.easyerp.DeployTool.service.ColumnDeploy;
import cn.com.easyerp.DeployTool.service.CustomLayout;
import cn.com.easyerp.DeployTool.service.TriggerDeploy;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.cache.ActionPrerequistieDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.OrderByDescribe;
import cn.com.easyerp.core.cache.TableApproveEventDescribe;
import cn.com.easyerp.core.cache.TableCheckRuleDescribe;
import cn.com.easyerp.core.cache.TableRenderModel;

@Repository
public interface TableDeployDao {
  List<Map<String, String>> selectBasicDeploy(@Param("lan") String paramString);
  
  void updateTable(@Param("table") AddTableDeploy paramAddTableDeploy);
  
  void addTable(@Param("table") AddTableDeploy paramAddTableDeploy);
  
  void deleteTable(@Param("table") AddTableDeploy paramAddTableDeploy);
  
  void updateColumn(@Param("column") ColumnDeploy paramColumnDeploy, @Param("tableName") String paramString);
  
  void addColumn(@Param("column") ColumnDeploy paramColumnDeploy, @Param("tableName") String paramString);
  
  void deleteColumn(@Param("columnName") String paramString1, @Param("tableName") String paramString2);
  
  void deleteColumnByTableName(@Param("tableName") String paramString);
  
  void deleteOrderRuleByTableName(@Param("tableName") String paramString);
  
  void deleteCheckRuleByTableName(@Param("tableName") String paramString);
  
  void deleteRendersByTableName(@Param("tableName") String paramString);
  
  void deleteAutoGenByTableName(@Param("tableName") String paramString);
  
  void deleteColumnI18NByTableName(@Param("tableName") String paramString);
  
  void deleteAutoGenI18NByTableName(@Param("tableName") String paramString);
  
  void createDBTable(@Param("tableSQL") String paramString);
  
  void dropDBTable(@Param("tableName") String paramString);
  
  void updateDBTable(@Param("table") AddTableDeploy paramAddTableDeploy);
  
  void deleteOrderRules(@Param("tableName") String paramString);
  
  void addOrderRule(@Param("orderBys") List<OrderByDescribe> paramList, @Param("tableName") String paramString);
  
  void deleteCheckRules(@Param("tableName") String paramString);
  
  void addCheckRule(@Param("checkRules") List<TableCheckRuleDescribe> paramList, @Param("tableName") String paramString);
  
  void deleteRenders(@Param("tableName") String paramString);
  
  void addRenders(@Param("renders") List<TableRenderModel> paramList, @Param("tableName") String paramString);
  
  void deleteAutoGen(@Param("tableName") String paramString);
  
  void addAutoGen(@Param("autoGens") List<AutoGenDeploy> paramList, @Param("tableName") String paramString);
  
  String getId(@Param("tableName") String paramString1, @Param("parent_id") String paramString2);
  
  void updateTrigger(@Param("trigger") TriggerDeploy paramTriggerDeploy);
  
  void addTrigger(@Param("trigger") TriggerDeploy paramTriggerDeploy);
  
  void deleteTrigger(@Param("table_action_id") String paramString);
  
  void updateI18N(@Param("i18n") I18nDescribe paramI18nDescribe);
  
  void addI18N(@Param("i18n") I18nDescribe paramI18nDescribe);
  
  void deleteI18N(@Param("i18n") I18nDescribe paramI18nDescribe);
  
  void deleteCondition(@Param("table_action_id") String paramString);
  
  void addConditions(@Param("conditions") List<ActionPrerequistieDescribe> paramList);
  
  void addCondition(@Param("condition") ActionPrerequistieDescribe paramActionPrerequistieDescribe);
  
  void deleteApi(@Param("table_action_id") String paramString);
  
  void addApi(@Param("api") ApiDescribe paramApiDescribe);
  
  void execSQL(@Param("sql") String paramString);
  
  void deleteTableApproveEvent(@Param("table_id") String paramString);
  
  void saveTableApproveEvents(@Param("tableApproveEvent") TableApproveEventDescribe paramTableApproveEventDescribe);
  
  List<CustomLayout> selectCustomLayout(@Param("table_id") String paramString);
  
  void changePassword(@Param("uuid") String paramString1, @Param("id") String paramString2, @Param("pwd") String paramString3, @Param("domain") String paramString4);
}


