 package cn.com.easyerp.core.widget.grid;
 
 import java.util.List;

import cn.com.easyerp.tree.TreeNodeModel;
 
 public class GridLeftModel
   extends StdGridModel
 {
   private List<TreeNodeModel> leftNodes;
   private String type = "0";
   
   private String column;
   
   public String getType() { return this.type; }
 
 
   
   public void setType(String type) { this.type = type; }
 
 
   
   public List<TreeNodeModel> getLeftNodes() { return this.leftNodes; }
 
 
   
   public void setLeftNodes(List<TreeNodeModel> leftNodes) { this.leftNodes = leftNodes; }
 
 
   
   public GridLeftModel(String table) { super(table); }
 
 
   
   public String getColumn() { return this.column; }
 
 
   
   public void setColumn(String column) { this.column = column; }
 }


