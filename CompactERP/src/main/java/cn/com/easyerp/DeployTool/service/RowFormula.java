 package cn.com.easyerp.DeployTool.service;
 
 import java.util.List;
 
 public class RowFormula {
   private List<PassageRowFormula> inserted;
   
   public List<PassageRowFormula> getInserted() { return this.inserted; }
   private List<PassageRowFormula> updated; private List<PassageRowFormula> deleted;
   
   public void setInserted(List<PassageRowFormula> inserted) { this.inserted = inserted; }
 
   
   public List<PassageRowFormula> getUpdated() { return this.updated; }
 
   
   public void setUpdated(List<PassageRowFormula> updated) { this.updated = updated; }
 
   
   public List<PassageRowFormula> getDeleted() { return this.deleted; }
 
   
   public void setDeleted(List<PassageRowFormula> deleted) { this.deleted = deleted; }
 }


