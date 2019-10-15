 package cn.com.easyerp.core.widget.complex;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.ComplexColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.widget.ComplexInputFieldModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.RecordModel;
 
 
 @Service
 public class ComplexInputService
 {
   public static final String IS_OLD_KEY = "is_old";
   private static final HashSet<String> oldColumn = new HashSet();
   private static final ColumnValue oldCV = new ColumnValue("is_old", Integer.valueOf(1));
   
   static  {
     oldColumn.add("is_old");
   }
 
   
   @Autowired
   private DataService dataService;
   
   public String makeComplexKey(TableDescribe table, RecordModel record) {
     String ret = null;
     for (String key : table.getIdColumns()) {
       String val = ((FieldModelBase)record.getFieldMap().get(key)).getValue().toString();
       if (ret == null) {
         ret = val;
       } else {
         ret = ret + "," + val;
       } 
     }  return ret;
   }
 
   
   public String makeComplexKey(TableDescribe table, Map<String, Object> data) {
     String ret = null;
     for (String key : table.getIdColumns()) {
       if (ret == null)
       { ret = (String)data.get(key); }
       else
       { ret = ret + "," + data.get(key); } 
     }  return ret;
   }
 
 
 
 
 
 
 
   
   public void insert(List<FieldModelBase> fields, String pid) {
     for (FieldModelBase field : fields) {
       ColumnDescribe desc = this.dataService.getColumnDesc(field);
       if (desc.getComplex_id() != null) {
         doInsert(this.dataService.getComplexColumn(desc.getComplex_id()), (ComplexInputFieldModel)field, pid);
       }
     } 
   }
 
 
 
 
 
 
 
   
   public void update(List<FieldModelBase> fields, String pid) {
     for (FieldModelBase field : fields) {
       
       ColumnDescribe desc = this.dataService.getColumnDesc(field);
       if (desc.getComplex_id() == null)
         continue; 
       ComplexInputFieldModel complexField = (ComplexInputFieldModel)field;
       
       if (complexField.getDetails() == null)
         continue; 
       ComplexColumnDescribe complex = this.dataService.getComplexColumn(desc.getComplex_id());
       
       String parentTable = complexField.getTable();
       TableDescribe tableDescribe = this.dataService.getTableDesc(complex.getDetail_tbl());
       String[] idColumns = tableDescribe.getIdColumns();
       HashSet<String> keys = new HashSet<String>(idColumns.length - 1);
       for (String column : idColumns) {
         if (!"is_old".equals(column))
           keys.add(column); 
       }  for (Map<String, Object> record : complexField.getDetails()) {
         String pid_col = complex.getDetail_tbl_pid_col();
         String pnm_col = complex.getDetail_tbl_pnm_col();
         record.put(pid_col, pid);
         record.put(pnm_col, parentTable);
         record.put("is_old", Integer.valueOf(1));
         this.dataService.updateRecord(complex.getDetail_tbl(), record, oldColumn, keys, null);
       } 
       
       doInsert(complex, complexField, pid);
     } 
   }
 
 
 
 
 
 
 
   
   public void delete(TableDescribe table, String pid) {
     for (ColumnDescribe column : table.getComplexColumns()) {
       ComplexColumnDescribe complex = this.dataService.getComplexColumn(column.getComplex_id());
       
       doDelete(complex, column.getTable_id(), pid);
     } 
   }
 
 
 
 
 
 
 
   
   public void removeOld(TableDescribe table, String pid) {
     List<ColumnValue> keys = new ArrayList<ColumnValue>(2);
     for (ColumnDescribe desc : table.getComplexColumns()) {
       ComplexColumnDescribe complex = this.dataService.getComplexColumn(desc.getComplex_id());
       
       keys.clear();
       keys.add(oldCV);
       keys.add(new ColumnValue(complex.getDetail_tbl_pid_col(), pid));
       keys.add(new ColumnValue(complex.getDetail_tbl_pnm_col(), desc.getTable_id()));
       this.dataService.deleteRecord(complex.getDetail_tbl(), keys);
     } 
   }
 
 
 
 
 
 
 
 
   
   private void doDelete(ComplexColumnDescribe complex, String parentTable, String pid) {
     String pid_col = complex.getDetail_tbl_pid_col();
     String pnm_col = complex.getDetail_tbl_pnm_col();
     
     Map<String, Object> data = new HashMap<String, Object>(3);
     data.put(pid_col, pid);
     data.put(pnm_col, parentTable);
     data.put("is_old", Integer.valueOf(1));
     HashSet<String> keys = new HashSet<String>(2);
     keys.add(pid_col);
     keys.add(pnm_col);
     this.dataService.updateRecord(complex.getDetail_tbl(), data, oldColumn, keys, null);
   }
 
 
 
 
 
 
 
 
 
   
   private void doInsert(ComplexColumnDescribe complex, ComplexInputFieldModel field, String pid) {
     if (field.getDetails() == null)
       return; 
     for (Map<String, Object> values : field.getDetails()) {
       values.put(complex.getDetail_tbl_pnm_col(), field.getTable());
       values.put(complex.getDetail_tbl_pid_col(), pid);
       values.put("is_old", Integer.valueOf(0));
       this.dataService.insertRecord(complex.getDetail_tbl(), values);
     } 
   }
 
 
 
 
 
 
 
 
   
   public void saveToRecord(RecordModel record, Set<String> fids) {
     for (String fid : fids) {
       FieldModelBase field = ViewService.fetchFieldModel(fid);
       if (!ComplexInputFieldModel.class.isInstance(field))
         continue; 
       ComplexInputFieldModel orig = (ComplexInputFieldModel)field;
       
       ComplexInputFieldModel ins = (ComplexInputFieldModel)record.getFieldMap().get(orig.getColumn());
       orig.copyExtra(ins);
     } 
   }
 
 
 
 
 
 
 
   
   public void delete(TableDescribe table, Map<String, Object> data) {
     for (ColumnDescribe desc : table.getComplexColumns()) {
       ComplexColumnDescribe complex = this.dataService.getComplexColumn(desc.getComplex_id());
       
       doDelete(complex, table.getId(), makeComplexKey(table, data));
     } 
   }
 }


