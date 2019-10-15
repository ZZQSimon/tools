 package cn.com.easyerp.DeployTool.service;
 
 import cn.com.easyerp.core.cache.ColumnDescribe;
 
 
 
 public class ColumnDeploy
   extends ColumnDescribe
 {
   private int columnChange;
   private String rowid;
   private String oldColumnName;
   
   public String getRowid() { return this.rowid; }
 
 
   
   public void setRowid(String rowid) { this.rowid = rowid; }
 
 
   
   public int getColumnChange() { return this.columnChange; }
 
 
   
   public void setColumnChange(int columnChange) { this.columnChange = columnChange; }
 
 
   
   public String getOldColumnName() { return this.oldColumnName; }
 
 
   
   public void setOldColumnName(String oldColumnName) { this.oldColumnName = oldColumnName; }
 }


