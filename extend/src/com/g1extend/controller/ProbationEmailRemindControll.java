/*    */ package com.g1extend.controller;
/*    */ 
/*    */ import com.g1extend.entity.EmailTemplate;
/*    */ import com.g1extend.entity.ProbationExpire;
/*    */ import com.g1extend.mail.MailDescribe;
/*    */ import com.g1extend.mail.MailService;
/*    */ import com.g1extend.service.ProbationRemindService;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Date;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
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
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @RequestMapping({"/probationEmailRemind"})
/*    */ public class ProbationEmailRemindControll
/*    */ {
/*    */   @Autowired
/*    */   private ProbationRemindService probationRemindService;
/*    */   @Autowired
/*    */   private MailService mailService;
/*    */   
/*    */   @RequestMapping({"/probationRemind"})
/*    */   @ResponseBody
/*    */   public void probationRemind(@RequestParam(value = "getDate", required = false) String getDate) {
/* 39 */     SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
/* 40 */     Date date = new Date();
/*    */     try {
/* 42 */       if (getDate != null && !"".equals(getDate)) {
/* 43 */         date = formatDate.parse(getDate);
/*    */       }
/* 45 */     } catch (ParseException e) {
/*    */       
/* 47 */       e.printStackTrace();
/*    */     } 
/*    */ 
/*    */     
/* 51 */     List<ProbationExpire> probationExpireList = this.probationRemindService.getALLProbationExpire(date);
/* 52 */     Map<String, List<ProbationExpire>> probationExpireMap = new HashMap<String, List<ProbationExpire>>();
/* 53 */     for (ProbationExpire probationExpire : probationExpireList) {
/* 54 */       if (probationExpireMap.get(probationExpire.getDirect_supervisor()) == null) {
/* 55 */         List<ProbationExpire> probationExpireNew = new ArrayList<ProbationExpire>();
/* 56 */         probationExpireMap.put(probationExpire.getDirect_supervisor(), probationExpireNew);
/*    */       } 
/* 58 */       ((List)probationExpireMap.get(probationExpire.getDirect_supervisor())).add(probationExpire);
/*    */     } 
/*    */     
/* 61 */     for (Map.Entry<String, List<ProbationExpire>> probationExpireEntry : probationExpireMap.entrySet()) {
/*    */       
/* 63 */       String email = this.probationRemindService.getDirectSupervisorEmail((String)probationExpireEntry.getKey());
/*    */       
/* 65 */       EmailTemplate emailTemplate = this.probationRemindService.getEmailTemplate();
/* 66 */       StringBuilder body = new StringBuilder();
/* 67 */       List<ProbationExpire> probationExpire = (List)probationExpireEntry.getValue();
/* 68 */       for (ProbationExpire probationExpire2 : probationExpire) {
/* 69 */         body.append(String.valueOf(probationExpire2.getName()) + "\t\t转正日期：" + probationExpire2.getPositive() + "\n");
/*    */       }
/* 71 */       String template = emailTemplate.getTemplate().replace("{StaffList}", body.toString());
/*    */       
/* 73 */       MailDescribe mail = new MailDescribe(emailTemplate.getTitle(), template);
/* 74 */       mail.setRecipients(email);
/* 75 */       mail.setCC("");
/* 76 */       this.mailService.send(mail);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\controller\ProbationEmailRemindControll.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */