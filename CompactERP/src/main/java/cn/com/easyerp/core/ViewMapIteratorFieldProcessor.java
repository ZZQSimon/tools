 package cn.com.easyerp.core;
 
 import java.util.HashMap;
import java.util.Map;

import cn.com.easyerp.core.widget.FieldModelBase;
 
 
 
 
 
 public class ViewMapIteratorFieldProcessor
   implements ViewMapIteratorProcessor
 {
   private Map<String, FieldModelBase> fields = new HashMap<>();
 
 
   
   public boolean process(String column, Object value, FieldModelBase field) {
     field.setValue(value);
     this.fields.put(column, field);
     return true;
   }
 
 
   
   public Map<String, FieldModelBase> getFields() { return this.fields; }
 }


