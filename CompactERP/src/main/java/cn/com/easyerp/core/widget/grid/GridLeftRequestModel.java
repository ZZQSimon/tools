 package cn.com.easyerp.core.widget.grid;
 
 import java.util.Map;

import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;
 
 
 
 public class GridLeftRequestModel
   extends WidgetRequestModelBase
 {
   private String tableName;
   private String column;
   private Map<String, FilterDescribe> leftSelectFilters;
   
   public String getTableName() { return this.tableName; }
 
 
   
   public void setTableName(String tableName) { this.tableName = tableName; }
 
 
   
   public String getColumn() { return this.column; }
 
 
   
   public void setColumn(String column) { this.column = column; }
 
 
   
   public Map<String, FilterDescribe> getLeftSelectFilters() { return this.leftSelectFilters; }
 
 
   
   public void setLeftSelectFilters(Map<String, FilterDescribe> leftSelectFilters) { this.leftSelectFilters = leftSelectFilters; }
 }


