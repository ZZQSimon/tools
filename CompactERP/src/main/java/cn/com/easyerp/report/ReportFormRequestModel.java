 package cn.com.easyerp.report;
 
 import java.util.Map;

import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 
 public class ReportFormRequestModel
   extends FormRequestModelBase
 {
   private String report_id;
   private FilterRequestModel filter;
   private Map<String, Object> filterParam;
   
   public String getReport_id() { return this.report_id; }
 
 
 
   
   public void setReport_id(String report_id) { this.report_id = report_id; }
 
 
 
   
   public FilterRequestModel getFilter() { return this.filter; }
 
 
 
   
   public void setFilter(FilterRequestModel filter) { this.filter = filter; }
 
 
   
   public Map<String, Object> getFilterParam() { return this.filterParam; }
 
 
   
   public void setFilterParam(Map<String, Object> filterParam) { this.filterParam = filterParam; }
 }


