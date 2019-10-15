 package cn.com.easyerp.DeployTool.service;

public class SubscribeDescribe
 {
   private String subscribe_id;
   private String dashboard_id;
   private String subscribe_status;
   private String subscriber;
   private String seq;
   
   public SubscribeDescribe() { this.subscribe_id = getUUID(); }
 
 
   
   public String getSubscribe_id() { return this.subscribe_id; }
 
   
   public void setSubscribe_id(String subscribe_id) { this.subscribe_id = subscribe_id; }
 
   
   public String getDashboard_id() { return this.dashboard_id; }
 
   
   public void setDashboard_id(String dashboard_id) { this.dashboard_id = dashboard_id; }
 
   
   public String getSubscribe_status() { return this.subscribe_status; }
 
   
   public void setSubscribe_status(String subscribe_status) { this.subscribe_status = subscribe_status; }
 
   
   public String getSubscriber() { return this.subscriber; }
 
   
   public void setSubscriber(String subscriber) { this.subscriber = subscriber; }
 
 
   
   private String getUUID() { return Long.toHexString(System.currentTimeMillis()); }
 
 
   
   public String getSeq() { return this.seq; }
 
 
   
   public void setSeq(String seq) { this.seq = seq; }
 }


