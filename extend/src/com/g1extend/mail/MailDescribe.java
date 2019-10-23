/*     */ package com.g1extend.mail;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import javax.mail.MessagingException;
/*     */ import javax.mail.internet.AddressException;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MailDescribe
/*     */ {
/*     */   private String subject;
/*     */   private String body;
/*     */   private InternetAddress[] recipients;
/*     */   private InternetAddress[] cc;
/*     */   private List<MimeBodyPart> attachments;
/*     */   
/*     */   public MailDescribe(String subject, String body) {
/*  24 */     this.attachments = new ArrayList();
/*     */ 
/*     */ 
/*     */     
/*  28 */     this.subject = subject;
/*  29 */     this.body = body;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  34 */   public String getSubject() { return this.subject; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  39 */   public String getBody() { return this.body; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  44 */   public InternetAddress[] getRecipients() { return this.recipients; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRecipients(String string) {
/*     */     try {
/*  55 */       this.recipients = InternetAddress.parse(string);
/*  56 */     } catch (AddressException e) {
/*  57 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  63 */   public InternetAddress[] getCC() { return this.cc; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCC(String string) {
/*     */     try {
/*  74 */       this.cc = InternetAddress.parse(string);
/*  75 */     } catch (AddressException e) {
/*  76 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  82 */   public List<MimeBodyPart> getAttachments() { return this.attachments; }
/*     */ 
/*     */ 
/*     */   
/*     */   public void attachHtml(String html) {
/*     */     try {
/*  88 */       MimeBodyPart htmlPart = new MimeBodyPart();
/*  89 */       htmlPart.setContent(html, "text/html");
/*  90 */       this.attachments.add(htmlPart);
/*  91 */     } catch (MessagingException e) {
/*  92 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void attachFile(String filename) {
/*     */     try {
/*  99 */       MimeBodyPart attachment = new MimeBodyPart();
/* 100 */       attachment.attachFile(filename);
/* 101 */       this.attachments.add(attachment);
/* 102 */     } catch (MessagingException e) {
/* 103 */       e.printStackTrace();
/* 104 */     } catch (IOException e) {
/* 105 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\mail\MailDescribe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */