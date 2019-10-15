 package cn.com.easyerp.operation;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.api.ApiLog;
import cn.com.easyerp.core.api.ApiResult;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.api.ApiType;
import cn.com.easyerp.core.api.JApiService;
import cn.com.easyerp.core.cache.ActionEventDescribe;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.evaluate.ModelService;
import cn.com.easyerp.core.logger.LogService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
 
@Controller
@RequestMapping({"/operation"})
public class OperationController extends FormViewControllerBase {
   public static final String OPERATION_SUCCESS_MSG = "operation success";
   public static final String OPERATION_FAILED_MSG = "operation failed";
   public static final String OPERATION_LOG_TABLE = "sys_action_log";
   public static final String OPERATION_RECORD_NOT_EXISTS = "operate record not exists";
   private static final String OPERATION_EMPTY_RECORD_ID = "_BLANK_";
   @Autowired
   private DataService dataService;
   @Autowired
   private ApiService apiService;
   
   @RequestMapping({"/dialog.view"})
   public ModelAndView dialog(@RequestBody OperationDialogRequestModel request) {
	 String actionName = request.getMethod();
	 TriggerDescribe trigger = this.dataService.getTrigger(actionName);
	 UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe)this.cacheService.getUrlInterface().get(((ApiDescribe)trigger.getApiMap().get(request.getEvent_id())).getId());
	 String apiTableName = urlInterfaceDescribe.getUrl();
	 TableDescribe table = this.dataService.getTableDesc(apiTableName);
	 DatabaseDataMap data = this.viewService.convertToDBValues(table, request.getParams());
	 OperationFormModel form = new OperationFormModel(ActionType.operate, request.getParent(), trigger, request.getColumn(), this.dataService.buildModel(table, data, false), apiTableName);
	 
