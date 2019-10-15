 package cn.com.easyerp.core.mail;
 
 import java.net.PasswordAuthentication;
import java.util.Properties;

import cn.com.easyerp.framework.common.Common;
 
public class MailServer {
	class SMTPAuthenticator extends Authenticator {
		PasswordAuthentication auth;
		public SMTPAuthenticator(String username, String password) { 
			this.auth = new PasswordAuthentication(username, password); 
		}
		public PasswordAuthentication getPasswordAuthentication() { 
			return this.auth;
		}
	}
   private String from;
   private Session session;
   
   public MailServer(String host, String user, String from, String password) {
     this.from = from;
     
     Properties props = new Properties();
     props.put("mail.host", host);
     props.put("mail.user", user);
     props.put("mail.transport.protocol", "smtp");
     props.put("mail.smtp.ssl.enable", "true");
     SMTPAuthenticator authenticator = null;
     if (Common.notBlank(password)) {
       props.put("mail.smtp.auth", Boolean.valueOf(true));
       authenticator = new SMTPAuthenticator(user, password);
     } 
     this.session = Session.getInstance(props, authenticator);
   }
 
 
 
 
 
 
 
   
   public static void main(String[] args) {
     MailServer mailServer = new MailServer("localhost", "dx@dx.com", "dx@dx.com", "1");
     
     MailDescribe mail = new MailDescribe("test", "test body");
     mail.setRecipients("zl<zl@dx.com>, wbf@dx.com ");
     mail.setCC("hyn@dx.com");
     
     mail.attachFile("/home/zl/works/tmp/a.jpg");
     
     MailService mailService = new MailService();
     mailService.send(mail, mailServer);
   }
 
 
 
 
 
 
 
   
   String getFrom() { return this.from; }
 
 
 
 
 
 
 
 
   
   Session getSession() { return this.session; }
 }


