 package cn.com.easyerp.module.customReport;
 
 import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.form.FormWithFilterModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 @Widget("customReport")
 public class CustomReportModel
   extends FormWithFilterModel
 {
   protected CustomReportModel(String parent, String tableName, FilterModel filter) { super(ActionType.view, parent, tableName, filter); }
 
 
   
   public String getTitle() { return "customReport"; }
 }


