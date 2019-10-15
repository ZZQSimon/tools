 package cn.com.easyerp.module.importer;
 
 import java.util.List;
import java.util.Map;
 
 
 
 
 
 
 public class CustomImportResult
 {
   private List<ImportRowModel> dataList;
   private List<String> status;
   private Map<String, CustomImportHeader> headers;
   private List<CustomImportDetail> details;
   
   public List<ImportRowModel> getDataList() { return this.dataList; }
 
 
 
   
   public void setDataList(List<ImportRowModel> dataList) { this.dataList = dataList; }
 
 
 
   
   public List<String> getStatus() { return this.status; }
 
 
 
   
   public void setStatus(List<String> status) { this.status = status; }
 
 
 
   
   public Map<String, CustomImportHeader> getHeaders() { return this.headers; }
 
 
 
   
   public void setHeaders(Map<String, CustomImportHeader> headers) { this.headers = headers; }
 
 
 
   
   public List<CustomImportDetail> getDetails() { return this.details; }
 
 
 
   
   public void setDetails(List<CustomImportDetail> details) { this.details = details; }
 }


