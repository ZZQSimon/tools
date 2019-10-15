 package cn.com.easyerp.DeployTool.view;
 
 import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 @Widget("aBatch")
 public class BatchModel
   extends FormModelBase
 {
   protected BatchModel(String parent) { super(ActionType.view, parent); }
 
 
 
 
   
   public String getTitle() { return "batch"; }
 }


