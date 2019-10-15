 package cn.com.easyerp.DeployTool.service;
 
 import cn.com.easyerp.auth.AuthService;
 
 public class ViewReportDetails
 {
   private String id;
   private String table_id;
   private int report_type;
   private String report_column;
   private String report_column_name;
   private String group_column;
   private String date_column;
   private String condition;
   private String sql;
   private String sql2;
   private String file_name;
   private int report_file_type;
   private int report_disp_type;
   private String pre_api_json;
   private String api_json;
   private String service_name;
   private String service_param;
   private String module;
   private String international_id;
   private int type;
   private String cn;
   private String en;
   private String jp;
   private String other1;
   private String other2;
   private String report_name;
   
   public String getInternational_id() { return this.international_id; }
 
   
   public void setInternational_id(String international_id) { this.international_id = international_id; }
 
   
   public int getType() { return this.type; }
 
   
   public void setType(int type) { this.type = type; }
 
   
   public String getCn() { return this.cn; }
   
   public void setCn(String cn) {
     if (AuthService.getCurrentUser().getLanguage_id().equals("cn")) {
       this.report_name = cn;
     }
     this.cn = cn;
   }
   
   public String getEn() { return this.en; }
   
   public void setEn(String en) {
     if (AuthService.getCurrentUser().getLanguage_id().equals("en")) {
       setReport_column(en);
     }
     this.en = en;
   }
   
   public String getJp() { return this.jp; }
   
   public void setJp(String jp) {
     if (AuthService.getCurrentUser().getLanguage_id().equals("jp")) {
       setReport_column(jp);
     }
     this.jp = jp;
   }
   
   public String getOther1() { return this.other1; }
   
   public void setOther1(String other1) {
     if (AuthService.getCurrentUser().getLanguage_id().equals("other1")) {
       setReport_column(other1);
     }
     this.other1 = other1;
   }
   
   public String getOther2() { return this.other2; }
   
   public void setOther2(String other2) {
     if (AuthService.getCurrentUser().getLanguage_id().equals("other2")) {
       setReport_column(other2);
     }
     this.other2 = other2;
   }
   
   public String getReport_name() { return this.report_name; }
 
   
   public void setReport_name(String report_name) { this.report_name = report_name; }
 
   
   public int getReport_type() { return this.report_type; }
 
   
   public void setReport_type(int report_type) { this.report_type = report_type; }
 
   
   public int getReport_file_type() { return this.report_file_type; }
 
   
   public void setReport_file_type(int report_file_type) { this.report_file_type = report_file_type; }
 
   
   public int getReport_disp_type() { return this.report_disp_type; }
 
   
   public void setReport_disp_type(int report_disp_type) { this.report_disp_type = report_disp_type; }
 
   
   public String getId() { return this.id; }
 
   
   public void setId(String id) { this.id = id; }
 
   
   public String getTable_id() { return this.table_id; }
 
   
   public void setTable_id(String table_id) { this.table_id = table_id; }
 
   
   public String getReport_column() { return this.report_column; }
 
   
   public void setReport_column(String report_column) { this.report_column = report_column; }
 
   
   public String getReport_column_name() { return this.report_column_name; }
 
   
   public void setReport_column_name(String report_column_name) { this.report_column_name = report_column_name; }
 
   
   public String getGroup_column() { return this.group_column; }
 
   
   public void setGroup_column(String group_column) { this.group_column = group_column; }
 
   
   public String getDate_column() { return this.date_column; }
 
   
   public void setDate_column(String date_column) { this.date_column = date_column; }
 
   
   public String getCondition() { return this.condition; }
 
   
   public void setCondition(String condition) { this.condition = condition; }
 
 
   
   public String getSql() { return this.sql; }
 
   
   public void setSql(String sql) { this.sql = sql; }
 
   
   public String getSql2() { return this.sql2; }
 
   
   public void setSql2(String sql2) { this.sql2 = sql2; }
 
   
   public String getFile_name() { return this.file_name; }
 
   
   public void setFile_name(String file_name) { this.file_name = file_name; }
 
   
   public String getPre_api_json() { return this.pre_api_json; }
 
   
   public void setPre_api_json(String pre_api_json) { this.pre_api_json = pre_api_json; }
 
   
   public String getApi_json() { return this.api_json; }
 
   
   public void setApi_json(String api_json) { this.api_json = api_json; }
 
   
   public String getService_name() { return this.service_name; }
 
   
   public void setService_name(String service_name) { this.service_name = service_name; }
 
   
   public String getService_param() { return this.service_param; }
 
   
   public void setService_param(String service_param) { this.service_param = service_param; }
 
   
   public String getModule() { return this.module; }
 
   
   public void setModule(String module) { this.module = module; }
 }


