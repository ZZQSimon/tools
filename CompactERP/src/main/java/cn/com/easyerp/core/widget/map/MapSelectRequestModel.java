 package cn.com.easyerp.core.widget.map;
 
 import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;
 
 
 
 
 
 
 
 public class MapSelectRequestModel
   extends FormRequestModelBase<MapSelectFormModel>
 {
   private Map<String, Object> filterData;
   private Map<String, String> selection;
   
   public Map<String, Object> getFilterData() { return this.filterData; }
 
 
 
   
   public void setFilterData(Map<String, Object> filterData) { this.filterData = filterData; }
 
 
 
   
   public Map<String, String> getSelection() { return this.selection; }
 
 
 
   
   public void setSelection(Map<String, String> selection) { this.selection = selection; }
 }


