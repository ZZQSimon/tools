 package cn.com.easyerp.view;
 
 import java.util.List;

import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
 @Widget("detail")
 public class ViewDetailFormModel
   extends DetailFormModel
 {
   public static final String editUrl = "/view/edit.view";
   public static final String nextUrl = "/view/next.view";
   
   public ViewDetailFormModel(ActionType action, String parent, String tableName, List<FieldModelBase> fields, List<GridModel> children, int cols) { super(action, parent, tableName, fields, children, cols); }
 
   public String getNextUrl() { return "/view/next.view"; }
   
   public String getEditUrl() { return "/view/edit.view"; }
 
   protected String getView() { return "viewDetail"; }
 }