	 this.modelService.buildModel(form);
	 form.setSeq(request.getSeq());
	 form.setEvent_id(request.getEvent_id());
	 return buildModelAndView(form); 
   } 
   @Autowired
   private AuthService authService; 
   @Autowired
   private CacheService cache; 
   @Autowired
   private SystemDao systemDao; 
   @Autowired
   private ViewService viewService; 
   @Autowired
   private JApiService jApiService; 
   @Autowired
   private CacheService cacheService; 
   @Autowired
   private ModelService modelService; 
   @Autowired
   private LogService logService;
   
   private Map<String, Object> buildActionLogData(OperationRequestModel request, TableDescribe table, String key, String statusOld, String statusNew) {
     Map<String, Object> ret = new HashMap<String, Object>(8);
     ret.put("table_id", 
         (table.getTable_type() == 1) ? table.getView_main_table() : table.getId());
     ret.put("key_value", key);
     ret.put("status_col", request.getColumn());
     ret.put("status_before", statusOld);
     ret.put("status_now", statusNew);
     return ret;
   }
   
   @Transactional
   @ResponseBody
   @RequestMapping({ "/operate.do" })
   public ActionResult exec(@RequestBody final OperationRequestModel request, final HttpServletRequest httpRequest, final AuthDetails user) {
       final String logId = this.logService.getMaxId();
       this.logService.logTemp(logId, LogService.LogType.operate, AuthService.getCurrentUserId());
       httpRequest.setAttribute("logId", (Object)logId);
       final String operationId = request.getOperationId();
       final TriggerDescribe trigger = this.dataService.getTrigger(operationId);
       httpRequest.setAttribute("data", (Object)operationId);
       final List<String> details = new ArrayList<String>();
       final TableDescribe table = this.dataService.getTableDesc(request.getTable());
       httpRequest.setAttribute("tableId", (Object)table.getId());
       final ApiDescribe api = trigger.getApiMap().get(request.getEvent_id());
       final String uuid = this.apiService.genUuid();
       final Map<String, AuthDetails> ownerCache = new HashMap<String, AuthDetails>();
       final List<Map<String, Object>> execApiParams = new ArrayList<Map<String, Object>>();
       final List<Map<String, Object>> execupdateRecordParams = new ArrayList<Map<String, Object>>();
       final List<Map<String, Object>> execinsertRecordParams = new ArrayList<Map<String, Object>>();
       for (final Map.Entry<String, ViewDataMap> viewEntry : request.getParam().getData().entrySet()) {
           final String entryKey = viewEntry.getKey();
           final Set<String> updateColumns = new HashSet<String>(1);
           final String statusColumn = request.getColumn();
           updateColumns.add(statusColumn);
           final WidgetModelBase widget = ViewService.fetchWidgetModel(entryKey);
           final Map<String, Object> valueMap = new HashMap<String, Object>(table.getIdColumns().length);
           FieldModelBase ownerField = null;
           final Map<String, Object> ids = new HashMap<String, Object>();
           final String[] idColumns = table.getIdColumns();
           if (RecordModel.class.isInstance(widget)) {
               final Map<String, FieldModelBase> map = ((RecordModel)widget).getFieldMap();
               for (final String column : table.getIdColumns()) {
                   final FieldModelBase field = map.get(column);
                   valueMap.put(column, field.getValue());
               }
               ownerField = this.authService.getOwner(map);
               for (int i = 0; i < idColumns.length; ++i) {
                   ids.put(idColumns[i], map.get(idColumns[i]).getValue());
               }
           }
           else {
               final DetailFormModel form = (DetailFormModel)widget;
               for (final String column : table.getIdColumns()) {
                   if (form != null) {
                       valueMap.put(column, form.getField(column).getValue());
                   }
               }
               if (form != null) {
                   ownerField = this.authService.getOwner(form.getFieldMap());
               }
               for (int i = 0; i < idColumns.length; ++i) {
                   ids.put(idColumns[i], form.getField(idColumns[i]).getValue());
               }
           }
           AuthDetails owner = null;
           if (ownerField != null) {
               final String ownerId = (String)ownerField.getValue();
               owner = ownerCache.get(ownerId);
               if (owner == null) {
                   ownerCache.put(ownerId, owner = this.authService.loadUser(ownerId));
               }
           }
           this.authService.optionAuth(user, table.getId(), api.getTable_action_id(), (owner == null) ? null : owner.getId(), ids);
           if (api != null) {
               final DatabaseDataMap data = this.apiService.buildParams(api, request.getParam(), entryKey);
               final Map<String, Object> apiParam = new HashMap<String, Object>();
               apiParam.put("api", api);
               DataMap apiData = data;
               if (Common.isViewDataMap((DataMap)data)) {
                   apiData = this.viewService.convertToDBValues(this.dataService.getTableDesc(api.getId()), (ViewDataMap)apiData);
               }
               apiParam.put("data", apiData);
               apiParam.put("uuid", uuid);
               execApiParams.add(apiParam);
           }
           final TableDescribe opTable = (table.getTable_type() == 1) ? this.dataService.getTableDesc(table.getView_main_table()) : table;
           final Map<String, Object> insertRecordParam = new HashMap<String, Object>();
           final Map<String, Object> map2 = this.buildActionLogData(request, table, (valueMap.get(opTable.getIdColumns()[0]) == null) ? null : valueMap.get(opTable.getIdColumns()[0]).toString(), null, null);
           insertRecordParam.put("insertRecord", map2);
           execinsertRecordParams.add(insertRecordParam);
       }
       for (int j = 0; j < execupdateRecordParams.size(); ++j) {
           final Map<String, Object> updateRecordParam = this.updateRecordDaramToDbValue(execupdateRecordParams.get(j));
           execupdateRecordParams.set(j, updateRecordParam);
       }
       final TableDescribe logTable = this.cache.getTableDesc("c_action_log");
       for (int k = 0; k < execinsertRecordParams.size(); ++k) {
           final Map<String, Object> insertRecordParam2 = this.insertRecordDaramToDbValue(execinsertRecordParams.get(k));
           final Map<String, Object> insertMap = this.dataService.makeInsertRecordToDb(logTable, insertRecordParam2);
           execinsertRecordParams.set(k, insertMap);
           final List<Map<String, Object>> actionLogParam = new ArrayList<Map<String, Object>>();
           actionLogParam.add(execinsertRecordParams.get(k));
           final List<String> insertSQL = this.insertSQLToDb(actionLogParam);
           this.systemDao.insertBatchRecord(insertSQL);
       }
       final ApiResult apiResult = this.execApi(execApiParams, execupdateRecordParams, execinsertRecordParams, api);
       if (!apiResult.isSuccess()) {
           Common.rollback();
           this.logService.updateLogException(logId, table.getId(), operationId, this.dataService.getMessageText("operation failed", new Object[0]));
           return (ActionResult)new ApiActionResult(apiResult.getResult(), this.dataService.getMessageText("operation failed", new Object[0]), apiResult.getMessages());
       }
       this.logService.updateLogNormal(logId, table.getId(), operationId);
       return (ActionResult)new ApiActionResult(0, this.dataService.getMessageText("operation success", new Object[0]), (List)details, (Object)"success");
   }
   
   @ResponseBody
   @RequestMapping({"/checkOperationAuth.do"})
   public ActionResult checkOperationAuth(@RequestBody OperationRequestModel request, AuthDetails user) {
     TriggerDescribe trigger = this.dataService.getTrigger(request.getOperationId());
     TableDescribe table = this.dataService.getTableDesc(request.getTable());
     for (Map.Entry<String, ViewDataMap> viewEntry : request.getParam().getData().entrySet()) {
       WidgetModelBase widget = ViewService.fetchWidgetModel((String)viewEntry.getKey());
       FieldModelBase ownerField = null;
       Map<String, Object> ids = new HashMap<String, Object>();
       String[] idColumns = table.getIdColumns();
       if (RecordModel.class.isInstance(widget)) {
         Map<String, FieldModelBase> map = ((RecordModel)widget).getFieldMap();
         ownerField = (FieldModelBase)this.authService.getOwner(map);
         for (int i = 0; i < idColumns.length; i++) {
           ids.put(idColumns[i], ((FieldModelBase)map.get(idColumns[i])).getValue());
         }
       } else {
         DetailFormModel form = (DetailFormModel)widget;
         if (form != null) {
           ownerField = (FieldModelBase)this.authService.getOwner(form.getFieldMap());
         }
         for (int i = 0; i < idColumns.length; i++) {
           ids.put(idColumns[i], ((FieldModelBase)form.getFieldMap().get(idColumns[i])).getValue());
         }
       } 
       String ownerId = null;
       if (ownerField != null) {
         ownerId = (String)ownerField.getValue();
       }
       this.authService.optionAuth(user, table.getId(), trigger.getAction_id(), ownerId, ids);
     } 
     return new ActionResult(true);
   }
   
   @ResponseBody
   @RequestMapping({"/outSideInterface.do"})
   public ActionResult outSideInterface(@RequestBody OperationRequestModel request, AuthDetails user) {
     ActionEventDescribe actionEvent = this.cacheService.getActionEvent(request.getEvent_id());
     ApiResult outMsg = this.apiService.doCallApi(actionEvent.getEvent_name(), request.getOutInterfaceParam());
     if (outMsg.isSuccess()) {
       return new ActionResult(true, outMsg.getMessages());
     }
     return new ActionResult(false, outMsg.getMessages());
   }
   
   private Map<String, Object> updateRecordDaramToDbValue(Map<String, Object> updateRecordParam) {
     String tableName = updateRecordParam.get("opTableId").toString();
     Map<String, Object> data = (Map)updateRecordParam.get("valueMap");
     Set<String> updateColumns = (Set)updateRecordParam.get("updateColumns");
     String where = updateRecordParam.get("where").toString();
     TableDescribe table = this.cache.getTableDesc(tableName);
     this.dataService.prepareUpdate(table, data);
     return this.dataService.makeDataToDb(table, data, updateColumns, null, where);
   }
   
   private Map<String, Object> insertRecordDaramToDbValue(Map<String, Object> insertRecordParam) {
     Map<String, Object> data = (Map)insertRecordParam.get("insertRecord");
     TableDescribe table = this.cache.getTableDesc("c_action_log");
     this.dataService.prepareInsert(table, data);
     return data;
   }
   
   public ApiResult execApi(List<Map<String, Object>> execApiParams, List<Map<String, Object>> execupdateRecordParams, List<Map<String, Object>> execinsertRecordParams, ApiDescribe api) {
     if (api != null && api.getType() != ApiType.java) {
       List<String> apiSQLs = apiSQLToDb(execApiParams);
       this.systemDao.callBatchApi(apiSQLs);
     } else {
       List<String> uuids = new ArrayList<String>();
       List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
       for (int i = 0; i < execApiParams.size(); i++) {
         uuids.add(((Map)execApiParams.get(i)).get("uuid").toString());
         data.add((Map)((Map)execApiParams.get(i)).get("data"));
       } 
       String apiName = ((UrlInterfaceDescribe)this.cacheService.getUrlInterface().get(api.getId())).getUrl();
       return this.jApiService.execBatchApi(uuids, apiName, data);
     } 
     List<String> updateSQL = updateSQLToDb(execupdateRecordParams);
     if (updateSQL != null && updateSQL.size() != 0) {
       this.systemDao.updateBatchRecord(updateSQL);
     }
     return apiResult(execApiParams);
   }
   
   private ApiResult apiResult(List<Map<String, Object>> execApiParams) {
     String apiResultSql = apiResultSQLToDb(execApiParams);
     List<ApiLog> logs = this.systemDao.selectBatchApiLogs(apiResultSql);
     List<String> msgs = new ArrayList<String>();
     int ret = 0;
     for (ApiLog log : logs) {
       String name = log.getProc_name();
       String msg = name + " : " + this.dataService.getMessageText(log.getMsg_id(), 
           (Object[])Common.split(log.getMsg_param(), ","));
       if (ret == 0 && name.equals(log.getId()))
         ret = log.getResult(); 
       if (log.getResult() == 1)
         ret = 1; 
       msgs.add(msg);
     } 
     return new ApiResult(ret, msgs);
   }
   
   private List<String> updateSQLToDb(List<Map<String, Object>> execupdateRecordParams) {
     List<String> updateSqls = new ArrayList<String>();
     for (int i = 0; i < execupdateRecordParams.size(); i++) {
       Map<String, Object> paramMap = (Map)execupdateRecordParams.get(i);
       String sql = "update " + paramMap.get("table") + " set ";
       List<ColumnValue> values = (List)paramMap.get("values");
       for (int j = 0; j < values.size(); j++) {
         if (j == values.size() - 1) {
           sql = sql + ((ColumnValue)values.get(j)).getId() + " = '" + ((ColumnValue)values.get(j)).getValue() + "'";
         } else {
           sql = sql + ((ColumnValue)values.get(j)).getId() + " = '" + ((ColumnValue)values.get(j)).getValue() + "',";
         } 
       } 
       sql = sql + " where ";
       List<ColumnValue> keys = (List)paramMap.get("keys");
       for (int j = 0; j < keys.size(); j++) {
         if (j == keys.size() - 1) {
           sql = sql + ((ColumnValue)keys.get(j)).getId() + " = '" + ((ColumnValue)keys.get(j)).getValue() + "' ";
         } else {
           sql = sql + ((ColumnValue)keys.get(j)).getId() + " = '" + ((ColumnValue)keys.get(j)).getValue() + "' and ";
         } 
       } 
       if (paramMap.get("where") != null && !"".equals(paramMap.get("where"))) {
         sql = sql + " and " + paramMap.get("where");
       }
       updateSqls.add(sql);
     } 
     return updateSqls;
   }
   
   private List<String> insertSQLToDb(List<Map<String, Object>> execInsertParams) {
     List<String> insertSqls = new ArrayList<String>();
     for (int i = 0; i < execInsertParams.size(); i++) {
       Map<String, Object> paramMap = (Map)execInsertParams.get(i);
       String sql = " insert into " + paramMap.get("tableName") + " (";
       List<ColumnValue> values = (List)paramMap.get("values");
       for (int j = 0; j < values.size(); j++) {
         if (j == values.size() - 1) {
           sql = sql + ((ColumnValue)values.get(j)).getId();
         } else {
           sql = sql + ((ColumnValue)values.get(j)).getId() + ",";
         } 
       } 
       sql = sql + ") values (";
       for (int j = 0; j < values.size(); j++) {
         if (j == values.size() - 1) {
           sql = sql + "'" + ((ColumnValue)values.get(j)).getValue() + "'";
         } else {
           sql = sql + "'" + ((ColumnValue)values.get(j)).getValue() + "',";
         } 
       } 
       sql = sql + ")";
       insertSqls.add(sql);
     } 
     return insertSqls;
   }
   
   private List<String> apiSQLToDb(List<Map<String, Object>> execApiParams) {
     List<String> apiSqls = new ArrayList<String>();
     for (int i = 0; i < execApiParams.size(); i++) {
       Map<String, Object> paramMap = (Map)execApiParams.get(i);
       
       UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe)this.cacheService.getUrlInterface().get(((ApiDescribe)paramMap.get("api")).getId());
       TableDescribe api = this.dataService.getTableDesc(urlInterfaceDescribe.getUrl());
       String sql = "call  " + urlInterfaceDescribe.getUrl();
       sql = sql + " ('" + paramMap.get("uuid") + "',";
       DataMap dataMap = (DataMap)paramMap.get("data");
       
       List<Object> params = buildParamSeq(api, dataMap);
       if (params == null)
         return null; 
       for (Object param : params) {
         sql = sql + param + ",";
       }
       sql = sql.substring(0, sql.length() - 1);
       sql = sql + ")";
       apiSqls.add(sql);
     }
     return apiSqls;
   }
   
   private String apiResultSQLToDb(List<Map<String, Object>> execApiParams) {
     String sql = "select proc_name, result, msg_id, msg_param from c_api_result";
     sql = sql + " where id in(";
     for (int i = 0; i < execApiParams.size(); i++) {
       sql = sql + "'" + ((Map)execApiParams.get(i)).get("uuid") + "',";
     }
     sql = sql.substring(0, sql.length() - 1);
     return sql + ")";
   }
   
   private List<Object> buildParamSeq(TableDescribe api, DataMap dataMap) {
     if (api == null || api.getColumns() == null || api.getColumns().size() == 0)
       return null; 
     List<Object> param = new ArrayList<Object>();
     for (ColumnDescribe column : api.getColumns()) {
       if (column.getData_type() == 11 || column
         .getData_type() == 4 || column
         .getData_type() == 12) {
    	   param.add("'" + dataMap.get(column.getColumn_name()) + "'"); 
    	   continue;
       } 
       param.add("'" + dataMap.get(column.getColumn_name()) + "'");
     } 
     return param;
   }
}