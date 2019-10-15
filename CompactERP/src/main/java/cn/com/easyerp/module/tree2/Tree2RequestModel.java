 package cn.com.easyerp.module.tree2;
 
 import java.util.Map;

import cn.com.easyerp.core.view.TableFormRequestModel;
 
 
 
 
 
 
 
 
 public class Tree2RequestModel
   extends TableFormRequestModel
 {
   private Map<String, Object> param;
   private String item_no;
   private String process_no;
   private String lot_no;
   private String conditions;
   
   public Map<String, Object> getParam() { return this.param; }
 
 
 
   
   public void setParam(Map<String, Object> param) { this.param = param; }
 
 
   
   public String getConditions() { return this.conditions; }
 
 
   
   public void setConditions(String conditions) { this.conditions = conditions; }
 
 
   
   public String getItem_no() { return this.item_no; }
 
 
   
   public void setItem_no(String item_no) { this.item_no = item_no; }
 
 
   
   public String getProcess_no() { return this.process_no; }
 
 
   
   public void setProcess_no(String process_no) { this.process_no = process_no; }
 
 
   
   public String getLot_no() { return this.lot_no; }
 
 
   
   public void setLot_no(String lot_no) { this.lot_no = lot_no; }
 }


