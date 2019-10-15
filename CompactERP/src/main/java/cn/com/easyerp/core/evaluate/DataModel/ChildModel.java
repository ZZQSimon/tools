 package cn.com.easyerp.core.evaluate.DataModel;
 
 import java.util.List;
import java.util.Map;
 
 
 
 public class ChildModel
 {
   private String id;
   private String parent;
   private String tableName;
   private List<DataModel> childrenDatas;
   private Map<String, ChildModel> children;
   
   public ChildModel(String id, String parent, String tableName) {
     this.id = id;
     this.parent = parent;
     this.tableName = tableName;
   }
 
   
   public String getId() { return this.id; }
 
 
   
   public void setId(String id) { this.id = id; }
 
 
   
   public String getParent() { return this.parent; }
 
 
   
   public void setParent(String parent) { this.parent = parent; }
 
 
   
   public String getTableName() { return this.tableName; }
 
 
   
   public void setTableName(String tableName) { this.tableName = tableName; }
 
 
   
   public List<DataModel> getChildrenDatas() { return this.childrenDatas; }
 
 
   
   public void setChildrenDatas(List<DataModel> childrenDatas) { this.childrenDatas = childrenDatas; }
 
 
   
   public Map<String, ChildModel> getChildren() { return this.children; }
 
 
   
   public void setChildren(Map<String, ChildModel> children) { this.children = children; }
 }


