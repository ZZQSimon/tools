 package cn.com.easyerp.module.orion;
 
 import java.util.Date;

import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 
 
 public class OrderDetailModel
 {
   private String order_id;
   private String id;
   private String issue_no;
   private String manufacture_id;
   private String device_id;
   private String item_id;
   private Double positive;
   private Double negative;
   private Double number;
   private Double price;
   private Double amount;
   private String reception_status;
   private String distribution_satatus;
   private String pay_status;
   private String memo;
   private String cre_user;
   private Date cre_date;
   private String upd_user;
   private Date upd_date;
   
   public String getOrder_id() { return this.order_id; }
 
 
 
   
   public void setOrder_id(String order_id) { this.order_id = order_id; }
 
 
 
   
   public String getId() { return this.id; }
 
 
 
   
   public void setId(String id) { this.id = id; }
 
 
 
   
   public String getIssue_no() { return this.issue_no; }
 
 
 
   
   public void setIssue_no(String issue_no) { this.issue_no = issue_no; }
 
 
 
   
   public String getManufacture_id() { return this.manufacture_id; }
 
 
 
   
   public void setManufacture_id(String manufacture_id) { this.manufacture_id = manufacture_id; }
 
 
 
   
   public String getDevice_id() { return this.device_id; }
 
 
 
   
   public void setDevice_id(String device_id) { this.device_id = device_id; }
 
 
 
   
   public String getItem_id() { return this.item_id; }
 
 
 
   
   public void setItem_id(String item_id) { this.item_id = item_id; }
 
 
 
   
   public Double getPositive() { return this.positive; }
 
 
 
   
   public void setPositive(Double positive) { this.positive = positive; }
 
 
 
   
   public Double getNegative() { return this.negative; }
 
 
 
   
   public void setNegative(Double negative) { this.negative = negative; }
 
 
 
   
   public Double getNumber() { return this.number; }
 
 
 
   
   public void setNumber(Double number) { this.number = number; }
 
 
 
   
   public Double getPrice() { return this.price; }
 
 
 
   
   public void setPrice(Double price) { this.price = price; }
 
 
 
   
   public Double getAmount() { return this.amount; }
 
 
 
   
   public void setAmount(Double amount) { this.amount = amount; }
 
 
 
   
   public String getReception_status() { return this.reception_status; }
 
 
 
   
   public void setReception_status(String reception_status) { this.reception_status = reception_status; }
 
 
 
   
   public String getDistribution_satatus() { return this.distribution_satatus; }
 
 
 
   
   public void setDistribution_satatus(String distribution_satatus) { this.distribution_satatus = distribution_satatus; }
 
 
 
   
   public String getPay_status() { return this.pay_status; }
 
 
 
   
   public void setPay_status(String pay_status) { this.pay_status = pay_status; }
 
 
 
   
   public String getMemo() { return this.memo; }
 
 
 
   
   public void setMemo(String memo) { this.memo = memo; }
 
 
 
   
   public String getCre_user() { return this.cre_user; }
 
 
 
   
   public void setCre_user(String cre_user) { this.cre_user = cre_user; }
 
 
 
   
   public Date getCre_date() { return this.cre_date; }
 
 
 
   
   public void setCre_date(Date cre_date) { this.cre_date = cre_date; }
 
 
 
   
   public String getUpd_user() { return this.upd_user; }
 
 
 
   
   public void setUpd_user(String upd_user) { this.upd_user = upd_user; }
 
 
 
   
   public Date getUpd_date() { return this.upd_date; }
 
 
 
   
   public void setUpd_date(Date upd_date) { this.upd_date = upd_date; }
 
 
   
   private String fillLeading(String value, char leadingChar, int len) {
     if (leadingChar == '\000')
       return value; 
     return Common.rightPad(value, len, leadingChar);
   }
 
   
   public String export(OrionReportParam param) {
     String delimiter = param.getDelimiter();
     char leadingChar = param.getLeadingChar();
     return fillLeading(this.issue_no, leadingChar, 13) + delimiter + 
       fillLeading(this.order_id, leadingChar, 15) + delimiter + 
       fillLeading(this.manufacture_id, leadingChar, 8) + delimiter + 
       fillLeading(this.device_id, leadingChar, 4) + delimiter + 
       fillLeading(this.item_id, leadingChar, 30);
   }
 }


