package cn.com.easyerp.email;

import java.net.PasswordAuthentication;
import java.util.Date;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

import org.apache.tomcat.jni.Address;

public class SendEmail
{
    public static final String HOST = "smtp.163.com";
    public static final String PROTOCOL = "smtp";
    public static final int PORT = 25;
    public static final String FROM = "h9_ting@163.com";
    public static final String PWD = "h260415.";
    
    private static Session getSession() {
        final Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.store.protocol", "smtp");
        props.put("mail.smtp.port", 25);
        props.put("mail.smtp.auth", true);
        final Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("h9_ting@163.com", "h260415.");
            }
        };
        final Session session = Session.getDefaultInstance(props, authenticator);
        return session;
    }
    
    public static void send(final String toEmail, final String content) {
        final Session session = getSession();
        try {
            System.out.println("--send--" + content);
            final Message msg = (Message)new MimeMessage(session);
            msg.setFrom((Address)new InternetAddress("h9_ting@163.com"));
            final InternetAddress[] address = { new InternetAddress(toEmail) };
            msg.setRecipients(Message.RecipientType.TO, (Address[])address);
            msg.setSubject("账号激活邮件");
            msg.setSentDate(new Date());
            msg.setContent((Object)content, "text/html;charset=utf-8");
            Transport.send(msg);
        }
        catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }
}
