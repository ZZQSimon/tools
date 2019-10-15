 package cn.com.easyerp.core.widget.complex;
 
 import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.cache.ComplexColumnDescribe;
import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 
 
 
 
 
 @Widget("complex")
 public class ComplexInputFormModel
   extends TableBasedFormModel
 {
   private ComplexColumnDescribe desc;
   private String column;
   private List<String> columns;
   private GridModel grid;
   
   protected ComplexInputFormModel(ActionType action, String parent, String tableName, String column, List<String> columns, ComplexColumnDescribe desc, GridModel grid) {
     super(action, parent, tableName);
     this.columns = columns;
     this.column = column;
     grid.setParent(getId());
     this.grid = grid;
     this.desc = desc;
   }
 
 
   
   public GridModel getGrid() { return this.grid; }
 
 
 
   
   @JsonIgnore
   public ComplexColumnDescribe getDesc() { return this.desc; }
 
 
 
   
   public List<String> getColumns() { return this.columns; }
 
 
 
   
   @JsonIgnore
   public String getColumn() { return this.column; }
 }


