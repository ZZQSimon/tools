 package cn.com.easyerp.passage;
 
 import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.widget.FieldModelBase;
 
 
 
 
 
 
 
 
 public class PassageCatalogModel
 {
   private String id;
   private List<FieldModelBase> fields;
   private List<Map<String, Object>> detail;
   private Map<String, List<Map<String, Object>>> catalogs;
   
   public PassageCatalogModel(String id, List<FieldModelBase> fields, Map<String, List<Map<String, Object>>> catalogs, List<Map<String, Object>> detail) {
     this.id = id;
     this.fields = fields;
     this.detail = detail;
     this.catalogs = catalogs;
   }
 
 
   
   public String getId() { return this.id; }
 
 
 
   
   public List<FieldModelBase> getFields() { return this.fields; }
 
 
 
   
   public Map<String, List<Map<String, Object>>> getCatalogs() { return this.catalogs; }
 
 
 
   
   public List<Map<String, Object>> getDetail() { return this.detail; }
 }


