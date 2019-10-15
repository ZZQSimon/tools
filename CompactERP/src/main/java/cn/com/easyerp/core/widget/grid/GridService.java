 package cn.com.easyerp.core.widget.grid;
 
 import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewMapIteratorFieldProcessor;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.approve.ApproveFlow;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TableRenderModel;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.companyCalendar.CalendarEvent;
import cn.com.easyerp.core.companyCalendar.PCCalendarService;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.data.AutoKeyDaoModel;
import cn.com.easyerp.core.data.AutoKeyService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ReferenceModel;
import cn.com.easyerp.core.evaluate.CacheModelService;
import cn.com.easyerp.core.evaluate.FormulaService;
import cn.com.easyerp.core.evaluate.ModelService;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.core.filter.FilterService;
import cn.com.easyerp.core.logger.LogService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.complex.ComplexInputService;
import cn.com.easyerp.core.widget.grid.dt.DataTablePagingDataModel;
import cn.com.easyerp.core.widget.grid.dt.DataTablePagingParameterModel;
import cn.com.easyerp.core.widget.map.MapSelectService;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 @Service
 public class GridService
 {
   private static final String IN_APPROVE = "data (${0}) is in approve";
   public static final String SQL_PARAM_CHECK_MSG = "unlawful parameters";
   @Autowired
   private DataService dataService;
   @Autowired
   private ApiService apiService;
   @Autowired
   private AutoKeyService autoKeyService;
   @Autowired
   private ComplexInputService complexInputService;
   @Autowired
   private AuthService authService;
   @Autowired
   private FilterService filterService;
   @Autowired
   private ViewService viewService;
   
   private void mapParentData(GridModel grid, Map<String, Object> data) {
     if (grid.getParentKey() == null)
       return; 
     TableDescribe table = this.dataService.getTableDesc(grid.getTable());
     TableDescribe parentTable = this.dataService.getTableDesc(table.getParent_id());
     String[] keys = table.getIdColumns();
     String[] parentKeys = parentTable.getIdColumns();
     for (int i = 0; i < parentKeys.length; i++)
       data.put(keys[i], grid.getParentKey().get(parentKeys[i]));  } @Autowired private CacheService cache; @Autowired private MapSelectService mapSelectService; @Autowired private CalendarDao calendarDao; @Autowired private PCCalendarService pcCalendarService; @Autowired private CacheService cacheService; @Autowired
   private ApproveDao approveDao; @Autowired
   private ModelService modelService; @Autowired
   private FormulaService formulaService; @Autowired
   private CacheModelService cacheModelService; @Autowired
   private LogService logService; public RecordModel insert(GridModel grid, Map<String, Object> data) { return insert(grid, data, false); }
 
 
   
   public RecordModel insert(GridModel grid, Map<String, Object> data, boolean viewMap) { return insert(grid, data, viewMap, null); }
 
   
   public RecordModel insert(GridModel grid, Map<String, Object> data, boolean viewMap, Collection<String> needed) {
     if (!viewMap)
       return insert(grid, data, null); 
     ViewMapIteratorFieldProcessor processor = new ViewMapIteratorFieldProcessor();
     DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(data, needed, processor);
     Map<String, FieldModelBase> fields = processor.getFields();
     return insert(grid, databaseDataMap, fields);
   }
   
   public RecordModel insert(GridModel grid, Map<String, Object> data, Map<String, FieldModelBase> fields) {
     String tableName = grid.getTable();
     TableDescribe table = this.dataService.getTableDesc(tableName);
     if (!grid.isInMemory()) {
       throw new ApplicationException("GridService:insert into model that is not inMemory");
     }
     mapParentData(grid, data);
     List<FieldModelBase> recordData = this.dataService.buildModel(table, data, grid.isStrict());
     if (GridInMemoryWithAutoKeyModel.class.isInstance(grid)) {
       GridInMemoryWithAutoKeyModel autoGrid = (GridInMemoryWithAutoKeyModel)grid;
       String lastKey = table.getIdColumns()[table.getIdColumns().length - 1];
       String key = (String)data.get(lastKey);
       if (Common.isBlank(key))
         for (FieldModelBase field : recordData) {
           if (field.getColumn().equals(lastKey)) {
             
             AutoKeyDaoModel output = this.autoKeyService.getIndexData(table, data, Integer.valueOf(autoGrid.getKeyIndex()));
             autoGrid.setKeyIndex(output.getNext_no().intValue());
             field.setValue(output.getNext_id()); break;
           } 
         }  
     } 
     RecordModel record = grid.newRecord(recordData);
     record.setParent(grid.getId());
     
     if (table.getViewStyle() != null && table.getViewStyle().getSeq() != null) {
       FieldModelBase field = (FieldModelBase)record.getFieldMap().get(table.getViewStyle().getSeq());
       field.setValue(Integer.valueOf(grid.getRecords().size() + 1));
     } 
     
     if (fields != null) {
       for (FieldModelBase field : recordData) {
         FieldModelBase orig = (FieldModelBase)fields.get(field.getColumn());
         if (orig == null)
           continue;  orig.copyExtra(field);
       } 
     }
     grid.addRecord(record);
     return record;
   }
 
   
   public RecordModel update(GridModel grid, RecordModel record, Map<String, Object> data) { return update(grid, record, data, false); }
 
 
   
   public RecordModel update(GridModel grid, RecordModel record, Map<String, Object> data, boolean viewMap) { return update(grid, record, data, viewMap, null); }
 
   
   public RecordModel update(GridModel grid, RecordModel record, Map<String, Object> data, boolean viewMap, Collection<String> needed) {
     if (!viewMap)
       return update(grid, record, data, null); 
     ViewMapIteratorFieldProcessor processor = new ViewMapIteratorFieldProcessor();
     DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(data, needed, processor);
     Map<String, FieldModelBase> fields = processor.getFields();
     return update(grid, record, databaseDataMap, fields);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public RecordModel update(GridModel grid, RecordModel record, Map<String, Object> data, Map<String, FieldModelBase> fields) {
	   if (!grid.isInMemory()) {
           throw new ApplicationException("GridService:insert into model that is not inMemory");
       }
       List<FieldModelBase> recordData;
       if (fields == null) {
           recordData = this.dataService.buildModel(grid.getTable(), data);
       }
       else {
           final TableDescribe table = this.dataService.getTableDesc(grid.getTable());
           recordData = new ArrayList<FieldModelBase>(fields.values());
           Collections.sort(recordData, new Comparator<FieldModelBase>() {
               @Override
               public int compare(final FieldModelBase f1, final FieldModelBase f2) {
                   final ColumnDescribe d1 = table.getColumn(f1.getColumn());
                   final ColumnDescribe d2 = table.getColumn(f2.getColumn());
                   return d1.getSeq() - d2.getSeq();
               }
           });
       }
       grid.updateRecord(record, recordData);
       return record;
   }
 
   
   public ActionResult delete(GridModel grid, List<String> recordIds, AuthDetails user, TriggerDescribe requestTriggerr, Map<String, TriggerDescribe> childTriggerRequestParams, HttpServletRequest httpRequest) {
     TableDescribe tableDesc = this.dataService.getTableDesc(grid.getTable());
     httpRequest.setAttribute("tableId", tableDesc.getId());
     if (1 == tableDesc.getIs_approve() && null != recordIds && null != tableDesc.getIdColumns() && 1 == tableDesc.getIdColumns().length) {
       List<Object> ids = new ArrayList<Object>();
       for (String id : recordIds) {
         RecordModel record = (RecordModel)ViewService.fetchWidgetModel(id);
         Object value = ((FieldModelBase)record.getFieldMap().get(tableDesc.getIdColumns()[0])).getValue();
         ids.add(value);
       } 
       
       List<String> noDeleteStatus = new ArrayList<String>();
       noDeleteStatus.add(ApproveFlow.ApproveStatus.PROGRESS);
       noDeleteStatus.add(ApproveFlow.ApproveStatus.REJECT);
       noDeleteStatus.add(ApproveFlow.ApproveStatus.CANCEL);
       noDeleteStatus.add(ApproveFlow.ApproveStatus.HOLD);
       noDeleteStatus.add(ApproveFlow.ApproveStatus.TERMINATION);
       List<Map<String, String>> isApprove = this.approveDao.checkIsApprove(tableDesc.getId(), tableDesc.getIdColumns()[0], ids, "approve_status", noDeleteStatus);
       if (null != isApprove && 0 != isApprove.size()) {
         String msg = "";
         for (Map<String, String> approveData : isApprove) {
           for (Map.Entry<String, String> data_id : approveData.entrySet())
             msg = msg + (String)data_id.getValue() + " "; 
         } 
         String i18nText = this.dataService.i18nText(this.cacheService.getMsgI18n("data (${0}) is in approve"));
         i18nText = i18nText.replace("${0}", msg);
         throw new ApplicationException(i18nText);
       } 
     } 
 
     
     if (grid.isInMemory()) {
       
       TableDescribe table = this.dataService.getTableDesc(grid.getTable());
       String seq = (table.getViewStyle() == null) ? null : table.getViewStyle().getSeq();
       List<Integer> seqList = null;
       if (seq != null)
         seqList = new ArrayList<Integer>(recordIds.size());  Iterator<RecordModel> iter;
       label73: for (iter = grid.getRecords().listIterator(); iter.hasNext(); ) {
         RecordModel record = (RecordModel)iter.next();
         for (String id : recordIds) {
           if (record.getId().equals(id)) {
             if (record.getStatus() == RecordModel.Status.inserted) {
               
               this.cacheModelService.deleteChildRecord(grid.getParent(), grid.getId(), record.getId());
               iter.remove();
               continue label73;
             } 
             this.cacheModelService.deleteChildRecord(grid.getParent(), grid.getId(), record.getId(), true);
             record.setStatus(RecordModel.Status.deleted);
             record.setCalendarEvents(this.viewService.calendarColorData(buildRecordData(record), table));
             
             if (seqList != null) {
               seqList.add(Integer.valueOf(((Integer)((FieldModelBase)record.getFieldMap().get(seq)).getValue()).intValue()));
             }
           } 
         } 
       } 
       if (seq != null) {
         Collections.sort(seqList);
         for (int i = seqList.size(); i > 0; i--) {
           int v = ((Integer)seqList.get(i - 1)).intValue();
           for (RecordModel record : grid.getRecords()) {
             if (record.getStatus() == RecordModel.Status.deleted)
               continue; 
             FieldModelBase field = (FieldModelBase)record.getFieldMap().get(seq);
             int value = ((Integer)field.getValue()).intValue();
             if (value > v) {
               field.setValue(Integer.valueOf(value - i));
               if (record.getStatus() == RecordModel.Status.none)
                 record.setStatus(RecordModel.Status.updated); 
             } 
           } 
         } 
       } 
       return Common.ActionOk;
     } 
     
     List<RecordModel> records = grid.getRecords();
     for (RecordModel record : records) {
       DatabaseDataMap databaseDataMap = buildRecordData(record);
       this.mapSelectService.inspectDelete(tableDesc, databaseDataMap);
     } 
     return deleteRecords(grid.getTable(), recordIds, user, requestTriggerr, childTriggerRequestParams, httpRequest);
   }
 
 
   
   public ActionResult deleteRecords(String tableName, List<String> recordIds, AuthDetails user, TriggerDescribe requestTriggerr, Map<String, TriggerDescribe> childTriggerRequestParams, HttpServletRequest httpRequest) {
     int ret = 0;
     TableDescribe table = this.dataService.getTableDesc(tableName);
     List<String> children = this.dataService.getChildrenTable(tableName);
     
     if (table.getReadonly() == 1) {
       return new ActionResult(false, "parent table readOnly delete false");
     }
     List<String> msgs = new ArrayList<String>();
     String successText = this.dataService.getMessageText("Delete success", new Object[] { this.dataService
           .getLabel(table) });
     
     List<CalendarEvent> deleteCalendarEvents = new ArrayList<CalendarEvent>();
     String[] parentIdColumns = table.getIdColumns();
     for (String id : recordIds) {
       RecordModel record = (RecordModel)ViewService.fetchWidgetModel(id);
       Map<String, FieldModelBase> fieldMap = record.getFieldMap();
       FieldModelBase field = (FieldModelBase)this.authService.getOwner(fieldMap);
       Map<String, Object> ids = new HashMap<String, Object>();
       for (int i = 0; i < parentIdColumns.length; i++) {
         ids.put(parentIdColumns[i], ((FieldModelBase)fieldMap.get(parentIdColumns[i])).getValue());
       }
       if (field != null)
       {
         this.authService.optionAuth(user, table.getId(), table
             .getTrigger(TriggerDescribe.TriggerType.delete).getAction_id(), field
             .getValue(), ids);
       }
 
 
 
       
       List<ColumnValue> keys = new ArrayList<ColumnValue>();
       for (String childName : children) {
         keys.clear();
         TableDescribe child = this.dataService.getTableDesc(childName);
         
         if (child.getReadonly() == 1)
           continue; 
         String[] idColumns = child.getIdColumns();
         for (int i = 0; i < idColumns.length - 1; i++)
           keys.add(new ColumnValue(idColumns[i], record.getValueMap()
                 .get(table.getIdColumns()[i]))); 
         if (keys.size() == 0) {
           throw new ApplicationException("Incorrect id column setting for child table \"" + childName + "\"");
         }
         List<Map<String, Object>> childRecords = this.dataService.selectRecordsByValues(child
             .getId(), null, keys, null);
         for (Map<String, Object> childRecord : childRecords) {
           ApiActionResult result; this.mapSelectService.inspectDelete(child, childRecord);
           if (child.hasComplexColumn()) {
             this.complexInputService.delete(child, childRecord);
           }
           deleteCalendarEvents.addAll(this.viewService.calendarColorData(childRecord, child));
           List<Map<String, Object>> deleteData = new ArrayList<Map<String, Object>>();
           deleteData.add(childRecord);
           if (childTriggerRequestParams == null) {
             result = this.apiService.deleteRecordWithTrigger(child, deleteData, false, null);
           } else {
             result = this.apiService.deleteRecordWithTrigger(child, deleteData, false, (TriggerDescribe)childTriggerRequestParams.get(childName));
           }  msgs.addAll(result.getDetails());
           if (!result.isSuccess()) {
             Common.rollback();
             return new ApiActionResult(result.getResult(), result.getMessage(), msgs);
           } 
           
           if (child.hasComplexColumn())
             this.complexInputService.removeOld(child, this.complexInputService
                 .makeComplexKey(child, childRecord)); 
         } 
       } 
       String pid = null;
       if (table.hasComplexColumn()) {
         pid = this.complexInputService.makeComplexKey(table, record);
         this.complexInputService.delete(table, pid);
       } 
       
       if (table.hasComplexColumn()) {
         this.complexInputService.removeOld(table, pid);
       }
     } 
     List<Map<String, Object>> deleteData = new ArrayList<Map<String, Object>>();
     for (String id : recordIds) {
       RecordModel record = (RecordModel)ViewService.fetchWidgetModel(id);
       List<FieldModelBase> fields = record.getFields();
       Map<String, Object> data = this.apiService.buildData(fields);
       deleteData.add(data);
       
       deleteCalendarEvents.addAll(this.viewService.calendarColorData(data, table));
     } 
     httpRequest.setAttribute("data", Common.toJson(deleteData));
     
     if (deleteCalendarEvents.size() != 0) {
       this.calendarDao.deleteBatchCalendarEventsByTable(deleteCalendarEvents);
     }
     
     ApiActionResult result = this.apiService.deleteRecordWithTrigger(table, deleteData, false, requestTriggerr);
     msgs.addAll(result.getDetails());
     if (!result.isSuccess()) {
       Common.rollback();
       ret = result.getResult();
     } 
 
     
     String deleteFailedMsg = this.dataService.getMessageText("Delete failed", new Object[] { this.dataService.getLabel(table) });
     if (ret == 0) {
       String logId = (String)httpRequest.getAttribute("logId");
       String tableId = (String)httpRequest.getAttribute("tableId");
       String data = (String)httpRequest.getAttribute("data");
       this.logService.updateLogNormal(logId, tableId, data);
     } 
     return new ApiActionResult(ret, (ret == 0) ? successText : deleteFailedMsg, msgs);
   }
   
   public String buildCondition(String tableName, FilterRequestModel filter) {
     if (filter == null)
       return null; 
     String condition = this.filterService.toWhere(filter);
     
     String filterTableName = filter.getTableName();
     if (condition == null || filterTableName.equals(tableName)) {
       return condition;
     }
     TableDescribe table = this.dataService.getTableDesc(tableName);
     TableDescribe filterTable = this.dataService.getTableDesc(filterTableName);
     return table.getIdColumns()[0] + " in (select distinct " + filterTable
       .getIdColumns()[0] + " from " + filterTableName + " where " + condition + ")";
   }
 
   
   private List<ReferenceModel> makeRefModels(TableDescribe table, Collection<String> columnsNeeded) {
     List<ColumnDescribe> columns = table.getColumns();
     List<ReferenceModel> refs = null;
     int suffix = 0;
     for (ColumnDescribe column : columns) {
       if (columnsNeeded != null && !columnsNeeded.contains(column.getColumn_name()))
         continue; 
       if (column.isVirtual())
         continue;  String ref_table_name = column.getRef_table_name();
       if (!Common.isBlank(ref_table_name)) {
         if (refs == null) {
           refs = new ArrayList<ReferenceModel>();
         }
         TableDescribe child = this.cache.getTableDesc(ref_table_name);
         
         if (child.getIdColumns().length > 1) {
           TableDescribe clone_child = null;
           try {
             clone_child = (TableDescribe)child.clone();
           } catch (CloneNotSupportedException e) {
             e.printStackTrace();
           } 
           
           clone_child.addId_column(child.getIdColumns()[child.getIdColumns().length - 1]);
           
           refs.add(new ReferenceModel(column, 
                 String.format("%s_%d", new Object[] { ref_table_name, Integer.valueOf(suffix++) }), clone_child)); continue;
         } 
         refs.add(new ReferenceModel(column, 
               String.format("%s_%d", new Object[] { ref_table_name, Integer.valueOf(suffix++) }), child));
       } 
     } 
     
     return refs;
   }
   private String buildSearch(String tableName, FilterRequestModel filter) {
     if (filter == null)
       return null; 
     String searchVal = null;
     if (filter.getFilters() != null && filter.getFilters().get("search") != null && ((FilterDescribe)filter
       .getFilters().get("search")).getValue() != null)
       searchVal = ((FilterDescribe)filter.getFilters().get("search")).getValue().toString(); 
     TableDescribe table = this.dataService.getTableDesc(tableName);
     String search = "";
     String ref_table_name = "";
     if (searchVal != null && !"".equals(searchVal)) {
       List<ColumnDescribe> columns = table.getColumns();
       for (int i = 1; i < columns.size(); i++) {
         
         if (((ColumnDescribe)columns.get(i)).getDic_id() != null && !"".equals(((ColumnDescribe)columns.get(i)).getDic_id())) {
           String dic_id = ((ColumnDescribe)columns.get(i)).getDic_id();
           String dictKey = getDictKeyByInternationl(this.dataService.getDict(dic_id), searchVal);
           if (dictKey == null) {
             continue;
           }
           search = search + " OR " + tableName + "." + ((ColumnDescribe)columns.get(i)).getColumn_name() + " like binary '%" + dictKey + "%'";
         } 
 
         
         ref_table_name = ((ColumnDescribe)columns.get(i)).getRef_table_name();
         if (ref_table_name == null || "".equals(ref_table_name))
         {
           
           search = search + " OR " + tableName + "." + ((ColumnDescribe)columns.get(i)).getColumn_name() + " like binary '%" + searchVal + "%'";
         }
       } 
       if (" OR".equals(search.substring(0, 3))) {
         search = search.substring(3, search.length());
       }
       List<ReferenceModel> refs = makeRefModels(table, null);
       if (null != refs) {
         for (ReferenceModel referenceModel : refs) {
           if ("".equals(search)) {
             search = search + buildNameExpression(referenceModel) + " like binary '%" + searchVal + "%'";
             continue;
           } 
           search = search + " OR " + buildNameExpression(referenceModel) + " like binary '%" + searchVal + "%'";
         } 
       }
     } 
     
     return "".equals(search) ? null : search;
   }
   private String buildNameExpression(ReferenceModel referenceModel) {
     String nameColumn = referenceModel.getChild().getName_column();
     String nameExpression = referenceModel.getChild().getName_expression_publicity();
     if (nameExpression == null || "".equals(nameExpression)) {
       if (nameColumn == null || "".equals(nameColumn)) {
         return referenceModel.getAlias() + "." + referenceModel.getChild().getIdColumns()[0];
       }
       return referenceModel.getAlias() + "." + referenceModel.getChild().getName_column();
     } 
     
     String regex = "\\$\\{(.*?)\\}|\\#\\{(.*?)\\}";
     Pattern pattern = Pattern.compile(regex);
     Matcher matcher = pattern.matcher(nameExpression);
     while (matcher.find()) {
       String param = matcher.group();
       if (param != null) {
         String name = param.substring(2, param.length() - 1);
         nameExpression = nameExpression.replace(param, "'," + referenceModel.getAlias() + "." + name + ",'");
       } 
     } 
     return "concat('" + nameExpression + "')";
   }
 
   
   private String getDictKeyByInternationl(Map<String, I18nDescribe> dict, String internationl) {
     String language_id = AuthService.getCurrentUser().getLanguage_id();
     for (Map.Entry<String, I18nDescribe> entry : dict.entrySet()) {
       switch (language_id) {
         case "cn":
           if (internationl.equals(((I18nDescribe)entry.getValue()).getCn())) {
             return (String)entry.getKey();
           }
           continue;
         case "en":
           if (internationl.equals(((I18nDescribe)entry.getValue()).getEn())) {
             return (String)entry.getKey();
           }
           continue;
         case "jp":
           if (internationl.equals(((I18nDescribe)entry.getValue()).getJp())) {
             return (String)entry.getKey();
           }
           continue;
         case "other1":
           if (internationl.equals(((I18nDescribe)entry.getValue()).getOther1())) {
             return (String)entry.getKey();
           }
           continue;
         case "other2":
           if (internationl.equals(((I18nDescribe)entry.getValue()).getOther2())) {
             return (String)entry.getKey();
           }
           continue;
       } 
       return null;
     } 
     
     return null;
   }
   public int count(GridModel grid, GridRequestModel param, AuthDetails user) {
     if (grid.isInMemory()) {
       int ret = 0;
       for (RecordModel record : grid.getRecords()) {
         if (record.getStatus() != RecordModel.Status.deleted)
           ret++; 
       }  return ret;
     } 
     String tableName = grid.getTable();
     List<AuthGroup> optionAuthGroup = this.authService.getOptionAuthGroup(tableName, "read");
 
 
 
 
 
 
 
 
     
     String condition = buildCondition(tableName, param.getFilter());
     String search = buildSearch(tableName, param.getFilter());
     if (condition == null) {
       condition = search;
     }
     else if (search != null) {
       condition = condition + " and (" + search + ")";
     } 
     return this.dataService.fetchDataRecordsCount(tableName, grid
         .getParentKey(), condition, optionAuthGroup, null, param.getIsApproveSelect(), param.getApproveSelectParam(), grid.isRefChild(), grid.getRefChildColumn());
   }
 
   
   public List<RecordModel> list(GridModel grid, GridRequestModel param, AuthDetails user) {
     if (grid.isInMemory()) {
       List<RecordModel> ret = new ArrayList<RecordModel>();
       for (RecordModel record : grid.getRecords()) {
         if (record.getStatus() != RecordModel.Status.deleted)
         {
           ret.add(record);
         }
       } 
 
       
       return ret;
     } 
     String tableName = grid.getTable();
     List<AuthGroup> optionAuthGroup = this.authService.getOptionAuthGroup(tableName, "read");
 
 
 
 
 
 
 
 
     
     String condition = buildCondition(tableName, param.getFilter());
     String search = buildSearch(tableName, param.getFilter());
     if (condition == null) {
       if (null != search && !"".equals(search)) {
         condition = "(" + search + ")";
       }
     } else if (search != null) {
       condition = condition + " and (" + search + ")";
     } 
     setRecords(grid, this.dataService.fetchDataRecords(tableName, grid
           .getParentKey(), condition, optionAuthGroup, param.getPaging(), param.getIsApproveSelect(), param.getApproveSelectParam(), grid.isRefChild(), grid.getRefChildColumn()));
     return grid.getRecords();
   }
   
   private List<RecordModel> setRecords(GridModel grid, List<Map<String, Object>> data) {
     List<RecordModel> ret = new ArrayList<RecordModel>();
     for (Map<String, Object> recordData : data) {
       
       RecordModel record = new RecordModel(this.dataService.buildModel(grid.getTable(), recordData));
       ret.add(record);
       record.setParent(grid.getId());
       Object nameExpression = this.dataService.buildNameExpression(this.cacheService.getTableDesc(grid.getTable()), recordData);
       record.setRef____name_Expression((nameExpression == null) ? null : nameExpression.toString());
     } 
     grid.setRecords(ret);
     
     this.modelService.clearListModel(grid.getId());
     this.modelService.buildModel(grid, ret);
     
     TableDescribe table = this.cacheService.getTableDesc(grid.getTable());
     if (table.getRenders() != null && table.getRenders().size() != 0 && ret.size() != 0) {
       for (RecordModel record : ret) {
         for (TableRenderModel render : table.getRenders()) {
           if (render.getLevel() == 1) {
             Object evaluate = this.formulaService.evaluate(grid.getId(), record.getId(), null, render
                 .getFormula(), null);
             if (evaluate instanceof Boolean && ((Boolean)evaluate).booleanValue())
               record.setRowColor(render.getColor()); 
             continue;
           } 
           Object evaluate = this.formulaService.evaluate(grid.getId(), record.getId(), null, render
               .getFormula(), null);
           if (evaluate instanceof Boolean && ((Boolean)evaluate).booleanValue()) {
             record.setColumnColor(render.getColor());
           }
         } 
       } 
     }
     return ret;
   }
   public GridModel buildModel(String tableName, Map<String, Object> parentData, boolean inMemory) {
     GridModel grid;
     if (!inMemory) {
       return new GridModel(tableName, parentData);
     }
     TableDescribe table = this.dataService.getTableDesc(tableName);
     ColumnDescribe lastKeyDesc = this.dataService.getColumnDesc(tableName, table
         .getIdColumns()[table.getIdColumns().length - 1]);
     
     if (lastKeyDesc.getData_type() == 13) {
 
       
       grid = new GridInMemoryWithAutoKeyModel(tableName, parentData, 0);
     } else {
       
       grid = new GridModel(tableName, parentData, true);
     }  if (parentData != null) {
       List<Map<String, Object>> records = this.dataService.fetchDataRecords(tableName, parentData, null, null);
       List<RecordModel> ret = new ArrayList<RecordModel>();
       for (Map<String, Object> recordData : records) {
         
         RecordModel record = new RecordModel(this.dataService.buildModel(grid.getTable(), recordData));
         ret.add(record);
         record.setParent(grid.getId());
 
         
         String refTableData = this.viewService.buildRefTableData(this.cacheService.getTableDesc(grid.getTable()), buildRecordData(record));
         List<CalendarEvent> calendarEvents = this.calendarDao.selectCalendarEventByTable("0000000001", grid.getTable(), refTableData);
         record.setCalendarEvents(calendarEvents);
       } 
       grid.setRecords(ret);
     } 
     
     return grid;
   }
   
   public DatabaseDataMap buildRecordData(RecordModel record) {
     DatabaseDataMap ret = new DatabaseDataMap();
     for (FieldModelBase field : record.getFields()) {
       ret.put(field.getColumn(), field.getValue());
     }
     return ret;
   }
   
   public void save(TableDescribe table, Map<String, FieldModelBase> fieldMap, String type, TriggerDescribe requestTriggerr) {
     switch (type) {
       case "inserted":
         this.mapSelectService.inspectInsert(table, fieldMap);
         break;
       case "updated":
         this.mapSelectService.inspectUpdate(table, fieldMap);
         break;
     } 
   }
 
 
 
 
   
   public void save(GridModel grid, Map<String, Object> parentData, TriggerDescribe requestTriggerr, Integer isList) {
	   if (!grid.isInMemory()) {
           return;
       }
       final TableDescribe table = this.dataService.getTableDesc(grid.getTable());
       final TableDescribe parentTable = this.dataService.getTableDesc(table.getParent_id());
       final String[] parentIds = parentTable.getIdColumns();
       final String[] ids = table.getIdColumns();
       final String lastKey = ids[ids.length - 1];
       for (final RecordModel record : grid.getRecords()) {
           final Map<String, FieldModelBase> fieldMap = record.getFieldMap();
           for (int keyCnt = 0; keyCnt < parentIds.length; ++keyCnt) {
               fieldMap.get(ids[keyCnt]).setValue(parentData.get(parentIds[keyCnt]));
           }
           final Map<String, Object> data = this.buildRecordData(record);
           final List<CalendarEvent> calendarEvents = record.getCalendarEvents();
           String maxId = this.calendarDao.getCalendarEventMaxId();
           if (maxId == null) {
               maxId = "0000000000";
           }
           String pid = null;
           if (table.hasComplexColumn()) {
               pid = this.complexInputService.makeComplexKey(table, record);
           }
           final List<FieldModelBase> fields = record.getFields();
           switch (record.getStatus()) {
               case inserted: {
                   if (calendarEvents != null && calendarEvents.size() != 0) {
                       for (final CalendarEvent calendarEvent : calendarEvents) {
                           maxId = this.pcCalendarService.getNextId(maxId);
                           calendarEvent.setP_calendar_event_id(maxId);
                           this.calendarDao.addCalendarEvents(calendarEvent);
                       }
                   }
                   if (table.hasComplexColumn()) {
                       this.complexInputService.insert((List)fields, pid);
                   }
                   this.apiService.insertRecordWithTrigger(table, (Map)data, requestTriggerr, isList);
                   this.mapSelectService.inspectInsert(table, (Map)record.getFieldMap());
                   continue;
               }
               case updated: {
                   if (calendarEvents != null && calendarEvents.size() != 0) {
                       for (final CalendarEvent calendarEvent : calendarEvents) {
                           if ("add".equals(calendarEvent.getStatus())) {
                               maxId = this.pcCalendarService.getNextId(maxId);
                               calendarEvent.setP_calendar_event_id(maxId);
                               this.calendarDao.addCalendarEvents(calendarEvent);
                           }
                           else {
                               this.calendarDao.updateCalendarEventsByTable(calendarEvent);
                           }
                       }
                   }
                   if (table.hasComplexColumn()) {
                       this.complexInputService.update((List)fields, pid);
                   }
                   this.apiService.updateRecordWithTrigger(table, (Map)data, (List)fields, requestTriggerr);
                   if (table.hasComplexColumn()) {
                       this.complexInputService.removeOld(table, pid);
                   }
                   this.mapSelectService.inspectUpdate(table, (Map)record.getFieldMap());
                   continue;
               }
               case deleted: {
                   if (calendarEvents != null && calendarEvents.size() != 0) {
                       for (final CalendarEvent calendarEvent : calendarEvents) {
                           this.calendarDao.deleteCalendarEventsByTable(calendarEvent);
                       }
                   }
                   this.mapSelectService.inspectDelete(table, (Map)data);
                   if (table.hasComplexColumn()) {
                       this.complexInputService.delete(table, pid);
                   }
                   final List<Map<String, Object>> deleteData = new ArrayList<Map<String, Object>>();
                   deleteData.add(this.apiService.buildData((List)fields));
                   this.apiService.deleteRecordWithTrigger(table, (List)deleteData, requestTriggerr);
                   if (table.hasComplexColumn()) {
                       this.complexInputService.removeOld(table, pid);
                       continue;
                   }
                   continue;
               }
           }
       }
   }
 
 
 
 
 
 
 
 
   
   public void autoGen(GridModel grid, Map<String, Object> param) {
     TableDescribe table = this.dataService.getTableDesc(grid.getTable());
     List<Map<String, Object>> list = this.dataService.dynamicQueryList(table.getAuto_gen_sql(), param);
     grid.reset();
     for (Map<String, Object> item : list) {
       String[] keys = table.getIdColumns();
       Map<String, Object> parentKey = grid.getParentKey();
       if (parentKey != null) {
         TableDescribe parentTable = this.dataService.getTableDesc(table.getParent_id());
         String[] parentKeys = parentTable.getIdColumns();
         for (int i = 0; i < keys.length - 1; i++) {
           item.put(keys[i], parentKey.get(parentKeys[i]));
         }
       } 
       insert(grid, item);
     } 
   }
   
   public void checkParam(GridModel grid, GridRequestModel param) {
     if (!this.dataService.sqlParamCheck(grid.getTable())) {
       throw new ApplicationException("unlawful parameters");
     }
     if (param != null && param.getFilter() != null && param.getFilter().getFilters() != null) {
       if (!this.dataService.sqlParamCheck(param.getFilter().getTableName())) {
         throw new ApplicationException("unlawful parameters");
       }
       Map<String, FilterDescribe> filters = param.getFilter().getFilters();
       if (filters.get("search") != null && ((FilterDescribe)filters.get("search")).getValue() != null && 
         !this.dataService.sqlParamCheck(((FilterDescribe)filters.get("search")).getValue().toString())) {
         ((FilterDescribe)filters.get("search")).setValue(StringEscapeUtils.escapeSql(((FilterDescribe)filters.get("search")).getValue().toString()));
       }
       
       for (Map.Entry<String, FilterDescribe> entry : filters.entrySet()) {
         if (!this.dataService.sqlParamCheck((String)entry.getKey())) {
           throw new ApplicationException("unlawful parameters");
         }
         if (!this.dataService.sqlParamCheck((((FilterDescribe)entry.getValue()).getValue() == null) ? "" : ((FilterDescribe)entry.getValue()).getValue().toString()))
           ((FilterDescribe)entry.getValue()).setValue(StringEscapeUtils.escapeSql(((FilterDescribe)entry.getValue()).getValue().toString())); 
       } 
     } 
   }
   
   public DataTablePagingDataModel page(GridModel grid, GridRequestModel param, AuthDetails user) {
     DataTablePagingParameterModel paging = param.getPaging();
     DataTablePagingDataModel ret = new DataTablePagingDataModel(paging);
     List<RecordModel> list = list(grid, param, user);
     ret.setRecords(list);
     int count = count(grid, param, user);
     ret.setRecordsFiltered(count);
     ret.setRecordsTotal(count);
     
     return ret;
   }
   
   public void reorder(GridModel grid, Map<String, Integer> reorder) {
     TableDescribe table = this.dataService.getTableDesc(grid.getTable());
     String seq = table.getViewStyle().getSeq();
     for (Map.Entry<String, Integer> r : reorder.entrySet()) {
       RecordModel record = (RecordModel)ViewService.fetchWidgetModel((String)r.getKey());
       ((FieldModelBase)record.getFieldMap().get(seq)).setValue(r.getValue());
       if (record.getStatus() != RecordModel.Status.inserted)
         record.setStatus(RecordModel.Status.updated); 
     } 
   }
   
   public List<Map<String, Object>> listAll(GridModel grid, GridRequestModel param, AuthDetails user) {
     String tableName = grid.getTable();
     List<AuthGroup> optionAuthGroup = this.authService.getOptionAuthGroup(tableName, "read");
     String condition = buildCondition(tableName, param.getFilter());
     String search = buildSearch(tableName, param.getFilter());
     if (condition == null) {
       if (null != search && !"".equals(search)) {
         condition = "(" + search + ")";
       }
     } else if (search != null) {
       condition = condition + " and (" + search + ")";
     } 
     
     List<ColumnDescribe> refcols = new ArrayList<ColumnDescribe>();
     List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
     
     List<Map<String, Object>> records = this.dataService.fetchDataRecords(tableName, grid
         .getParentKey(), condition, optionAuthGroup, param.getPaging(), param.getIsApproveSelect(), param.getApproveSelectParam(), grid.isRefChild(), grid.getRefChildColumn());
     TableDescribe table = this.dataService.getTableDesc(tableName);
     for (ColumnDescribe col : table.getColumns()) {
       if (!Common.isBlank(col.getRef_table_name()) || !Common.isBlank(col.getDic_id())) {
         refcols.add(col);
       }
     } 
     for (Map<String, Object> record : records) {
       for (ColumnDescribe col : refcols) {
         String key = col.getColumn_name();
         if (!Common.isBlank(col.getDic_id())) {
           record.put(key, this.dataService.getDictText(col.getDic_id(), (record.get(key) == null) ? null : record.get(key).toString())); continue;
         } 
         record.put(key, this.dataService.buildNameExpression(this.dataService.getTableDesc(col.getRef_table_name()), (Map)record.get(key + ".ref")));
       } 
 
       
       data.add(record);
     } 
     
     return data;
   }
   
   public List<DatabaseDataMap> filterKeyList(GridRequestModel param) {
     String tableName = param.getFilter().getTableName();
     TableDescribe table = this.dataService.getTableDesc(tableName);
     String condition = buildCondition(tableName, param.getFilter());
     return this.dataService.fetchAllDataRecords(tableName, table, condition);
   }
 }


