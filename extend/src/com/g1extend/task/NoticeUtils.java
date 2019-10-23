package com.g1extend.task;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.net.ssl.SSLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.http.MethodType;
import com.g1extend.entity.Notice;
import com.g1extend.expection.ApplicationException;
import com.g1extend.mail.MailDescribe;
import com.g1extend.mail.MailService;
import com.g1extend.service.NoticeService;
import com.g1extend.service.SmsService;
import com.g1extend.utils.HttpClientUtil;
import com.g1extend.utils.common;

public class NoticeUtils {
    private static Log log = LogFactory.getLog(NoticeUtils.class);

    /** 最高尝试发送次数 */
    public static final int MAX_COUNTS = 5;
    /** 发送状态 已发送 1 */
    public static final int SENT = 1;
    /** 发送状态 未发送 0 */
    public static final int UNSENT = 0;
    /** 通知类型 邮件 01 */
    public static final String T_MAIL = "01";
    /** 通知类型 短信 02 */
    public static final String T_SMS = "02";

    public static final String SMS = "SMS";
    public static final String MAIL = "MAIL";
    public static final String ALL = "ALL";

    public static void saveSentSMS(final NoticeService noticeService, Notice notice) {
        notice.setNotice_type(T_SMS);
        notice.setIs_sent(SENT);
        notice.setSent_time(NoticeLog.now());
        createAndSave(noticeService, notice);
    }

    public static void saveUnsentSMS(final NoticeService noticeService, Notice notice) {
        notice.setNotice_type(T_SMS);
        notice.setIs_sent(UNSENT);
        notice.setSent_time("");
        createAndSave(noticeService, notice);
    }

    public static void saveSentMAIL(final NoticeService noticeService, Notice notice) {
        notice.setNotice_type(T_MAIL);
        notice.setIs_sent(SENT);
        notice.setSent_time(NoticeLog.now());
        createAndSave(noticeService, notice);
    }

    public static void saveUnsentMAIL(final NoticeService noticeService, Notice notice) {
        notice.setNotice_type(T_MAIL);
        notice.setIs_sent(UNSENT);
        notice.setSent_time("");
        createAndSave(noticeService, notice);
    }

