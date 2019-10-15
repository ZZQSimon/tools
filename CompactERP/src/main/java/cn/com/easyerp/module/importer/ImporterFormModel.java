 package cn.com.easyerp.module.importer;
 
 import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 @Widget("importer")
 public class ImporterFormModel
   extends FormModelBase
 {
   private String customerId;
   private int typeId;
   private List<ColumnDescribe> columns;
   private Map<String, ColumnDescribe> keyColMap;
   private List<RecordModel> records;
   private CustomImportResult result;
   
   protected ImporterFormModel(List<ColumnDescribe> columns, Map<String, ColumnDescribe> keyColMap, String customerId, int typeId) {
     super(ActionType.create, null);
     this.columns = columns;
     setKeyColMap(keyColMap);
     this.customerId = customerId;
     this.typeId = typeId;
   }
 
 
 
   
   public String getTitle() { return Common.getDataService().getMessageText("import excel", new Object[0]); }
 
 
 
   
   public String getCustomerId() { return this.customerId; }
 
 
 
   
   public int getTypeId() { return this.typeId; }
 
 
 
   
   public List<ColumnDescribe> getColumns() { return this.columns; }
 
 
 
   
   public List<RecordModel> getRecords() { return this.records; }
 
 
 
   
   public void setRecords(List<RecordModel> records) { this.records = records; }
 
 
 
   
   public CustomImportResult getResult() { return this.result; }
 
 
 
   
   public void setResult(CustomImportResult result) { this.result = result; }
 
 
 
   
   public Map<String, ColumnDescribe> getKeyColMap() { return this.keyColMap; }
 
 
 
   
   public void setKeyColMap(Map<String, ColumnDescribe> keyColMap) { this.keyColMap = keyColMap; }
 }


