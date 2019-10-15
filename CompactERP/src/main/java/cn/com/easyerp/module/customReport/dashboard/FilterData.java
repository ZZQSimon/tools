 package cn.com.easyerp.module.customReport.dashboard;
 
 import java.util.Date;
import java.util.List;
 
 
 
 public class FilterData
 {
   private Date beginDate;
   private Date endDate;
   private List<String> region;
   private List<String> store_type;
   private List<String> employee_type;
   private List<String> store;
   private List<String> grade;
   
   public Date getBeginDate() { return this.beginDate; }
 
 
   
   public void setBeginDate(Date beginDate) { this.beginDate = beginDate; }
 
 
   
   public Date getEndDate() { return this.endDate; }
 
 
   
   public void setEndDate(Date endDate) { this.endDate = endDate; }
 
 
   
   public List<String> getRegion() { return this.region; }
 
 
   
   public void setRegion(List<String> region) { this.region = region; }
 
 
   
   public List<String> getStore_type() { return this.store_type; }
 
 
   
   public void setStore_type(List<String> store_type) { this.store_type = store_type; }
 
 
   
   public List<String> getEmployee_type() { return this.employee_type; }
 
 
   
   public void setEmployee_type(List<String> employee_type) { this.employee_type = employee_type; }
 
 
   
   public List<String> getStore() { return this.store; }
 
 
   
   public void setStore(List<String> store) { this.store = store; }
 
 
   
   public List<String> getGrade() { return this.grade; }
 
 
   
   public void setGrade(List<String> grade) { this.grade = grade; }
 }


