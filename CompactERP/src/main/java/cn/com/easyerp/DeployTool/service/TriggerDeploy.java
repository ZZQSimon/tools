 package cn.com.easyerp.DeployTool.service;
 
 import cn.com.easyerp.core.cache.TriggerDescribe;
 
 
 
 public class TriggerDeploy
   extends TriggerDescribe
 {
   private int conditionChange;
   private int apiChange;
   private int buttonActionChange;
   
   public int getConditionChange() { return this.conditionChange; }
 
 
   
   public void setConditionChange(int conditionChange) { this.conditionChange = conditionChange; }
 
 
   
   public int getApiChange() { return this.apiChange; }
 
 
   
   public void setApiChange(int apiChange) { this.apiChange = apiChange; }
 
 
   
   public int getButtonActionChange() { return this.buttonActionChange; }
 
 
   
   public void setButtonActionChange(int buttonActionChange) { this.buttonActionChange = buttonActionChange; }
 }


