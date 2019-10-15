 package cn.com.easyerp.range;
 
 import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 @Controller
 @RequestMapping({"/range"})
 public class RangeController
   extends FormViewControllerBase
 {
   @Autowired
   private GridService gridService;
   @Autowired
   private DataService dataService;
   
   @RequestMapping({"/table.view"})
   public ModelAndView view(@RequestBody RangeRequestModel data) {
     List<FieldModelBase> fields;
     ActionType action;
     String tableName = data.getTable();
     TableDescribe table = this.dataService.getTableDesc(tableName);
     String groupCols = table.getValid_date_group_cols();
 
 
     
     int cols = table.getDetail_disp_cols();
     if (data.getParam() != null && data.getParam().size() > 0) {
       action = ActionType.edit;
       data.setMode("edit");
       fields = this.dataService.buildModel(table, this.dataService
           .selectRecordByKey(tableName, data.getParam()));
     } else {
       action = ActionType.create;
       data.setMode("create");
       fields = this.dataService.buildEmptyDataRecord(tableName, null);
     } 
     List<FieldModelBase> filter = new ArrayList<FieldModelBase>();
     List<FieldModelBase> detail = new ArrayList<FieldModelBase>();
     
     boolean flag = false;
     for (FieldModelBase field : fields) {
       if (groupCols != null) {
         for (String groupCol : Common.split(groupCols, ",")) {
           if (field.getColumn().equals(groupCol)) {
             
             filter.add(field);
             flag = true;
             break;
           } 
         } 
         if (!flag)
         {
           detail.add(field);
         }
         flag = false;
         continue;
       } 
       detail.add(field);
     } 
 
     
     List<GridModel> children = new ArrayList<GridModel>();
 
     
     children = childView(tableName);
 
 
 
     
     RangeFormModel form = new RangeFormModel(action, data.getParent(), data.getMode(), tableName, groupCols, filter, detail, cols, children);
 
 
 
 
 
     
     return buildModelAndView(form);
   }
   @Autowired
   private ViewService viewService; @Autowired
   private SystemDao systemDao;
   @Transactional
   @ResponseBody
   @RequestMapping({"/create.do"})
   public ActionResult Create(@RequestBody RangeRequestModel data) {
     RangeFormModel form = (RangeFormModel)ViewService.fetchFormModel(data.getId());
     TableDescribe table = this.dataService.getTableDesc(form.getTableName());
 
     
     Map<String, Object> returnData = new HashMap<String, Object>();
     DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(data.getParam());
     
     if (!table.getValid_date_cols().isEmpty()) {
       String message = "";
       
       message = dateRangeCheck(table.getValid_date_cols(), databaseDataMap);
       if (!message.isEmpty()) {
         returnData.put("msg", message);
         returnData.put("ret", "false");
         return new ActionResult(true, returnData);
       } 
 
       
       message = dateRecursiveCheck(form.getTableName(), form.getGroupCols(), databaseDataMap);
       if (!message.isEmpty()) {
         returnData.put("msg", message);
         returnData.put("ret", "false");
         return new ActionResult(true, returnData);
       } 
     } 
 
     
     String[] keys = table.getIdColumns();
     String lastKey = keys[keys.length - 1];
     ColumnDescribe lastKeyDesc = table.getColumn(lastKey);
     if (lastKeyDesc.getData_type() != 13) {
       
       String message = pkeyRecursiveCheck(form.getTableName(), databaseDataMap);
       if (!message.isEmpty()) {
         returnData.put("msg", message);
         returnData.put("ret", "false");
         return new ActionResult(true, returnData);
       } 
     } 
 
     
     int ret = this.dataService.insertRecord(form.getTableName(), databaseDataMap);
     if (ret != 1) {
       String errorMsg = this.dataService.getMessageText("DCS-901", new Object[0]);
       returnData.put("msg", errorMsg);
       returnData.put("ret", "false");
       return new ActionResult(true, returnData);
     } 
     
     String successMsg = this.dataService.getMessageText("DCS-001", new Object[0]);
     returnData.put("msg", successMsg);
     returnData.put("ret", "true");
     
     return new ActionResult(true, returnData);
   }
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping({"/edit.do"})
   public ActionResult Edit(@RequestBody RangeRequestModel data) {
     RangeFormModel form = (RangeFormModel)ViewService.fetchFormModel(data.getId());
     TableDescribe table = this.dataService.getTableDesc(form.getTableName());
 
     
     Map<String, Object> returnData = new HashMap<String, Object>();
     DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(data.getParam());
     
     int ret = 0;
     if (!table.getValid_date_cols().isEmpty()) {
       String message = "";
       
       message = dateRangeCheck(table.getValid_date_cols(), databaseDataMap);
       if (!message.isEmpty()) {
         returnData.put("msg", message);
         returnData.put("ret", "false");
         return new ActionResult(true, returnData);
       } 
 
       
       message = dateRecursiveCheck(form.getTableName(), form.getGroupCols(), databaseDataMap);
       if (!message.isEmpty()) {
         returnData.put("msg", message);
         returnData.put("ret", "false");
         return new ActionResult(true, returnData);
       } 
       
       boolean isExcute = false;
       for (String key : table.getIdColumns()) {
         if (table.getValid_date_cols().indexOf(key) >= 0) {
           isExcute = true;
           break;
         } 
       } 
       if (!isExcute) {
         
         ret = this.dataService.updateRecord(form.getTableName(), databaseDataMap);
       } else {
         
         ret = updateRecord(form
             .getTableName(), data
             .getValidDate(), table
             .getValid_date_cols(), databaseDataMap);
       }
     
     } else {
       
       ret = this.dataService.updateRecord(form.getTableName(), databaseDataMap);
     } 
     
     if (ret != 1) {
       String errorMsg = this.dataService.getMessageText("DCS-902", new Object[0]);
       returnData.put("msg", errorMsg);
       returnData.put("ret", "false");
       return new ActionResult(true, returnData);
     } 
     
     String successMsg = this.dataService.getMessageText("DCS-002", new Object[0]);
     returnData.put("msg", successMsg);
     returnData.put("ret", "true");
     
     return new ActionResult(true, returnData);
   }
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping({"/delete.do"})
   public ActionResult Delete(@RequestBody RangeRequestModel data) {
     RangeFormModel form = (RangeFormModel)ViewService.fetchFormModel(data.getId());
     TableDescribe table = this.dataService.getTableDesc(form.getTableName());
     Map<String, Object> param = data.getParam();
     
     ColumnValue colVal = null;
     List<ColumnValue> itemvalue = new ArrayList<ColumnValue>();
     for (String key : table.getIdColumns()) {
       colVal = new ColumnValue(key, param.get(key));
       itemvalue.add(colVal);
     } 
     
     int count = this.dataService.deleteRecord(form.getTableName(), itemvalue);
 
     
     Map<String, Object> returnData = new HashMap<String, Object>();
     if (count == 0) {
       String errorMsg = this.dataService.getMessageText("DCS-903", new Object[0]);
       returnData.put("msg", errorMsg);
       returnData.put("ret", "false");
       return new ActionResult(true, returnData);
     } 
     
     String successMsg = this.dataService.getMessageText("DCS-003", new Object[0]);
     returnData.put("msg", successMsg);
     returnData.put("ret", "true");
     
     return new ActionResult(true, returnData);
   }
 
 
   
   private int updateRecord(String tableName, Date validDate, String validDatacols, Map<String, Object> data) {
     TableDescribe table = this.dataService.getTableDesc(tableName);
     
     boolean hasValidData = false;
     HashSet<String> keyColumns = new HashSet<String>();
     for (String column : table.getIdColumns()) {
       if (validDatacols.indexOf(column) < 0) {
         keyColumns.add(column);
       } else {
         hasValidData = true;
       } 
     } 
     
     String where = null;
     if (hasValidData) {
       String[] validData = Common.split(validDatacols, ",", true);
       if (validData.length != 1)
       {
         if (validData.length > 1) {
           where = "'" + Common.convertToDateText(validDate) + "'" + " between " + validData[0] + " and " + validData[1];
         }
       }
     } 
 
     
     return this.dataService.updateRecord(tableName, data, null, keyColumns, where);
   }
 
 
 
 
 
 
 
   
   private List<GridModel> childView(String tableName) {
     ArrayList<GridModel> list = new ArrayList<GridModel>();
     GridModel grid = this.gridService.buildModel(tableName, null, false);
     grid.setAutoLoad(false);
     grid.setHasSum(false);
     list.add(grid);
     
     return list;
   }
 
 
   
   private String dateRangeCheck(String validDatacols, Map<String, Object> data) {
     String start_date_value = "";
     String end_date_value = "";
     String[] validData = Common.split(validDatacols, ",", true);
     boolean checkResult = true;
     
     if (validData.length == 1) {
       start_date_value = data.get(validData[0]).toString();
     }
     else if (validData.length > 1) {
       start_date_value = data.get(validData[0]).toString();
       end_date_value = data.get(validData[1]).toString();
       if (start_date_value.compareTo(end_date_value) > 0) {
         checkResult = false;
       }
     } 
     
     String message = "";
     if (!checkResult) {
       String startDate = this.dataService.getMessageText("Start_Date", new Object[0]);
       message = startDate;
       if (!end_date_value.isEmpty()) {
         message = message + "/" + this.dataService.getMessageText("End_Date", new Object[0]);
       }
       message = message + " " + this.dataService.getMessageText("incorrect value", new Object[0]);
     } 
     return message;
   }
 
 
   
   private String pkeyRecursiveCheck(String tableName, Map<String, Object> data) {
     TableDescribe table = this.dataService.getTableDesc(tableName);
     Map<String, Object> valueMap = new HashMap<String, Object>();
 
     
     for (String key : table.getIdColumns()) {
       valueMap.put(key, data.get(key));
     }
     Map<String, Object> record = this.dataService.selectRecordByKey(tableName, valueMap);
     
     String message = "";
     if (record != null) {
       message = this.dataService.getMessageText("DCS-901", new Object[0]);
     }
     return message;
   }
 
 
   
   private String dateRecursiveCheck(String tableName, String groupCols, Map<String, Object> data) {
     TableDescribe table = this.dataService.getTableDesc(tableName);
     Map<String, Object> valueMap = new HashMap<String, Object>();
 
     
     String where = "";
     String start_date = "";
     String end_date = "";
     String start_date_value = "";
     String end_date_value = "";
     String[] validData = Common.split(table.getValid_date_cols(), ",", true);
     if (validData.length == 1) {
       start_date = validData[0];
     }
     else if (validData.length > 1) {
       start_date = validData[0];
       end_date = validData[1];
       start_date_value = data.get(start_date).toString();
       end_date_value = data.get(end_date).toString();
       
       where = "('" + start_date_value + "'" + " between " + tableName + "." + start_date + " and " + tableName + "." + end_date + " or '" + end_date_value + "'" + " between " + tableName + "." + start_date + " and " + tableName + "." + end_date + " or '" + start_date_value + "'" + " <= " + tableName + "." + start_date + " and '" + end_date_value + "'" + " >= " + tableName + "." + end_date + ")";
     } 
 
 
 
 
 
 
 
 
 
     
     Set<String> columnsNeeded = new HashSet<String>();
     for (String idColumn : table.getIdColumns()) {
       columnsNeeded.add(idColumn);
       valueMap.put(idColumn, data.get(idColumn));
     } 
     valueMap.remove(start_date);
     valueMap.remove(end_date);
     if (!groupCols.isEmpty()) {
       valueMap = new HashMap<String, Object>();
       for (String groupCol : Common.split(groupCols, ",", true)) {
         valueMap.put(groupCol, data.get(groupCol));
       }
     } 
     List<Map<String, Object>> records = this.dataService.selectRecordsByValues(tableName, columnsNeeded, valueMap, where);
 
     
     boolean isExit = false;
     if (records.size() == 1) {
       for (String idColumn : table.getIdColumns()) {
         if (!data.get(idColumn).equals(((Map)records.get(0)).get(idColumn))) {
           isExit = true;
           break;
         } 
       } 
     } else if (records.size() > 1) {
       isExit = true;
     } 
     String message = "";
     if (isExit) {
       String startDate = this.dataService.getMessageText("Start_Date", new Object[0]);
       message = startDate;
       if (!end_date_value.isEmpty()) {
         message = message + "/" + this.dataService.getMessageText("End_Date", new Object[0]);
       }
       message = this.dataService.getMessageText("DCS-PKEXIST", new Object[] { message });
     } 
     
     return message;
   }
 }


