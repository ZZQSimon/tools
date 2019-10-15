 package cn.com.easyerp.core.view.form.list;
 
 import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
 @Widget("mobileList")
 public class mobileTableModel
   extends FormModelBase
 {
   private String table;
   private GridModel grid;
   private String group_column;
   private List<Map<String, Object>> groups = new ArrayList<>();
 
   
   protected mobileTableModel(String parent) { super(ActionType.view, parent); }
 
 
 
   
   public String getTitle() { return "mobileList"; }
 
 
   
   public String getTable() { return this.table; }
 
 
   
   public void setTable(String table) { this.table = table; }
 
 
   
   public GridModel getGrid() { return this.grid; }
 
 
   
   public void setGrid(GridModel grid) { this.grid = grid; }
 
 
   
   public String getGroup_column() { return this.group_column; }
 
 
   
   public void setGroup_column(String group_column) { this.group_column = group_column; }
 
 
   
   public List<Map<String, Object>> getGroups() { return this.groups; }
 
 
   
   public void setGroups(List<Map<String, Object>> groups) { this.groups = groups; }
 }


