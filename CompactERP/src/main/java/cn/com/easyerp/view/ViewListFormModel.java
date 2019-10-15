 package cn.com.easyerp.view;
 
 import java.util.Set;

import cn.com.easyerp.core.cache.style.TableViewStyle;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.form.list.ListFormModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
 @Widget("list")
 public class ViewListFormModel extends ListFormModel {
   public static final String editUrl = "/view/edit.view";
   
   public ViewListFormModel(String parent, String tableName, FilterModel filter, GridModel grid, Set<TableViewStyle.Button> hideButtons, boolean load, boolean control) { super(ActionType.view, parent, tableName, filter, grid, hideButtons, load, control); }
   
   public String getEditUrl() { return "/view/edit.view"; }
   
   protected String getView() { return "viewList"; }
 }