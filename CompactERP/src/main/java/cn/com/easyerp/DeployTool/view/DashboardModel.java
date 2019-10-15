 package cn.com.easyerp.DeployTool.view;
 
 import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 @Widget("dashboard")
 public class DashboardModel
   extends FormModelBase
 {
   protected DashboardModel(String parent) { super(ActionType.view, parent); }
 
 
 
   
   public String getTitle() { return "dashboard"; }
 }


