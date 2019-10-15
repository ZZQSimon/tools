 package cn.com.easyerp.generate;
 
 import java.util.List;
import java.util.Set;

import cn.com.easyerp.core.cache.AutoGenTableDesc;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.form.FormWithFilterModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.StdGridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 
 @Widget("ag")
 public class AutoGenerateFormModel
   extends FormWithFilterModel
 {
   private AutoGenTableDesc ag;
   private StdGridModel grid;
   private boolean hasSum;
   private List<String> columns;
   private Set<String> inputs;
   
   protected AutoGenerateFormModel(String parent, String tableName, AutoGenTableDesc ag, FilterModel filter, StdGridModel grid, boolean hasSum, List<String> columns, Set<String> inputs) {
     super(ActionType.edit, parent, tableName, filter);
     this.ag = ag;
     this.grid = grid;
     this.hasSum = hasSum;
     this.columns = columns;
     this.inputs = inputs;
     grid.setParent(getId());
   }
 
 
   
   public AutoGenTableDesc getAg() { return this.ag; }
 
 
 
   
   public StdGridModel getGrid() { return this.grid; }
 
 
 
   
   public boolean isHasSum() { return this.hasSum; }
 
 
 
   
   public List<String> getColumns() { return this.columns; }
 
 
 
   
   public Set<String> getInputs() { return this.inputs; }
 }


