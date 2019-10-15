 package cn.com.easyerp.DeployTool.service;
 
 import cn.com.easyerp.core.cache.AutoGenTableDesc;
 
 
 
 public class AutoGenDeploy
   extends AutoGenTableDesc
 {
   private String id_I18N;
   private String editable_cols;
   
   public String getId_I18N() { return this.id_I18N; }
 
 
   
   public void setId_I18N(String id_I18N) { this.id_I18N = id_I18N; }
 
 
   
   public String getEditable_cols() { return this.editable_cols; }
 
 
   
   public void setEditable_cols(String editable_cols) { this.editable_cols = editable_cols; }
 }


