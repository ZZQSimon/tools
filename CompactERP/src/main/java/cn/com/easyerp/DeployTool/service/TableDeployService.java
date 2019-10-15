 package cn.com.easyerp.DeployTool.service;
 
 import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.DeployTool.view.TableDeployRequestModel;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.cache.ActionPrerequistieDescribe;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.OrderByDescribe;
import cn.com.easyerp.core.cache.TableApproveEventDescribe;
import cn.com.easyerp.core.cache.TableCheckRuleDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TableRenderModel;
 
 @Service
 public class TableDeployService
 {
   public static final String OWNER_KEY = "owner";
   public static final String APPROVE_STATUS_KEY = "approve_status";
   public static final String USER_TABLE_NAME = "m_user";
   
   @Transactional
   public void CRUTable(TableDeployRequestModel request) {
     if (request.getEditData() != null && request.getEditData().size() != 0) {
       for (Map.Entry<String, AddTableDeploy> entry : request.getEditData().entrySet()) {
         checkTableApproveAuthColumn((AddTableDeploy)entry.getValue());
         updateTable((AddTableDeploy)entry.getValue());
         
         saveTableApproveEvent((AddTableDeploy)entry.getValue(), null);
       } 
     }
     if (request.getAddTables() != null && request.getAddTables().size() != 0) {
       for (Map.Entry<String, AddTableDeploy> entry : request.getAddTables().entrySet()) {
         checkTableApproveAuthColumn((AddTableDeploy)entry.getValue());
         createTable((AddTableDeploy)entry.getValue());
         saveTableApproveEvent((AddTableDeploy)entry.getValue(), null);
       } 
     }
     if (request.getDeleteTables() != null && request.getDeleteTables().size() != 0)
       for (Map.Entry<String, AddTableDeploy> entry : request.getDeleteTables().entrySet()) {
         deleteTable((AddTableDeploy)entry.getValue());
         saveTableApproveEvent((AddTableDeploy)entry.getValue(), null);
       }  
   } public static final String APPROVE_STATUS_DICT = "approve_status"; public static final String APPROVE_STATUS_DICT_DEFAULT = "1"; @Autowired
   private TableDeployDao tableDeployDao; @Autowired
   private CacheService cacheService;
   private void saveTableApproveEvent(AddTableDeploy tableDeploy, String isDelete) {
     this.tableDeployDao.deleteTableApproveEvent(tableDeploy.getId());
     if (isDelete == null && 
       tableDeploy.getApproveEvent() != null) {
       for (Map.Entry<String, List<TableApproveEventDescribe>> entry : tableDeploy.getApproveEvent().entrySet()) {
         if (((String)entry.getKey()).equals("pass")) {
           int i = 1;
           for (TableApproveEventDescribe tableApproveEvent : entry.getValue()) {
             tableApproveEvent.setTable_id(tableDeploy.getId());
             tableApproveEvent.setApprove_event_type(0);
             tableApproveEvent.setSeq(i++);
             this.tableDeployDao.saveTableApproveEvents(tableApproveEvent);
           } 
         } 
         if (((String)entry.getKey()).equals("reject")) {
           int i = 1;
           for (TableApproveEventDescribe tableApproveEvent : entry.getValue()) {
             tableApproveEvent.setTable_id(tableDeploy.getId());
             tableApproveEvent.setApprove_event_type(1);
             tableApproveEvent.setSeq(i++);
             this.tableDeployDao.saveTableApproveEvents(tableApproveEvent);
           } 
         } 
       } 
     }
   }
 
   
   private void tableChange(AddTableDeploy tableDeploy) {
     String i18NId = I18NToDB(tableDeploy.getI18n());
     tableDeploy.setInternational_id(i18NId);
     this.tableDeployDao.updateTable(tableDeploy);
     
     updateColumn(tableDeploy);
     if (tableDeploy.getOrderRuleChange() != 0) {
       updateOrderRule(tableDeploy.getOrderBy(), tableDeploy.getId());
     }
     if (tableDeploy.getCheckRuleChange() != 0) {
       updateCheckRule(tableDeploy.getCheckRules(), tableDeploy.getId());
     }
     if (tableDeploy.getRendersChange() != 0) {
       updateRenders(tableDeploy.getRenders(), tableDeploy.getId());
     }
     if (tableDeploy.getAutoGenChange() != 0) {
       updateAutoGen(tableDeploy.getAutoGens(), tableDeploy.getId());
     }
     if (tableDeploy.getTriggers() != null && tableDeploy.getTriggers().size() != 0)
       CRUTrigger(tableDeploy.getTriggers(), tableDeploy.getId(), false); 
     if (tableDeploy.getDeleteTrigger() != null && tableDeploy.getDeleteTrigger().size() != 0) {
       CRUTrigger(tableDeploy.getDeleteTrigger(), tableDeploy.getId(), true);
     }
   }
   
   private void updateTable(AddTableDeploy tableDeploy) { tableChange(tableDeploy); }
 
   
   private void createTable(AddTableDeploy tableDeploy) {
     if (tableDeploy.getId() == null || "".equals(tableDeploy.getId())) {
       return;
     }
     String i18NToDB = I18NToDB(tableDeploy.getI18n());
     
     tableDeploy.getI18n().setInternational_id(i18NToDB);
     tableDeploy.setInternational_id(i18NToDB);
     this.tableDeployDao.addTable(tableDeploy);
     
     if (tableDeploy.getColumns() != null && tableDeploy.getColumns().size() != 0) {
       List<ColumnDeploy> columns = tableDeploy.getColumns();
       for (int i = 0; i < columns.size(); i++) {
         addColumn((ColumnDeploy)columns.get(i), tableDeploy.getId());
       }
     } 
     if (tableDeploy.getOrderRuleChange() != 0) {
       this.tableDeployDao.addOrderRule(tableDeploy.getOrderBy(), tableDeploy.getId());
     }
     if (tableDeploy.getCheckRuleChange() != 0) {
       this.tableDeployDao.addCheckRule(tableDeploy.getCheckRules(), tableDeploy.getId());
     }
     if (tableDeploy.getRendersChange() != 0) {
       this.tableDeployDao.addRenders(tableDeploy.getRenders(), tableDeploy.getId());
     }
     if (tableDeploy.getAutoGenChange() != 0) {
       addAutoGen(tableDeploy.getAutoGens(), tableDeploy.getId());
     }
     if (tableDeploy.getTriggers() != null && tableDeploy.getTriggers().size() != 0) {
       Map<String, TriggerDeploy> triggers = tableDeploy.getTriggers();
       for (Map.Entry<String, TriggerDeploy> entry : triggers.entrySet()) {
         addTrigger((TriggerDeploy)entry.getValue(), tableDeploy.getId());
       }
     } 
     
     if (tableDeploy.getTable_type() == 0 || tableDeploy
       .getTable_type() == 4 || tableDeploy
       .getTable_type() == 5)
       this.tableDeployDao.createDBTable(buildCreateTableSQL(tableDeploy)); 
   }
   private void checkTableApproveAuthColumn(AddTableDeploy tableDeploy) {
     if (tableDeploy == null)
       return; 
     if (tableDeploy.getIs_approve() == 1) {
       if (!tableDeploy.getColumnMap().containsKey("approve_status")) {
         addApproveStatus(tableDeploy);
       }
     } else if (tableDeploy.getIs_auth() == 1) {
       if (!tableDeploy.getColumnMap().containsKey("owner")) {
         addOwner(tableDeploy);
 
 
 
       
       }
 
 
 
     
     }
     else {
 
 
 
 
       
       if (tableDeploy.getColumnMap().containsKey("owner")) {
         if (tableDeploy.getDeleteColumns() == null) {
           tableDeploy.setDeleteColumns(new ArrayList<>());
         }
         tableDeploy.getDeleteColumns().add(tableDeploy.getColumnMap().get("owner"));
         for (int i = 0; i < tableDeploy.getColumns().size(); i++) {
           if ("owner".equals(((ColumnDeploy)tableDeploy.getColumns().get(i)).getColumn_name())) {
             tableDeploy.getColumns().remove(i);
             break;
           } 
         } 
         tableDeploy.getColumnMap().remove("owner");
       } 
       if (tableDeploy.getColumnMap().containsKey("approve_status")) {
         if (tableDeploy.getDeleteColumns() == null) {
           tableDeploy.setDeleteColumns(new ArrayList<>());
         }
         tableDeploy.getDeleteColumns().add(tableDeploy.getColumnMap().get("approve_status"));
         for (int i = 0; i < tableDeploy.getColumns().size(); i++) {
           if ("approve_status".equals(((ColumnDeploy)tableDeploy.getColumns().get(i)).getColumn_name())) {
             tableDeploy.getColumns().remove(i);
             break;
           } 
         } 
         tableDeploy.getColumnMap().remove("approve_status");
       } 
     } 
   }
   private void addOwner(AddTableDeploy tableDeploy) {
     ColumnDeploy column = new ColumnDeploy();
     column.setColumnChange(2);
     column.setTable_id(tableDeploy.getId());
     column.setColumn_name("owner");
     column.setUrl_id("");
     column.setVirtual(false);
     column.setData_type(1);
     column.setMax_len(Integer.valueOf(20));
     column.setIs_condition(Integer.valueOf(0));
     column.setRef_table_name("m_user");
     column.setSum_flag(Integer.valueOf(0));
     column.setRo_insert(true);
     column.setCell_cnt(3);
     column.setSeq(100);
     
     I18nDescribe i18n = new I18nDescribe();
     i18n.setCn("鎷ユ湁鑰�");
     i18n.setEn("owner");
     column.setI18n(i18n);
     tableDeploy.getColumns().add(column);
   }
   private void addApproveStatus(AddTableDeploy tableDeploy) {
     ColumnDeploy column = new ColumnDeploy();
     column.setColumnChange(2);
     column.setTable_id(tableDeploy.getId());
     column.setColumn_name("approve_status");
     column.setUrl_id("");
     column.setVirtual(false);
     column.setData_type(1);
     column.setMax_len(Integer.valueOf(20));
     column.setIs_condition(Integer.valueOf(0));
     column.setDic_id("approve_status");
     column.setDefault_value("1");
     column.setSum_flag(Integer.valueOf(0));
     column.setRo_insert(true);
     column.setCell_cnt(3);
     column.setSeq(0);
     
     I18nDescribe i18n = new I18nDescribe();
     i18n.setCn("瀹℃壒鐘舵��");
     i18n.setEn("approve_status");
     column.setI18n(i18n);
     tableDeploy.getColumns().add(column);
   }
 
   
   private void deleteTable(AddTableDeploy tableDeploy) {
     if (tableDeploy.getId() == null || "".equals(tableDeploy.getId())) {
       return;
     }
     this.tableDeployDao.deleteTable(tableDeploy);
 
     
     this.tableDeployDao.deleteColumnByTableName(tableDeploy.getId());
     
     this.tableDeployDao.deleteOrderRuleByTableName(tableDeploy.getId());
     
     this.tableDeployDao.deleteCheckRuleByTableName(tableDeploy.getId());
     
     this.tableDeployDao.deleteRendersByTableName(tableDeploy.getId());
 
     
     this.tableDeployDao.deleteAutoGenByTableName(tableDeploy.getId());
     
     if (tableDeploy.getTriggers() != null && tableDeploy.getTriggers().size() != 0) {
       for (Map.Entry<String, TriggerDeploy> entry : tableDeploy.getTriggers().entrySet()) {
         deleteTrigger((TriggerDeploy)entry.getValue());
       }
     }
     if (tableDeploy.getDeleteTrigger() != null && tableDeploy.getDeleteTrigger().size() != 0) {
       for (int i = 0; i < tableDeploy.getDeleteTrigger().size(); i++) {
         deleteTrigger((TriggerDeploy)tableDeploy.getDeleteTrigger().get(Integer.valueOf(i)));
       }
     }
     
     if (tableDeploy.getTable_type() == 0 || tableDeploy
       .getTable_type() == 4 || tableDeploy
       .getTable_type() == 5)
       this.tableDeployDao.dropDBTable(tableDeploy.getId()); 
   }
   public String I18NToDB(I18nDescribe i18n) {
     if (i18n == null)
       return null; 
     String I18NId = null;
     if (i18n.getInternational_id() == null || "".equals(i18n.getInternational_id())) {
       String[] uuids = UUID.randomUUID().toString().split("-");
       I18NId = uuids[0].toLowerCase() + uuids[1].toLowerCase();
       i18n.setInternational_id(I18NId);
       this.tableDeployDao.addI18N(i18n);
     } else {
       I18NId = i18n.getInternational_id();
       this.tableDeployDao.updateI18N(i18n);
     } 
     return I18NId;
   }
   private String buildCreateTableSQL(AddTableDeploy tableDeploy) {
     if (tableDeploy.getId() == null || "".equals(tableDeploy.getId()))
       return null; 
     String sql = "CREATE TABLE if not exists `" + tableDeploy.getId() + "` (";
     List<ColumnDeploy> columns = tableDeploy.getColumns();
     List<String> idColumns = new ArrayList<String>();
     for (int i = 0; i < columns.size(); i++) {
       if (((ColumnDeploy)columns.get(i)).getIs_id_column() == 1) {
         idColumns.add(((ColumnDeploy)columns.get(i)).getColumn_name());
       }
       String columnSQL = buildColumnSQL((ColumnDescribe)columns.get(i));
       if (columnSQL != null) {
         sql = sql + columnSQL + ",";
       }
     } 
     if (idColumns.size() == 0) {
       sql = sql.substring(0, sql.length() - 1);
     } else {
       sql = sql + " PRIMARY KEY (";
       for (int i = 0; i < idColumns.size(); i++) {
         sql = sql + "`" + (String)idColumns.get(i) + "`,";
       }
       sql = sql.substring(0, sql.length() - 1);
       sql = sql + ")";
     } 
     return sql + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
   } public String buildColumnSQL(ColumnDescribe column) {
     int MaxLen;
     String data_format;
     if (column.getColumn_name() == null || "".equals(column.getColumn_name()))
       return null; 
     if (column.getIs_encrypt() == 1) {
       return "`" + column.getColumn_name() + "` blob ";
     }
     switch (column.getData_type()) {
       case 6:
         return "`" + column.getColumn_name() + "` blob ";
       case 1:
         return "`" + column.getColumn_name() + "` varchar(" + ((column
           .getMax_len() == null || column.getMax_len().intValue() == 0) ? "20" : column.getMax_len()) + ")";
       case 15:
         return "`" + column.getColumn_name() + "` longtext";
       case 7:
       case 8:
       case 9:
       case 10:
       case 13:
       case 14:
         return "`" + column.getColumn_name() + "` varchar(" + ((column
           .getMax_len() == null || column.getMax_len().intValue() == 0) ? "200" : column.getMax_len()) + ")";
       case 2:
       case 5:
         return "`" + column.getColumn_name() + "` int";
       case 3:
         data_format = (column.getData_format() == null) ? "" : column.getData_format();
         try {
           Integer.parseInt(data_format);
         } catch (Exception e) {
           data_format = "2";
         } 
         MaxLen = (column.getMax_len() == null || column.getMax_len().intValue() == 0) ? 10 : column.getMax_len().intValue();
         return "`" + column.getColumn_name() + "` decimal(" + MaxLen + "," + data_format + ")";
       case 4:
         return "`" + column.getColumn_name() + "` DATE";
       case 11:
         return "`" + column.getColumn_name() + "` TIME";
       case 12:
         return "`" + column.getColumn_name() + "` DATETIME";
     } 
     return null;
   }
 
 
   
   private void addColumn(ColumnDeploy columnDeploy, String tableName) {
     String I18NId = I18NToDB(columnDeploy.getI18n());
     columnDeploy.getI18n().setInternational_id(I18NId);
     columnDeploy.setInternational_id(I18NId);
     this.tableDeployDao.addColumn(columnDeploy, tableName);
   }
   private void deleteColumn(ColumnDeploy columnDeploy, String tableName) {
     this.tableDeployDao.deleteColumn(columnDeploy.getColumn_name(), tableName);
     deleteI18N(columnDeploy.getI18n());
   }
   private void updateColumn(ColumnDeploy columnDeploy, String tableName) {
     String I18NId = I18NToDB(columnDeploy.getI18n());
     columnDeploy.setInternational_id(I18NId);
     columnDeploy.getI18n().setInternational_id(I18NId);
     this.tableDeployDao.updateColumn(columnDeploy, tableName);
   }
   
   private void updateOrderRule(List<OrderByDescribe> orderBy, String tableName) {
     this.tableDeployDao.deleteOrderRules(tableName);
     if (orderBy != null && orderBy.size() > 0)
       this.tableDeployDao.addOrderRule(orderBy, tableName); 
   }
   
   private void updateCheckRule(List<TableCheckRuleDescribe> checkRules, String tableName) {
     this.tableDeployDao.deleteCheckRules(tableName);
     if (checkRules != null && checkRules.size() > 0) {
       for (int i = 0; i < checkRules.size(); i++) {
         if ("".equals(((TableCheckRuleDescribe)checkRules.get(i)).getError_msg_id()) || ((TableCheckRuleDescribe)checkRules.get(i)).getError_msg_id() == null) {
           I18nDescribe checkRulesI18N = ((TableCheckRuleDescribe)checkRules.get(i)).getMsgI18n();
           String[] uuids = UUID.randomUUID().toString().split("-");
           String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
           checkRulesI18N.setInternational_id(uuid);
           ((TableCheckRuleDescribe)checkRules.get(i)).setError_msg_id(uuid);
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
           
           this.tableDeployDao.addI18N(checkRulesI18N);
         } 
       } 
       this.tableDeployDao.addCheckRule(checkRules, tableName);
     } 
   }
   private void updateRenders(List<TableRenderModel> renders, String tableName) {
     this.tableDeployDao.deleteRenders(tableName);
     if (renders != null && renders.size() > 0)
       this.tableDeployDao.addRenders(renders, tableName); 
   }
   
   private void updateAutoGen(Map<String, AutoGenDeploy> autoGens, String tableName) {
     this.tableDeployDao.deleteAutoGen(tableName);
     if (autoGens == null || autoGens.size() == 0)
       return; 
     addAutoGen(autoGens, tableName);
   }
   private void addAutoGen(Map<String, AutoGenDeploy> autoGens, String tableName) {
     List<AutoGenDeploy> autoGenList = new ArrayList<AutoGenDeploy>();
     for (Map.Entry<String, AutoGenDeploy> autoGen : autoGens.entrySet()) {
       String[] uuids = UUID.randomUUID().toString().split("-");
       String autoGenId = uuids[0].toLowerCase() + uuids[1].toLowerCase();
       ((AutoGenDeploy)autoGen.getValue()).setId(autoGenId);
       I18nDescribe autoGenI18N = new I18nDescribe();
       if ("".equals(((AutoGenDeploy)autoGen.getValue()).getInternational_id()) || ((AutoGenDeploy)autoGen.getValue()).getInternational_id() == null) {
         ((AutoGenDeploy)autoGen.getValue()).setInternational_id(autoGenId);
         autoGenI18N.setInternational_id(autoGenId);
         switch (AuthService.getCurrentUser().getLanguage_id()) {
           case "cn":
             autoGenI18N.setCn(((AutoGenDeploy)autoGen.getValue()).getId_I18N());
             break;
           case "en":
             autoGenI18N.setEn(((AutoGenDeploy)autoGen.getValue()).getId_I18N());
             break;
           case "jp":
             autoGenI18N.setJp(((AutoGenDeploy)autoGen.getValue()).getId_I18N());
             break;
           case "other1":
             autoGenI18N.setOther1(((AutoGenDeploy)autoGen.getValue()).getId_I18N());
             break;
           case "other2":
             autoGenI18N.setOther2(((AutoGenDeploy)autoGen.getValue()).getId_I18N());
             break;
           default:
             autoGenI18N.setCn(((AutoGenDeploy)autoGen.getValue()).getId_I18N());
             break;
         } 
         this.tableDeployDao.addI18N(autoGenI18N);
       } else {
         autoGenI18N.setInternational_id(((AutoGenDeploy)autoGen.getValue()).getInternational_id());
       } 
       autoGenList.add(autoGen.getValue());
     } 
     this.tableDeployDao.addAutoGen(autoGenList, tableName);
   }
 
 
 
   
   private void deleteTrigger(TriggerDeploy trigger) {
     if (trigger.getAction_id() == null || "".equals(trigger.getAction_id()))
       return; 
     deleteCondition(trigger.getAction_id());
     deleteApi(trigger.getAction_id());
     this.tableDeployDao.deleteTrigger(trigger.getAction_id());
     deleteI18N(trigger.getAction_name_I18N());
   }
 
 
 
 
 
   
   private String addTrigger(TriggerDeploy trigger, String tableName) {
     String table_action_id = this.tableDeployDao.getId("c_table_action", "");
     trigger.setTable_id(tableName);
     trigger.setAction_id(table_action_id);
     
     if (trigger.getAction_name_I18N() != null) {
       String internation_id = this.tableDeployDao.getId("c_international", "");
       trigger.getAction_name_I18N().setInternational_id(internation_id);
       trigger.setAction_name_international(internation_id);
       addI18N(trigger.getAction_name_I18N());
     } 
     this.tableDeployDao.addTrigger(trigger);
     if (trigger.getCondition() != null && trigger.getCondition().size() != 0) {
       createCondition(trigger.getCondition(), table_action_id);
     }
     if (trigger.getApi() != null && trigger.getApi().size() != 0) {
       createApi(trigger.getApi(), table_action_id);
     }
     
     return table_action_id;
   }
   private void updateTrigger(TriggerDeploy trigger, String tableName) {
     String i18NId = null;
     if (trigger.getAction_name_I18N() != null) {
       i18NId = I18NToDB(trigger.getAction_name_I18N());
       trigger.getAction_name_I18N().setInternational_id(i18NId);
     } 
     trigger.setTable_id(tableName);
     trigger.setAction_name_international(i18NId);
     this.tableDeployDao.updateTrigger(trigger);
     if (trigger.getConditionChange() != 0) {
       if (trigger.getCondition() != null && trigger.getCondition().size() != 0) {
         deleteCondition(trigger.getAction_id());
         createCondition(trigger.getCondition(), trigger.getAction_id());
       } else {
         deleteCondition(trigger.getAction_id());
       } 
     }
     if (trigger.getApiChange() != 0) {
       if (trigger.getApi() != null && trigger.getApi().size() != 0) {
         deleteApi(trigger.getAction_id());
         createApi(trigger.getApi(), trigger.getAction_id());
       } else {
         deleteApi(trigger.getAction_id());
       } 
     }
   }
 
 
 
 
   
   private void CRUTrigger(Map<String, TriggerDeploy> triggers, String tableName, boolean isDelete) {
     if (isDelete) {
       for (Map.Entry<String, TriggerDeploy> entry : triggers.entrySet()) {
         if (((TriggerDeploy)entry.getValue()).getAction_id() != null && 
           !"".equals(((TriggerDeploy)entry.getValue()).getAction_id())) {
           deleteTrigger((TriggerDeploy)entry.getValue());
         }
       } 
     } else {
       for (Map.Entry<String, TriggerDeploy> entry : triggers.entrySet()) {
         TriggerDeploy trigger = (TriggerDeploy)entry.getValue();
         switch (trigger.getButtonActionChange()) {
           case 0:
             break;
           case 1:
             updateTrigger(trigger, tableName);
             break;
           case 2:
             addTrigger(trigger, tableName);
             break;
         } 
       } 
     } 
   }
 
   
   private void createCondition(List<ActionPrerequistieDescribe> condition, String actionId) {
     if (actionId == null || "".equals(actionId))
       return; 
     for (int i = 0; i < condition.size(); i++) {
       ((ActionPrerequistieDescribe)condition.get(i)).setTable_action_id(actionId);
       if ("".equals(((ActionPrerequistieDescribe)condition.get(i)).getViolate_msg_international_id()) || ((ActionPrerequistieDescribe)condition.get(i)).getViolate_msg_international_id() == null) {
         I18nDescribe conditionI18N = ((ActionPrerequistieDescribe)condition.get(i)).getViolate_msg_I18N();
         String[] uuids = UUID.randomUUID().toString().split("-");
         String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
         conditionI18N.setInternational_id(uuid);
         ((ActionPrerequistieDescribe)condition.get(i)).setViolate_msg_international_id(uuid);
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
         
         this.tableDeployDao.addI18N(conditionI18N);
       } 
     } 
     this.tableDeployDao.addConditions(condition);
   }
   private void deleteCondition(String actionId) {
     if (actionId != null)
       this.tableDeployDao.deleteCondition(actionId); 
   }
   private void createApi(List<ApiDescribe> api, String actionId) {
     if (api == null || api.size() == 0)
       return; 
     for (int i = 0; i < api.size(); i++) {
       String c_table_action_event = this.tableDeployDao.getId("c_table_action_event", "");
       ((ApiDescribe)api.get(i)).setEvent_id(c_table_action_event);
       ((ApiDescribe)api.get(i)).setTable_action_id(actionId);
       this.tableDeployDao.addApi((ApiDescribe)api.get(i));
     } 
   }
   
   private void deleteApi(String actionId) { this.tableDeployDao.deleteApi(actionId); }
 
   
   private void deleteI18N(I18nDescribe i18n) {}
 
   
   private void addI18N(I18nDescribe i18n) {
     if (i18n != null)
       this.tableDeployDao.addI18N(i18n); 
   }
   private void updateI18N(I18nDescribe i18n) {
     if (i18n != null)
       this.tableDeployDao.updateI18N(i18n); 
   }
   private void updateColumn(AddTableDeploy tableDeploy) {
     if (tableDeploy == null) {
       return;
     }
     if (tableDeploy.getDeleteColumns() != null && tableDeploy.getDeleteColumns().size() != 0) {
       List<ColumnDeploy> deleteColumns = tableDeploy.getDeleteColumns();
       for (int i = 0; i < deleteColumns.size(); i++) {
         
         deleteColumn((ColumnDeploy)deleteColumns.get(i), tableDeploy.getId());
         
         if (tableDeploy.getTable_type() == 0 || tableDeploy
           .getTable_type() == 4 || tableDeploy
           .getTable_type() == 5) {
           String columnDBSQL = buildDeleteColumnDBSQL((ColumnDeploy)deleteColumns.get(i), tableDeploy.getId());
           if (columnDBSQL != null) {
             this.tableDeployDao.execSQL(columnDBSQL);
           }
         } 
       } 
     } 
     if (tableDeploy.getColumns() != null && tableDeploy.getColumns().size() != 0) {
       List<ColumnDeploy> columns = tableDeploy.getColumns();
       for (int i = 0; i < columns.size(); i++) {
         switch (((ColumnDeploy)columns.get(i)).getColumnChange()) {
 
           
           case 1:
             updateColumn((ColumnDeploy)columns.get(i), tableDeploy.getId());
             
             if (tableDeploy.getTable_type() == 0 || tableDeploy
               .getTable_type() == 4 || tableDeploy
               .getTable_type() == 5) {
               String columnDBSQL = buildColumnChange((ColumnDeploy)columns.get(i), tableDeploy.getId());
               if (columnDBSQL != null) {
                 this.tableDeployDao.execSQL(columnDBSQL);
               }
             } 
             break;
           case 2:
             addColumn((ColumnDeploy)columns.get(i), tableDeploy.getId());
             
             if (tableDeploy.getTable_type() == 0 || tableDeploy
               .getTable_type() == 4 || tableDeploy
               .getTable_type() == 5) {
               String columnDBSQL = buildAddColumnDBSQL((ColumnDeploy)columns.get(i), tableDeploy.getId());
               if (columnDBSQL != null) {
                 this.tableDeployDao.execSQL(columnDBSQL);
               }
             } 
             break;
         } 
       } 
       if (tableDeploy.getTable_type() == 0 || tableDeploy
         .getTable_type() == 4 || tableDeploy
         .getTable_type() == 5) {
         updatePrimaryKey(tableDeploy);
       }
     } 
   }
   
   private void updatePrimaryKey(AddTableDeploy table) {
     if (table == null || table.getColumns() == null || table.getColumns().size() == 0)
       return; 
     TableDescribe tableDesc = this.cacheService.getTableDesc(table.getId());
     String sql = "";
     if (tableDesc.getIdColumns() != null && tableDesc.getIdColumns().length != 0) {
       sql = "alter table " + table.getId() + " drop primary key;";
       try {
         this.tableDeployDao.execSQL(sql);
       } catch (Exception e) {}
     } 
 
     
     List<ColumnDeploy> columns = table.getColumns();
     boolean flag = false;
     List<String> idColumns = new ArrayList<String>();
     for (int i = 0; i < columns.size(); i++) {
       if (((ColumnDeploy)columns.get(i)).getIs_id_column() == 1) {
         idColumns.add(((ColumnDeploy)columns.get(i)).getColumn_name());
       }
     } 
 
 
 
 
     
     if (idColumns.size() > 0) {
       sql = "alter table " + table.getId() + " add primary key(";
       for (int i = 0; i < idColumns.size(); i++) {
         sql = sql + "`" + (String)idColumns.get(i) + "`,";
       }
       sql = sql.substring(0, sql.length() - 1);
       sql = sql + ")";
       this.tableDeployDao.execSQL(sql);
     } 
   }
   private String buildColumnChange(ColumnDeploy column, String tableName) {
     if (column.getOldColumnName() == null || "".equals(column.getOldColumnName())) {
       return buildUpdateColumnDBSQL(column, tableName);
     }
     
     this.tableDeployDao.deleteColumn(column.getOldColumnName(), tableName);
     this.tableDeployDao.addColumn(column, tableName);
     return buildChangeColumnNameDBSQL(column, tableName);
   }
   
   private String buildAddColumnDBSQL(ColumnDeploy column, String tableName) {
     if (tableName == null || "".equals(tableName) || column
       .getColumn_name() == null || "".equals(column.getColumn_name()))
       return null; 
     String columnSQL = buildColumnSQL(column);
     return "alter table `" + tableName + "` add column " + columnSQL;
   }
   
   private String buildDeleteColumnDBSQL(ColumnDeploy column, String tableName) {
     if (tableName == null || "".equals(tableName) || column
       .getColumn_name() == null || "".equals(column.getColumn_name()))
       return null; 
     return "alter table `" + tableName + "` drop column `" + column.getColumn_name() + "`";
   }
   
   private String buildUpdateColumnDBSQL(ColumnDeploy column, String tableName) {
     String columnSQL = buildColumnSQL(column);
     return "alter table `" + tableName + "` modify column " + columnSQL;
   }
   
   private String buildChangeColumnNameDBSQL(ColumnDeploy column, String tableName) {
     if (column.getOldColumnName() == null || "".equals(column.getOldColumnName()))
       return null; 
     String columnSQL = buildColumnSQL(column);
     return "alter table `" + tableName + "` change column `" + column.getOldColumnName() + "` " + columnSQL;
   }
 }


