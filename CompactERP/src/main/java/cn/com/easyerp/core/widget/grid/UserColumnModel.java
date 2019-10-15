 package cn.com.easyerp.core.widget.grid;
 
 import java.util.List;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 @Widget("userColumn")
 public class UserColumnModel
   extends FormModelBase
 {
   private List<UserColumn> userColumns;
   private String table;
   
   public String getTable() { return this.table; }
 
 
   
   public void setTable(String table) { this.table = table; }
 
 
   
   public List<UserColumn> getUserColumns() { return this.userColumns; }
 
 
   
   public void setUserColumns(List<UserColumn> userColumns) { this.userColumns = userColumns; }
 
 
   
   public UserColumnModel(String parent) { super(ActionType.view, parent); }
 
 
   
   public String getTitle() { return ""; }
 }


