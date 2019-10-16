package cn.com.easyerp.core.mail;

import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.DxCache;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.master.DxRoutingDataSource;

@Service
public class MailService {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private SystemDao systemDao;
    // @Autowired
    // private AuthService authService;

    public boolean send(MailDescribe mail) {
        String currentDomainKey = this.dxRoutingDataSource.getDomainKey();
        DxCache cache = (DxCache) this.cacheService.getDxCache().get(currentDomainKey);
        if (cache == null) {
            SystemParameter param = this.systemDao.selectSystemParam_master();
            MailServer mailServer = new MailServer(param.getMail_host(), param.getFtp_user(), param.getMail_addr(),
                    param.getMail_pwd());
            return send(mail, mailServer);
        }
        return send(mail, this.cacheService.getMailServer());
    }

    public boolean send(MailDescribe mail, MailServer mailServer) {
        try {
            MimeMessage mimeMessage = new MimeMessage(mailServer.getSession());
            mimeMessage.setFrom(new InternetAddress(mailServer.getFrom()));
            mimeMessage.setSubject(mail.getSubject());
            mimeMessage.setText(mail.getBody());
            for (InternetAddress address : mail.getRecipients()) {
                mimeMessage.addRecipient(Message.RecipientType.TO, address);
            }
            InternetAddress[] cc = mail.getCC();
            if (cc.length > 0)
                for (InternetAddress address : cc) {
                    mimeMessage.addRecipient(Message.RecipientType.CC, address);
                }
            List<MimeBodyPart> attachments = mail.getAttachments();
            if (attachments.size() > 0) {
                MimeMultipart mimeMultipart = new MimeMultipart();
                for (MimeBodyPart attachment : attachments)
                    mimeMultipart.addBodyPart(attachment);
                MimeBodyPart body = new MimeBodyPart();
                body.setText(mail.getBody());
                mimeMultipart.addBodyPart(body);
                mimeMessage.setContent(mimeMultipart);
            }

            Transport.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendHtmlEmail(MailDescribe mail) {
        String currentDomainKey = this.dxRoutingDataSource.getDomainKey();
        DxCache cache = (DxCache) this.cacheService.getDxCache().get(currentDomainKey);
        if (cache == null) {
            SystemParameter param = this.systemDao.selectSystemParam_master();
            MailServer mailServer = new MailServer(param.getMail_host(), param.getMail_user(), param.getMail_addr(),
                    param.getMail_pwd());
            return sendHtmlEmail(mail, mailServer);
        }
        return sendHtmlEmail(mail, this.cacheService.getMailServer());
    }

    public boolean sendHtmlEmail(MailDescribe mail, MailServer mailServer) {
        try {
            MimeMessage mimeMessage = new MimeMessage(mailServer.getSession());
            mimeMessage.setFrom(new InternetAddress(mailServer.getFrom()));
            mimeMessage.setSubject(mail.getSubject());

            for (InternetAddress address : mail.getRecipients()) {
                mimeMessage.addRecipient(Message.RecipientType.TO, address);
            }
            InternetAddress[] cc = mail.getCC();
            if (cc.length > 0)
                for (InternetAddress address : cc) {
                    mimeMessage.addRecipient(Message.RecipientType.CC, address);
                }
            List<MimeBodyPart> attachments = mail.getAttachments();
            if (attachments.size() > 0) {
                MimeMultipart mimeMultipart = new MimeMultipart();
                for (MimeBodyPart attachment : attachments)
                    mimeMultipart.addBodyPart(attachment);
                MimeBodyPart body = new MimeBodyPart();
                body.setText(mail.getBody());
                mimeMultipart.addBodyPart(body);
                mimeMessage.setContent(mimeMultipart);
            }

            mimeMessage.setContent(mail.getBody(), "text/html; charset=utf-8");
            Transport.send(mimeMessage);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
