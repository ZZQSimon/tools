/*     */ package com.g1extend.entity;
/*     */ 
/*     */ import java.util.Date;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Contract_renewal
/*     */ {
/*     */   private String id;
/*     */   private String approve_status;
/*     */   private String area;
/*     */   private String city;
/*     */   private String emp_id;
/*     */   private String emp_name;
/*     */   private String rank;
/*     */   private String position;
/*     */   private String shop_id;
/*     */   private String shop_name;
/*     */   private String area_manager;
/*     */   private Date old_begin_date;
/*     */   private Date old_end_date;
/*     */   private String old_work_hour_type;
/*     */   private Date renew_date;
/*     */   private String renew_type;
/*     */   private Date new_begin_date;
/*     */   private Date new_end_date;
/*     */   private String new_work_hour_type;
/*     */   private String cre_user;
/*     */   private Date cre_date;
/*     */   private String upd_user;
/*     */   private Date upd_date;
/*     */   private String owner;
/*     */   
/*  39 */   public String getShop_name() { return this.shop_name; }
/*     */ 
/*     */   
/*  42 */   public void setShop_name(String shop_name) { this.shop_name = shop_name; }
/*     */ 
/*     */   
/*  45 */   public String getArea() { return this.area; }
/*     */ 
/*     */   
/*  48 */   public void setArea(String area) { this.area = area; }
/*     */ 
/*     */   
/*  51 */   public String getOld_work_hour_type() { return this.old_work_hour_type; }
/*     */ 
/*     */   
/*  54 */   public void setOld_work_hour_type(String old_work_hour_type) { this.old_work_hour_type = old_work_hour_type; }
/*     */ 
/*     */   
/*  57 */   public String getNew_work_hour_type() { return this.new_work_hour_type; }
/*     */ 
/*     */   
/*  60 */   public void setNew_work_hour_type(String new_work_hour_type) { this.new_work_hour_type = new_work_hour_type; }
/*     */ 
/*     */   
/*  63 */   public String getId() { return this.id; }
/*     */ 
/*     */   
/*  66 */   public void setId(String id) { this.id = id; }
/*     */ 
/*     */   
/*  69 */   public String getEmp_name() { return this.emp_name; }
/*     */ 
/*     */   
/*  72 */   public void setEmp_name(String emp_name) { this.emp_name = emp_name; }
/*     */ 
/*     */   
/*  75 */   public String getEmp_id() { return this.emp_id; }
/*     */ 
/*     */   
/*  78 */   public void setEmp_id(String emp_id) { this.emp_id = emp_id; }
/*     */ 
/*     */   
/*  81 */   public Date getRenew_date() { return this.renew_date; }
/*     */ 
/*     */   
/*  84 */   public void setRenew_date(Date renew_date) { this.renew_date = renew_date; }
/*     */ 
/*     */   
/*  87 */   public String getApprove_status() { return this.approve_status; }
/*     */ 
/*     */   
/*  90 */   public void setApprove_status(String approve_status) { this.approve_status = approve_status; }
/*     */ 
/*     */   
/*  93 */   public Date getOld_begin_date() { return this.old_begin_date; }
/*     */ 
/*     */   
/*  96 */   public void setOld_begin_date(Date old_begin_date) { this.old_begin_date = old_begin_date; }
/*     */ 
/*     */   
/*  99 */   public Date getOld_end_date() { return this.old_end_date; }
/*     */ 
/*     */   
/* 102 */   public void setOld_end_date(Date old_end_date) { this.old_end_date = old_end_date; }
/*     */ 
/*     */   
/* 105 */   public String getRenew_type() { return this.renew_type; }
/*     */ 
/*     */   
/* 108 */   public void setRenew_type(String renew_type) { this.renew_type = renew_type; }
/*     */ 
/*     */   
/* 111 */   public Date getNew_begin_date() { return this.new_begin_date; }
/*     */ 
/*     */   
/* 114 */   public void setNew_begin_date(Date new_begin_date) { this.new_begin_date = new_begin_date; }
/*     */ 
/*     */   
/* 117 */   public Date getNew_end_date() { return this.new_end_date; }
/*     */ 
/*     */   
/* 120 */   public void setNew_end_date(Date new_end_date) { this.new_end_date = new_end_date; }
/*     */ 
/*     */   
/* 123 */   public String getCity() { return this.city; }
/*     */ 
/*     */   
/* 126 */   public void setCity(String city) { this.city = city; }
/*     */ 
/*     */   
/* 129 */   public String getRank() { return this.rank; }
/*     */ 
/*     */   
/* 132 */   public void setRank(String rank) { this.rank = rank; }
/*     */ 
/*     */   
/* 135 */   public String getPosition() { return this.position; }
/*     */ 
/*     */   
/* 138 */   public void setPosition(String position) { this.position = position; }
/*     */ 
/*     */   
/* 141 */   public String getShop_id() { return this.shop_id; }
/*     */ 
/*     */   
/* 144 */   public void setShop_id(String shop_id) { this.shop_id = shop_id; }
/*     */ 
/*     */   
/* 147 */   public String getArea_manager() { return this.area_manager; }
/*     */ 
/*     */   
/* 150 */   public void setArea_manager(String area_manager) { this.area_manager = area_manager; }
/*     */ 
/*     */   
/* 153 */   public String getCre_user() { return this.cre_user; }
/*     */ 
/*     */   
/* 156 */   public void setCre_user(String cre_user) { this.cre_user = cre_user; }
/*     */ 
/*     */   
/* 159 */   public Date getCre_date() { return this.cre_date; }
/*     */ 
/*     */   
/* 162 */   public void setCre_date(Date cre_date) { this.cre_date = cre_date; }
/*     */ 
/*     */   
/* 165 */   public String getUpd_user() { return this.upd_user; }
/*     */ 
/*     */   
/* 168 */   public void setUpd_user(String upd_user) { this.upd_user = upd_user; }
/*     */ 
/*     */   
/* 171 */   public Date getUpd_date() { return this.upd_date; }
/*     */ 
/*     */   
/* 174 */   public void setUpd_date(Date upd_date) { this.upd_date = upd_date; }
/*     */ 
/*     */   
/* 177 */   public String getOwner() { return this.owner; }
/*     */ 
/*     */   
/* 180 */   public void setOwner(String owner) { this.owner = owner; }
/*     */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\entity\Contract_renewal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */