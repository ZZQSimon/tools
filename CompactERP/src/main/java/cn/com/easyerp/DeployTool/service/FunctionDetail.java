 package cn.com.easyerp.DeployTool.service;
 
 import java.util.List;
 
 public class FunctionDetail
 {
   private List<FunctionPoint> insert;
   
   public List<FunctionPoint> getInsert() { return this.insert; }
   private List<FunctionPoint> update; private List<FunctionPoint> deleted;
   
   public void setInsert(List<FunctionPoint> insert) { this.insert = insert; }
 
   
   public List<FunctionPoint> getUpdate() { return this.update; }
 
   
   public void setUpdate(List<FunctionPoint> update) { this.update = update; }
 
   
   public List<FunctionPoint> getDelete() { return this.deleted; }
 
   
   public void setDelete(List<FunctionPoint> delete) { this.deleted = delete; }
 }


