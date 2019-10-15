 package cn.com.easyerp.DeployTool.view;
 
 import java.util.Map;

import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 
 public class UrlInterfaceRequestModel
   extends FormRequestModelBase<UrlInterfaceModel>
 {
   private Map<String, UrlInterfaceDescribe> editUrlInterfaceMap;
   private Map<String, UrlInterfaceDescribe> addUrlInterfaceMap;
   private Map<String, UrlInterfaceDescribe> deleteUrlInterfaceMap;
   private String type;
   
   public Map<String, UrlInterfaceDescribe> getEditUrlInterfaceMap() { return this.editUrlInterfaceMap; }
 
 
   
   public void setEditUrlInterfaceMap(Map<String, UrlInterfaceDescribe> editUrlInterfaceMap) { this.editUrlInterfaceMap = editUrlInterfaceMap; }
 
 
   
   public Map<String, UrlInterfaceDescribe> getAddUrlInterfaceMap() { return this.addUrlInterfaceMap; }
 
 
   
   public void setAddUrlInterfaceMap(Map<String, UrlInterfaceDescribe> addUrlInterfaceMap) { this.addUrlInterfaceMap = addUrlInterfaceMap; }
 
 
   
   public Map<String, UrlInterfaceDescribe> getDeleteUrlInterfaceMap() { return this.deleteUrlInterfaceMap; }
 
 
   
   public void setDeleteUrlInterfaceMap(Map<String, UrlInterfaceDescribe> deleteUrlInterfaceMap) { this.deleteUrlInterfaceMap = deleteUrlInterfaceMap; }
 
 
   
   public String getType() { return this.type; }
 
 
   
   public void setType(String type) { this.type = type; }
 }


