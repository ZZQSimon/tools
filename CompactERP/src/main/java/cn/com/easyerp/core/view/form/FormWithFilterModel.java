 package cn.com.easyerp.core.view.form;
 
 import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 
 @Widget("filter")
 public class FormWithFilterModel
   extends TableBasedFormModel
 {
   private FilterModel filter;
   
   protected FormWithFilterModel(ActionType action, String parent, String tableName) { this(action, parent, tableName, null); }
 
 
   
   protected FormWithFilterModel(ActionType action, String parent, String tableName, FilterModel filter) {
     super(action, parent, tableName);
     this.filter = (filter == null) ? new FilterModel(tableName) : filter;
     this.filter.setParent(getId());
   }
 
 
   
   public FilterModel getFilter() { return this.filter; }
 }


