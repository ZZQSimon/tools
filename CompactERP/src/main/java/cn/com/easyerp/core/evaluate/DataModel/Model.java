 package cn.com.easyerp.core.evaluate.DataModel;
 
 import java.util.Map;

import cn.com.easyerp.core.widget.FieldModelBase;
 
 
 
 public class Model
 {
   private String id;
   private String parent;
   private String action;
   private String tableName;
   private Map<String, FieldModelBase> fieldMap;
   private Map<String, ChildModel> children;
   
   public Model(String id, String parent, String action, String tableName) {
     this.id = id;
     this.parent = parent;
     this.action = action;
     this.tableName = tableName;
   }
 
   
   public String getId() { return this.id; }
 
 
   
   public void setId(String id) { this.id = id; }
 
 
   
   public String getParent() { return this.parent; }
 
 
   
   public void setParent(String parent) { this.parent = parent; }
 
 
   
   public String getAction() { return this.action; }
 
 
   
   public void setAction(String action) { this.action = action; }
 
 
   
   public String getTableName() { return this.tableName; }
 
 
   
   public void setTableName(String tableName) { this.tableName = tableName; }
 
 
   
   public Map<String, FieldModelBase> getFieldMap() { return this.fieldMap; }
 
 
   
   public void setFieldMap(Map<String, FieldModelBase> fieldMap) { this.fieldMap = fieldMap; }
 
 
   
   public Map<String, ChildModel> getChildren() { return this.children; }
 
 
   
   public void setChildren(Map<String, ChildModel> children) { this.children = children; }
 }


