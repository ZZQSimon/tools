 package cn.com.easyerp.DeployTool.view;
 
 import java.util.List;

import cn.com.easyerp.DeployTool.service.DashboardDescribe;
import cn.com.easyerp.DeployTool.service.SubscribeDescribe;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 public class DashboardRequestModel
   extends FormRequestModelBase<DashboardModel> {
   private DashboardDescribe dashboard;
   private String report_id;
   private List<SubscribeDescribe> subscribes;
   private String condition;
   private int subscribe_status;
   private String orderBy;
   private int defaultCount;
   private String table;
   
   public DashboardDescribe getDashboard() { return this.dashboard; }
 
 
   
   public void setDashboard(DashboardDescribe dashboard) { this.dashboard = dashboard; }
 
 
   
   public String getReport_id() { return this.report_id; }
 
 
   
   public void setReport_id(String report_id) { this.report_id = report_id; }
 
 
   
   public List<SubscribeDescribe> getSubscribes() { return this.subscribes; }
 
 
   
   public void setSubscribes(List<SubscribeDescribe> subscribes) { this.subscribes = subscribes; }
 
 
   
   public String getCondition() { return this.condition; }
 
 
   
   public void setCondition(String condition) { this.condition = condition; }
 
 
   
   public int getSubscribe_status() { return this.subscribe_status; }
 
 
   
   public void setSubscribe_status(int subscribe_status) { this.subscribe_status = subscribe_status; }
 
 
   
   public String getOrderBy() { return this.orderBy; }
 
 
   
   public void setOrderBy(String orderBy) { this.orderBy = orderBy; }
 
 
   
   public int getDefaultCount() { return this.defaultCount; }
 
 
   
   public void setDefaultCount(int defaultCount) { this.defaultCount = defaultCount; }
 
 
   
   public String getTable() { return this.table; }
 
 
   
   public void setTable(String table) { this.table = table; }
 }


