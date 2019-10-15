 package cn.com.easyerp.auth.externalForm;
 
 import cn.com.easyerp.core.view.FormRequestModelBase;
 
 public class ExternalFormRequestModel extends FormRequestModelBase<ExternalFormModel> {
   private String jsondata;
   
   public String getJsondata() { return this.jsondata; }
   
   private String str;
   
   public void setJsondata(String jsondata) { this.jsondata = jsondata; }
 
 
   
   public String getStr() { return this.str; }
 
 
   
   public void setStr(String str) { this.str = str; }
 }