    public static void createAndSave(final NoticeService noticeService, Notice notice) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        notice.setId(id);
        notice.setSent_counts(1);
        notice.setCreate_time(NoticeLog.now());
        noticeService.save(notice);
    }

    public static String failedReason(final int sentSerial, final String reason) {
        return MessageFormat.format("{0}. {1} \n", sentSerial, reason);
    }

    public static String genLink(final String link, final String data, final String key) {
        StringBuffer sb = new StringBuffer(link);
        sb.append("?data=");
        sb.append(data);
        sb.append("&domain=");
        sb.append(key);
        return sb.toString();
    }

    public static boolean sendSMS(final String telephone, final String smsMessage, Properties sys_param,
            SmsService smsService) {
        return sendSMS(telephone, smsMessage, sys_param.getProperty("sms_sign"), sys_param.getProperty("sms_entry_id"),
                smsService);
    }

    public static void updateSMS(final boolean resentFlag, final int sentSerial, final NoticeService noticeService,
            Notice notice) {
        update(resentFlag, SMS, sentSerial, noticeService, notice);
    }

    public static void updateMail(final boolean resentFlag, final int sentSerial, final NoticeService noticeService,
            Notice notice) {
        update(resentFlag, MAIL, sentSerial, noticeService, notice);
    }

    public static void update(final boolean resentFlag, final String type, final int sentSerial,
            final NoticeService noticeService, Notice notice) {
        if (resentFlag) {
            sentUpdate(type, sentSerial, noticeService, notice);
        } else {
            unsentUpdate(type, "into DB return false.", sentSerial, noticeService, notice);
        }
    }

    private static boolean sentUpdate(final String type, final int sentSerial, final NoticeService noticeService,
            Notice notice) {
        notice.setIs_sent(SENT);
        notice.setSent_time(NoticeLog.now());

        notice.setSent_counts(sentSerial);
        log.info(NoticeLog.format("==={0}== resend one, update data into database begin.", type));
        boolean flag = noticeService.updateSent(notice);
        log.info(NoticeLog.format("==={0}== resend one, update data into database finished.", type));

        return flag;
    }

    public static boolean unsentUpdateSMS(final String errMessage, final int sentSerial,
            final NoticeService noticeService, Notice notice) {
        return unsentUpdate(SMS, errMessage, sentSerial, noticeService, notice);
    }

    public static boolean unsentUpdateMAIL(final String errMessage, final int sentSerial,
            final NoticeService noticeService, Notice notice) {
        return unsentUpdate(MAIL, errMessage, sentSerial, noticeService, notice);
    }

    private static boolean unsentUpdate(final String type, final String errMessage, final int sentSerial,
            final NoticeService noticeService, Notice notice) {
        notice.setIs_sent(UNSENT);
        notice.setSent_counts(sentSerial);
        notice.setFailed_reason(notice.getFailed_reason() + failedReason(sentSerial, errMessage));

        log.info(NoticeLog.format("==={0}== resend failed, update data into database begin.", type));
        boolean flag = noticeService.updateSent(notice);
        log.info(NoticeLog.format("==={0}== resend failed, update data into database finished.", type));

        if (sentSerial == MAX_COUNTS) {
            log.info(NoticeLog.format("===**= {0} sent {1} times ,all failed ,won't send again", type, MAX_COUNTS));
        }
        return flag;
    }

    private static boolean sendSMS(final String telephone, final String smsMessage, final String sms_sign,
            final String sms_entry_id, SmsService smsService) {
        SendSmsRequest sms = new SendSmsRequest();
        sms.setMethod(MethodType.POST);
        sms.setPhoneNumbers(telephone);
        sms.setSignName(sms_sign);
        sms.setTemplateCode(sms_entry_id);
        log.info(NoticeLog.format("===sms== smsMessage : [{0}] ", smsMessage));
        sms.setTemplateParam(smsMessage);
        boolean resentFlag = smsService.sendSms(sms);
        return resentFlag;
    }

    public static boolean sendEmail(final String email, final String name, final String link, MailService mailService) {
        StringBuilder body = new StringBuilder();
        body.append("<div>");
        body.append(name);
        body.append(",您好。欢迎加入阿迪达斯</div>");
        body.append("<br/><div>请点击以下链接或扫描二维码，填写您的个人信息。</div>");
        body.append("<br/><div><label>链接:</label>");
        body.append("<a href='");
        body.append(link);
        body.append("' target='_blank'>");
        body.append(link);
        body.append("</a></div>");
        body.append("<br/><div>谢谢!</div>");

        MailDescribe mail = new MailDescribe("阿迪达斯入职", body.toString());
        mail.setRecipients(email);
        mail.setCC("");

        boolean resentFlag = mailService.sendHtmlEmail(mail);
        return resentFlag;
    }

    public static String getFromCodeStrOut_do_SMS(final String baseUrl, final Map<String, Object> tmp)
            throws SSLException, ApplicationException {
        return getFromCodeStrOut_do(baseUrl, tmp, SMS);
    }

    public static String getFromCodeStrOut_do_MAIL(final String baseUrl, final Map<String, Object> tmp)
            throws SSLException, ApplicationException {
        return getFromCodeStrOut_do(baseUrl, tmp, MAIL);
    }

    public static String getFromCodeStrOut_do(final String baseUrl, final Map<String, Object> tmp, String TYPE)
            throws SSLException, ApplicationException {
        if (baseUrl.toLowerCase().startsWith("https:")) {
            throw new SSLException("Unrecognized SSL message, protocal https unsupported");
        } else {
            String result = "";
            try {
                result = HttpClientUtil.doGetSSL(baseUrl + "/data/codeStrOut.do", tmp);
            } catch (Exception e) {
                String errMessage = NoticeLog.log06(TYPE, e.getMessage());
                throw new ApplicationException(errMessage);
            }
            log.info(NoticeLog.log07(TYPE, result));

            if (result.contains("HTTP Status 404")) {
                throw new ApplicationException("return 404");
            }
            if (result.contains("HTTP Status 500")) {
                throw new ApplicationException("return 500");
            }

            String data = null;
            try {
                data = (String) common.stringMapJson(result).get("data");
            } catch (ApplicationException e) {
                throw new ApplicationException("data not json");
            }
            return data;
        }
    }

}
