 package cn.com.easyerp.core.mail;
 
 import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;

import cn.com.easyerp.core.exception.ApplicationException;
 
 
 
 
 
 
 
 public class MailDescribe
 {
   private String subject;
   private String body;
   private InternetAddress[] recipients;
   private InternetAddress[] cc;
   private List<MimeBodyPart> attachments = new ArrayList<>();
 
   
   public MailDescribe(String subject, String body) {
     this.subject = subject;
     this.body = body;
   }
 
 
   
   public String getSubject() { return this.subject; }
 
 
 
   
   public String getBody() { return this.body; }
 
 
 
   
   public InternetAddress[] getRecipients() { return this.recipients; }
 
 
 
 
 
 
 
   
   public void setRecipients(String string) {
     try {
       this.recipients = InternetAddress.parse(string);
     } catch (AddressException e) {
       e.printStackTrace();
     } 
   }
 
 
   
   public InternetAddress[] getCC() { return this.cc; }
 
 
 
 
 
 
 
   
   public void setCC(String string) {
     try {
       this.cc = InternetAddress.parse(string);
     } catch (AddressException e) {
       e.printStackTrace();
     } 
   }
 
 
   
   public List<MimeBodyPart> getAttachments() { return this.attachments; }
 
 
   
   public void attachHtml(String html) {
     try {
       MimeBodyPart htmlPart = new MimeBodyPart();
       htmlPart.setContent(html, "text/html");
       this.attachments.add(htmlPart);
     } catch (MessagingException e) {
       throw new ApplicationException(e);
     } 
   }
 
   
   public void attachFile(String filename) {
     try {
       MimeBodyPart attachment = new MimeBodyPart();
       attachment.attachFile(filename);
       this.attachments.add(attachment);
     } catch (MessagingException e) {
       throw new ApplicationException(e);
     } catch (IOException e) {
       e.printStackTrace();
     } 
   }
 }


