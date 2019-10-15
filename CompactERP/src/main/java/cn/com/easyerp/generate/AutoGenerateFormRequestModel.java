 package cn.com.easyerp.generate;
 
 import java.util.Map;

import cn.com.easyerp.core.view.TableFormRequestModel;
 
 
 
 
 
 
 
 
 
 public class AutoGenerateFormRequestModel
   extends TableFormRequestModel
 {
   private Map<String, Object> filterData;
   private Map<String, Map<String, Object>> data;
   
   public Map<String, Object> getFilterData() { return this.filterData; }
 
 
 
   
   public void setFilterData(Map<String, Object> filterData) { this.filterData = filterData; }
 
 
 
   
   public Map<String, Map<String, Object>> getData() { return this.data; }
 
 
 
   
   public void setData(Map<String, Map<String, Object>> data) { this.data = data; }
 }


