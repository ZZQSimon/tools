 package cn.com.easyerp.core.widget;
 
 import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.ComplexColumnDescribe;
 
 
 
 
 
 
 
 
 public class ComplexInputFieldModel
   extends FieldModelBase
 {
   private List<Map<String, Object>> details;
   private List<String> columnNames;
   private String detailColumnName;
   private Set<String> allColumnNames;
   
   public ComplexInputFieldModel(ColumnDescribe desc, Object value, ComplexColumnDescribe complex) {
     super(desc.getTable_id(), desc.getColumn_name(), value);
     this.detailColumnName = complex.getDetail_tbl_number_col();
   }
 
 
 
   
   public ComplexInputFieldModel() {}
 
 
 
   
   public FieldModelBase copyExtra(FieldModelBase field) {
     ComplexInputFieldModel f = (ComplexInputFieldModel)field;
     f.details = this.details;
     f.columnNames = this.columnNames;
     f.allColumnNames = this.allColumnNames;
     f.detailColumnName = this.detailColumnName;
     return field;
   }
 
 
   
   public List<Map<String, Object>> getDetails() { return this.details; }
 
 
 
   
   public void setDetails(List<Map<String, Object>> details) { this.details = details; }
 
 
 
   
   public List<String> getColumnNames() { return this.columnNames; }
 
 
   
   public void setColumnNames(List<String> columnNames) {
     this.columnNames = columnNames;
     this.allColumnNames = new HashSet(columnNames);
     this.allColumnNames.add(this.detailColumnName);
   }
 
 
   
   public Set<String> allColumnNames() { return this.allColumnNames; }
 }


