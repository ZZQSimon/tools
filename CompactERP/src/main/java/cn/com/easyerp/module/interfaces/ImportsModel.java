 package cn.com.easyerp.module.interfaces;
 
 import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 
 
 
 @Widget("imports")
 public class ImportsModel
   extends FormModelBase
 {
   private GridModel grid;
   private List<Map<String, Object>> typeList;
   
   public ImportsModel(ActionType action, String parent) {
     super(action, parent);
     this.grid = this.grid;
   }
 
 
 
   
   public GridModel getGrid() { return this.grid; }
 
 
   
   public List<Map<String, Object>> getTypeList() { return this.typeList; }
 
 
   
   public void setTypeList(List<Map<String, Object>> typeList) { this.typeList = typeList; }
 
 
 
 
 
 
   
   public String getTitle() { return "导入页面"; }
 }


