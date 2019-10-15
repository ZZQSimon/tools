 package cn.com.easyerp.DeployTool.view;
 
 import java.util.List;

import cn.com.easyerp.DeployTool.service.International;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 
 public class InternationalRequestModel
   extends FormRequestModelBase<InternationalModel>
 {
   private List<International> insert;
   private List<International> update;
   private List<International> deleted;
   private String params;
   private String retrieveValue;
   private String findContext;
   private String replaceWith;
   private String scope;
   private int pageNumber;
   private int pageSize;
   
   public String getRetrieveValue() { return this.retrieveValue; }
 
   
   public void setRetrieveValue(String retrieveValue) { this.retrieveValue = retrieveValue; }
 
   
   public String getParams() { return this.params; }
 
   
   public void setParams(String params) { this.params = params; }
 
   
   public List<International> getInsert() { return this.insert; }
 
   
   public void setInsert(List<International> insert) { this.insert = insert; }
 
   
   public List<International> getUpdate() { return this.update; }
 
   
   public void setUpdate(List<International> update) { this.update = update; }
 
   
   public List<International> getDeleted() { return this.deleted; }
 
   
   public void setDeleted(List<International> deleted) { this.deleted = deleted; }
 
   
   public String getFindContext() { return this.findContext; }
 
   
   public void setFindContext(String findContext) { this.findContext = findContext; }
 
   
   public String getReplaceWith() { return this.replaceWith; }
 
   
   public void setReplaceWith(String replaceWith) { this.replaceWith = replaceWith; }
 
   
   public String getScope() { return this.scope; }
 
   
   public void setScope(String scope) { this.scope = scope; }
 
   
   public int getPageNumber() { return this.pageNumber; }
 
   
   public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
 
   
   public int getPageSize() { return this.pageSize; }
 
   
   public void setPageSize(int pageSize) { this.pageSize = pageSize; }
 }


