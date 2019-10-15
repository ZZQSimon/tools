 package cn.com.easyerp.core.widget.grid;
 
 import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.widget.FieldModelBase;
 
 
 
 
 
 
 
 public class RecordModelWithTags
   extends RecordModel
 {
   private Map<String, String> fieldTags;
   
   public RecordModelWithTags(List<FieldModelBase> fields) { super(fields); }
 
 
 
   
   public Map<String, String> getFieldTags() { return this.fieldTags; }
 
 
 
   
   public void setFieldTags(Map<String, String> fieldTags) { this.fieldTags = fieldTags; }
 }


