/*     */ package com.g1extend.mail;
/*     */ 
/*     */ import com.g1extend.service.ContractService;
/*     */ import com.g1extend.utils.common;

/*     */ import java.util.List;
/*     */ import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
/*     */ import javax.mail.Message;
/*     */ import javax.mail.MessagingException;
import javax.mail.Multipart;
/*     */ import javax.mail.Transport;
/*     */ import javax.mail.internet.InternetAddress;
/*     */ import javax.mail.internet.MimeBodyPart;
/*     */ import javax.mail.internet.MimeMessage;
/*     */ import javax.mail.internet.MimeMultipart;

/*     */ import org.springframework.beans.factory.annotation.Autowired;
/*     */ import org.springframework.stereotype.Service;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Service
/*     */ public class MailService
/*     */ {
/*     */   @Autowired
/*     */   private ContractService contractService;
/*     */   private MailServer server;
/*     */   private String mail_host;
/*     */   private String mail_user;
/*     */   private String mail_pwd;
/*     */   private String mail_addr;
/*     */   
/*     */   MailService() {
/*  46 */     Properties param = common.getSystemParam();
/*  47 */     this.mail_host = param.getProperty("mail_host");
/*  48 */     this.mail_user = param.getProperty("mail_user");
/*  49 */     this.mail_pwd = param.getProperty("mail_pwd");
/*  50 */     this.mail_addr = param.getProperty("mail_addr");
/*     */     
/*  52 */     this.server = new MailServer(this.mail_host, this.mail_user, this.mail_addr, this.mail_pwd);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   public void send(MailDescribe mail) { send(mail, this.server); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean send(MailDescribe mail, MailServer mailServer) {
/*     */     try {
			    final Message msg = (Message)new MimeMessage(mailServer.getSession());
			    msg.setFrom((Address)new InternetAddress(mailServer.getFrom()));
			    msg.setSubject(mail.getSubject());
			    msg.setText(mail.getBody());
			    InternetAddress[] recipients;
			    for (int length = (recipients = mail.getRecipients()).length, i = 0; i < length; ++i) {
			        final InternetAddress address = recipients[i];
			        msg.addRecipient(Message.RecipientType.TO, (Address)address);
			    }
			    final InternetAddress[] cc = mail.getCC();
			    if (cc.length > 0) {
			        InternetAddress[] array;
			        for (int length2 = (array = cc).length, j = 0; j < length2; ++j) {
			            final InternetAddress address2 = array[j];
			            msg.addRecipient(Message.RecipientType.CC, (Address)address2);
			        }
			    }
			    final List<MimeBodyPart> attachments = mail.getAttachments();
			    if (attachments.size() > 0) {
			        final Multipart mp = (Multipart)new MimeMultipart();
			        for (final MimeBodyPart attachment : attachments) {
			            mp.addBodyPart((BodyPart)attachment);
			        }
			        final MimeBodyPart body = new MimeBodyPart();
			        body.setText(mail.getBody());
			        mp.addBodyPart((BodyPart)body);
			        msg.setContent(mp);
			    }
			    Transport.send(msg);
			    return true;
			}
			catch (MessagingException e) {
			    e.printStackTrace();
			    return false;
			}
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 107 */   public boolean sendHtmlEmail(MailDescribe mail) { return sendHtmlEmail(mail, this.server); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean sendHtmlEmail(MailDescribe mail, MailServer mailServer) {
				try {
			        final Message msg = (Message)new MimeMessage(mailServer.getSession());
			        msg.setFrom((Address)new InternetAddress(mailServer.getFrom()));
			        msg.setSubject(mail.getSubject());
			        InternetAddress[] recipients;
			        for (int length = (recipients = mail.getRecipients()).length, i = 0; i < length; ++i) {
			            final InternetAddress address = recipients[i];
			            msg.addRecipient(Message.RecipientType.TO, (Address)address);
			        }
			        final InternetAddress[] cc = mail.getCC();
			        if (cc.length > 0) {
			            InternetAddress[] array;
			            for (int length2 = (array = cc).length, j = 0; j < length2; ++j) {
			                final InternetAddress address2 = array[j];
			                msg.addRecipient(Message.RecipientType.CC, (Address)address2);
			            }
			        }
			        final List<MimeBodyPart> attachments = mail.getAttachments();
			        if (attachments.size() > 0) {
			            final Multipart mp = (Multipart)new MimeMultipart();
			            for (final MimeBodyPart attachment : attachments) {
			                mp.addBodyPart((BodyPart)attachment);
			            }
			            final MimeBodyPart body = new MimeBodyPart();
			            body.setText(mail.getBody());
			            mp.addBodyPart((BodyPart)body);
			            msg.setContent(mp);
			        }
			        msg.setContent((Object)mail.getBody(), "text/html; charset=utf-8");
			        Transport.send(msg);
			        return true;
			    }
			    catch (MessagingException e) {
			        e.printStackTrace();
			        return false;
			    }
/*     */   }
/*     */ }
