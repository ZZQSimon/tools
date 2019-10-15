 package cn.com.easyerp.range;
 
 import java.util.Date;
import java.util.Map;

import cn.com.easyerp.core.view.TableFormRequestModel;
 
 
 
 
 
 
 
 
 public class RangeRequestModel
   extends TableFormRequestModel
 {
   private Map<String, Object> param;
   private Date validDate;
   private String groupCols;
   private String mode;
   
   public Map<String, Object> getParam() { return this.param; }
 
 
 
   
   public void setParam(Map<String, Object> param) { this.param = param; }
 
 
   
   public Date getValidDate() { return this.validDate; }
 
 
   
   public void setValidDate(Date validDate) { this.validDate = validDate; }
 
 
   
   public String getGroupCols() { return this.groupCols; }
 
 
   
   public void setGroupCols(String groupCols) { this.groupCols = groupCols; }
 
 
   
   public String getMode() { return this.mode; }
 
 
   
   public void setMode(String mode) { this.mode = mode; }
 }


