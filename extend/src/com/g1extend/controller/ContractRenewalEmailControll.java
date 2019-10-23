/*    */ package com.g1extend.controller;
/*    */ 
/*    */ import com.g1extend.entity.ContractRenewal;
/*    */ import com.g1extend.entity.EmailTemplate;
/*    */ import com.g1extend.mail.MailDescribe;
/*    */ import com.g1extend.mail.MailService;
/*    */ import com.g1extend.service.ContractRenewalService;
/*    */ import java.util.List;
/*    */ import org.springframework.beans.factory.annotation.Autowired;
/*    */ import org.springframework.stereotype.Controller;
/*    */ import org.springframework.web.bind.annotation.RequestMapping;
/*    */ import org.springframework.web.bind.annotation.RequestParam;
/*    */ import org.springframework.web.bind.annotation.ResponseBody;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @RequestMapping({"/contractRenewalEmail"})
/*    */ public class ContractRenewalEmailControll
/*    */ {
/*    */   @Autowired
/*    */   private ContractRenewalService contractRenewalService;
/*    */   @Autowired
/*    */   private MailService mailService;
/*    */   
/*    */   @RequestMapping({"/contractRenewal"})
/*    */   @ResponseBody
/*    */   public void contractRenewal(@RequestParam(value = "dataId", required = false) String dataId, @RequestParam(value = "empId", required = false) String empId) {
/*    */     try {
/* 32 */       Thread.sleep(10000L);
/* 33 */     } catch (InterruptedException e) {
/*    */       
/* 35 */       e.printStackTrace();
/*    */     } 
/*    */     
/* 38 */     String approveId = this.contractRenewalService.getApproveId(dataId);
/* 39 */     String distinctCount = this.contractRenewalService.getDistinctCount(dataId);
/* 40 */     String dount = this.contractRenewalService.getCount(dataId);
/* 41 */     if (!distinctCount.equals(dount)) {
/*    */ 
/*    */       
/* 44 */       EmailTemplate emailTemplate = this.contractRenewalService.getEmailTemplate();
/* 45 */       String empName = this.contractRenewalService.getEmpName(empId);
/* 46 */       StringBuilder body = new StringBuilder();
/* 47 */       List<ContractRenewal> allUser = this.contractRenewalService.getAllUser(approveId);
/* 48 */       for (int i = 0; i < allUser.size(); i++) {
/* 49 */         if (!"".equals(allUser.get(i)) && ((ContractRenewal)allUser.get(i)).getInternational_id() != null) {
/* 50 */           String name = this.contractRenewalService.getEmpName(((ContractRenewal)allUser.get(i)).getUser());
/* 51 */           String info = "不同意";
/* 52 */           if (((ContractRenewal)allUser.get(i)).getInternational_id().equals("agreeevent_button_xuqian")) {
/* 53 */             info = "同意续签";
/* 54 */           } else if (((ContractRenewal)allUser.get(i)).getInternational_id().equals("agreeevent_button_shunyan")) {
/* 55 */             info = "同意顺延";
/*    */           } 
/* 57 */           body.append(String.valueOf(name) + "：" + info + "\n");
/*    */         } 
/*    */       } 
/* 60 */       String template = emailTemplate.getTemplate().replace("{emp_id}", empName).replace("{approve}", body.toString());
/* 61 */       for (int i = 0; i < allUser.size(); i++) {
/*    */         
/* 63 */         String email = this.contractRenewalService.getEmail(((ContractRenewal)allUser.get(i)).getUser());
/* 64 */         System.out.println("邮件发送成功" + ((ContractRenewal)allUser.get(i)).getUser() + email);
/*    */         
/* 66 */         MailDescribe mail = new MailDescribe(emailTemplate.getTitle(), template);
/* 67 */         mail.setRecipients(email);
/* 68 */         mail.setCC("");
/* 69 */         this.mailService.send(mail);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\controller\ContractRenewalEmailControll.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */