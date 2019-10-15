 package cn.com.easyerp.core.view.form.list;
 
 import java.util.Map;
import java.util.Set;

import cn.com.easyerp.core.cache.style.TableViewStyle;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.form.FormWithFilterModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 
 @Widget("list")
 public class ListFormModel
   extends FormWithFilterModel
 {
   private GridModel grid;
   private boolean leftSelect = false;
   private String tabIcon;
   private boolean load;
   private boolean control;
   private Map<String, FilterDescribe> leftSelectFilters;
   
   public ListFormModel(ActionType action, String parent, String tableName, FilterModel filter, GridModel grid, Set<TableViewStyle.Button> hideButtons, boolean load, boolean control) {
     super(action, parent, tableName, filter);
     setHideButtons(hideButtons);
     this.grid = grid;
     this.load = load;
     this.control = control;
     grid.setParent(getId());
   }
 
 
   
   public GridModel getGrid() { return this.grid; }
 
 
 
   
   public boolean isLoad() { return this.load; }
 
 
   
   public String getTabIcon() { return this.tabIcon; }
 
 
   
   public void setTabIcon(String tabIcon) { this.tabIcon = tabIcon; }
 
 
 
   
   public boolean isControl() { return this.control; }
 
 
   
   public boolean isLeftSelect() { return this.leftSelect; }
 
 
   
   public void setLeftSelect(boolean leftSelect) { this.leftSelect = leftSelect; }
 
 
   
   public Map<String, FilterDescribe> getLeftSelectFilters() { return this.leftSelectFilters; }
 
 
   
   public void setLeftSelectFilters(Map<String, FilterDescribe> leftSelectFilters) { this.leftSelectFilters = leftSelectFilters; }
 }


