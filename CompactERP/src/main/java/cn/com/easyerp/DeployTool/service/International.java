 package cn.com.easyerp.DeployTool.service;
 
 import java.util.Date;
 
 public class International
 {
   private String international_id;
   private int type;
   private String cn;
   private String en;
   private String jp;
   private String other1;
   private String other2;
   private String module;
   private Date cre_date;
   
   public Date getCre_date() { return this.cre_date; }
 
   
   public void setCre_date(Date cre_date) { this.cre_date = cre_date; }
 
   
   public String getInternational_id() { return this.international_id; }
 
   
   public void setInternational_id(String international_id) { this.international_id = international_id; }
 
   
   public int getType() { return this.type; }
 
   
   public void setType(int type) { this.type = type; }
 
   
   public String getCn() { return this.cn; }
 
   
   public void setCn(String cn) { this.cn = cn; }
 
   
   public String getEn() { return this.en; }
 
   
   public void setEn(String en) { this.en = en; }
 
   
   public String getJp() { return this.jp; }
 
   
   public void setJp(String jp) { this.jp = jp; }
 
   
   public String getOther1() { return this.other1; }
 
   
   public void setOther1(String other1) { this.other1 = other1; }
 
   
   public String getOther2() { return this.other2; }
 
   
   public void setOther2(String other2) { this.other2 = other2; }
 
   
   public String getModule() { return this.module; }
 
   
   public void setModule(String module) { this.module = module; }
 }


