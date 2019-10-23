/*    */ package com.g1extend.mail;
/*    */ 
/*    */ import java.util.Properties;
/*    */ import javax.mail.Authenticator;
/*    */ import javax.mail.PasswordAuthentication;
/*    */ import javax.mail.Session;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MailServer
/*    */ {
/*    */   private String from;
/*    */   private Session session;
/*    */   
/*    */   public MailServer(String host, String user, String from, String password) {
/* 21 */     this.from = from;
/*    */     
/* 23 */     Properties props = new Properties();
/* 24 */     props.put("mail.host", host);
/* 25 */     props.put("mail.user", user);
/* 26 */     props.put("mail.transport.protocol", "smtp");
/* 27 */     props.put("mail.smtp.ssl.enable", "true");
/*    */     
/* 29 */     SMTPAuthenticator authenticator = null;
/* 30 */     if (password != null && !"".equals(password)) {
/* 31 */       props.put("mail.smtp.auth", Boolean.valueOf(true));
/* 32 */       authenticator = new SMTPAuthenticator(user, password);
/*    */     } 
/* 34 */     this.session = Session.getInstance(props, authenticator);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 46 */   String getFrom() { return this.from; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 56 */   Session getSession() { return this.session; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private static class SMTPAuthenticator
/*    */     extends Authenticator
/*    */   {
/*    */     PasswordAuthentication auth;
/*    */ 
/*    */ 
/*    */     
/* 68 */     public SMTPAuthenticator(String username, String password) { this.auth = new PasswordAuthentication(username, password); }
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 73 */     public PasswordAuthentication getPasswordAuthentication() { return this.auth; }
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\mail\MailServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */