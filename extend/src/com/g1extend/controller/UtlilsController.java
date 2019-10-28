package com.g1extend.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.net.ssl.SSLException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.http.MethodType;
import com.g1extend.entity.Notice;
import com.g1extend.expection.ApplicationException;
import com.g1extend.mail.MailDescribe;
import com.g1extend.mail.MailService;
import com.g1extend.service.ContractService;
import com.g1extend.service.FtpService;
import com.g1extend.service.NoticeService;
import com.g1extend.service.OuterPageService;
import com.g1extend.service.SmsService;
import com.g1extend.task.NoticeLog;
import com.g1extend.task.NoticeUtils;
import com.g1extend.utils.HttpClientUtil;
import com.g1extend.utils.QRCodeParams;
import com.g1extend.utils.QRCodeUtil;
import com.g1extend.utils.common;

@Controller
@RequestMapping({ "/utils" })
public class UtlilsController {
    @Autowired
    private MailService mailService;
    @Autowired
    @Qualifier("aliSmsServer")
    private SmsService smsService;
    @Autowired
    private FtpService ftpService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private OuterPageService outerPageService;
    @Autowired
    private NoticeService noticeService;

    private static Log log = LogFactory.getLog(UtlilsController.class);

    @RequestMapping({ "/getQrcode" })
    public ModelAndView getFormModal(@RequestParam(value = "code", required = true) String code, String domain,
            HttpServletRequest request) {
        code = code + "&domain=" + domain;
        String pathStr = request.getSession().getServletContext().getRealPath("/img/");
        if ("\\".equals(File.separator)) {
            pathStr = pathStr.substring(0).replaceAll("/", "\\\\");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        QRCodeParams param = new QRCodeParams();
        param.setTxt(code);
        param.setHeight(Integer.valueOf(300));
        param.setWidth(Integer.valueOf(300));
        param.setFilePath(pathStr);
        param.setFileName(uuid + ".png");
        String path = uuid + ".png";

        QRCodeUtil.generateQRImage(param);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/Qrcode");
        modelAndView.addObject("path", path);
        modelAndView.addObject("url", code);
        return modelAndView;
    }

    @RequestMapping({ "/entryRemind.do" })
    @ResponseBody
    public String inductionMail(@RequestParam(value = "link", required = true) String link, String param, String name,
            String email, String telephone, int isemail, int issms, String baseUrl, HttpServletRequest request) {
        log.info("===send notice API entryRemind.do is called ===");

        String key = this.outerPageService.selectDataBase();
        log.info("key from selectDataBase(): " + key);

        Map<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("domain", key);
        String data = null;
        try {
            String str = URLEncoder.encode(param, "utf-8");
            log.info("param after encode ：" + str);

            tmp.put("str", str);
            log.info("tmp：" + tmp.toString());

            data = NoticeUtils.getFromCodeStrOut_do(baseUrl, tmp, NoticeUtils.ALL);
            log.info("data from CodeStrOut.do :" + data);
        } catch (UnsupportedEncodingException | SSLException | ApplicationException e) {
            if (issms != 0) {
                String errMessage = NoticeLog.smsLog11(e.getMessage());
                Notice notice = new Notice(link, param, name, telephone, baseUrl, failedReason(1, errMessage));
                log.info(NoticeLog.smsLog17());
                NoticeUtils.saveUnsentSMS(noticeService, notice);
                log.info(NoticeLog.smsLog18());
            }
            if (isemail != 0) {
                String errMessage = NoticeLog.mailLog11(e.getMessage());
                Notice notice = new Notice(link, param, name, email, baseUrl, failedReason(1, errMessage));
                log.info(NoticeLog.mailLog17());
                NoticeUtils.saveUnsentMAIL(noticeService, notice);
                log.info(NoticeLog.mailLog18());

            }
        }
        link = NoticeUtils.genLink(link, data, key);
        log.info("link after append : " + link);

        String pathStr = request.getSession().getServletContext().getRealPath("/img/");
        if ("\\".equals(File.separator)) {
            pathStr = pathStr.substring(0).replaceAll("/", "\\\\");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        QRCodeParams code = new QRCodeParams();
        code.setTxt(link);
        code.setHeight(Integer.valueOf(300));
        code.setWidth(Integer.valueOf(300));
        code.setFilePath(pathStr);
        code.setFileName(uuid + ".png");
        String path = uuid + ".png";

        log.info("email============================");
        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath() + "/";

        Properties sys_param = common.getSystemParam();
        if (issms != 0) {
            log.info(NoticeLog.smsLog15());
            Map<String, Object> tmpData = new HashMap<String, Object>();
            tmpData.put("name", name);
            tmpData.put("link", (data == null) ? "" : data);
            String smsMessage = common.toJson(tmpData);

            boolean isSent = NoticeUtils.sendSMS(telephone, smsMessage, sys_param, smsService);
            if (isSent) {
                Notice notice = new Notice(link, param, name, telephone, baseUrl, "");
                log.info(NoticeLog.smsLog13("succeed"));
                NoticeUtils.saveSentSMS(noticeService, notice);
                log.info(NoticeLog.smsLog14("succeed"));
            } else {
                Notice notice = new Notice(link, param, name, telephone, baseUrl, "1.smsService return failed;");
                log.info(NoticeLog.smsLog13("failed"));
                NoticeUtils.saveUnsentSMS(noticeService, notice);
                log.info(NoticeLog.smsLog14("failed"));
            }
            log.info(NoticeLog.smsLog16());
        }
        if (isemail != 0) {
            log.info(NoticeLog.mailLog15());
            boolean isSent = NoticeUtils.sendEmail(email, name, link, mailService);
            if (isSent) {
                Notice notice = new Notice(link, param, name, email, baseUrl, "");
                log.info(NoticeLog.mailLog13("succeed"));
                NoticeUtils.saveSentMAIL(noticeService, notice);
                log.info(NoticeLog.mailLog14("succeed"));
            } else {
                Notice notice = new Notice(link, param, name, email, baseUrl, "1.mailService return failed;");
                log.info(NoticeLog.mailLog13("failed"));
                NoticeUtils.saveUnsentMAIL(noticeService, notice);
                log.info(NoticeLog.mailLog14("failed"));
            }
            log.info(NoticeLog.mailLog16());
        }

        return "succeed!";
    }

    @RequestMapping({ "/up.do" })
    @ResponseBody
    public String sendMail(String email) {
        log.info("send mail start===========");

        MailDescribe mail = new MailDescribe("阿迪达斯入职", "测试邮件");
        mail.setRecipients(email);
        mail.setCC("");

        if (this.mailService.sendHtmlEmail(mail))
            return "send mail succeed";
        return "send mail faild!===============";
    }

    @RequestMapping({ "/phone.do" })
    @ResponseBody
    public String phone(String phone) {
        Properties param = common.getSystemParam();
        SendSmsRequest sms = new SendSmsRequest();
        sms.setMethod(MethodType.POST);
        sms.setPhoneNumbers(phone);
        sms.setSignName(param.getProperty("sms_sign"));
        sms.setTemplateCode(param.getProperty("sms_entry_id"));
        sms.setTemplateParam("{\"name\":\"吴辉\", \"link\":\":code\"}");
        this.smsService.sendSms(sms);

        return "succeed";
    }

    @RequestMapping({ "/data.do" })
    @ResponseBody
    public String data() {
        String key = "DEVHRIDB";
        Map<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("domain", key);
        try {
            tmp.put("str", URLEncoder.encode("xxxxx", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String result = HttpClientUtil.doGetSSL("https://hirs-dev.adidas.com.cn/adidas/data/codeStrOut.do", tmp);
        String data = (String) common.stringMapJson(result).get("data");

        return "data=" + data + ",domain=" + key;
    }

    @RequestMapping({ "/base.do" })
    @ResponseBody
    public String base() {
        String key = this.contractService.getDomain();
        Map<String, Object> tmp = new HashMap<String, Object>();
        tmp.put("domain", key);
        try {
            tmp.put("str", URLEncoder.encode("xxxxx", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String result = HttpClientUtil.doGetSSL("http://139.224.130.197:8080/adidas/data/codeStrOut.do", tmp);
        String data = (String) common.stringMapJson(result).get("data");

        return "data=" + data + ",domain=" + key;
    }

    @RequestMapping({ "/img.do" })
    @ResponseBody
    public String img(HttpServletRequest request) {
        String pathStr = request.getSession().getServletContext().getRealPath("/img/");

        if ("\\".equals(File.separator)) {
            pathStr = pathStr.substring(0).replaceAll("/", "\\\\");
        }
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        QRCodeParams code = new QRCodeParams();
        code.setTxt("2222");
        code.setHeight(Integer.valueOf(300));
        code.setWidth(Integer.valueOf(300));
        code.setFilePath(pathStr);
        code.setFileName(uuid + ".png");
        String path = uuid + ".png";

        StringBuilder body = new StringBuilder();
        body.append("<div>,您好。欢迎加入阿迪达斯</div>");
        body.append("<br/><div>请点击以下链接或扫描二维码，填写您的个人信息。</div>");
        body.append("<br/><div><label>链接:</label><a href='' target='_blank'></a></div>");
        body.append(
                "<br/><div style='text-align:center'><img src='data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAPYAAAD2AQAAAADNaUdlAAACK0lEQVR42tVZ0a3EMAhDygAdqatnpA4QiQe2097bwDlVVRX3PhAYGxrRvzufiCtHJu4z63ri+xnjdZbPXa/U87rmqjsOx3MXcgD+1HWvKwvEc9RxvbL/dAbe0SBF2WHNQsZReAeEsmKtVYkdg+/6WqBDP1/Kz//6M8XJb6bo//XLf1888VsVH8gNlnejGjp3x4MlhlQ0y5EoZGnuf1jjTYWcOOvn1RRHcfEVexwkuEHuJopoDZVDrO44O1MfgBNviHWvw7DH3ybUtoLMxuuVnCH+WOOVBLF5CwVe6XBRX+640tLdtFtU9it9BexG2OOtZl1HkdSEazJjrLUT8Bk/skCuo9EGhdocB7/RjSAICJS9No/AYeLkj1BfCLazpFnBHOdAwJywS7XLhlwgdf44NA2asL1qaNzp/uqOd03hkfpAZUZx4W6Pa6aEMnA+Q1lRHFhf1njKUMiQhuhO0b7DHg+Z7Fu2NF9/SrvtjrOPApe+jZTEaT7wxjsbj3LCsYDOdHBos8cxWWqgJ63XzhX8hTuOyYCedH71heAQoj3OViR9mLSobLeijjc+fmaahViXkjYxLtjjULPx0J9Ck/eulP7CHUdbYkwc8ZNCHWfg3G9hIM7FzQRYMqRy7vjeL25bjf3Kz37XHd+yEK+/1qKII74/ruYqf61dO+oLdXcGrs9i335lsukehL/+GrOmvngcgH/VFLn3K9oyft8/fHHxmzshfl/dH8oUtDX+B6wrimXm+IXHAAAAAElFTkSuQmCC'/></div>");
        body.append("<br/><div>谢谢!</div>");

        MailDescribe mail = new MailDescribe("阿迪达斯入职", body.toString());
        mail.setRecipients("583773081@qq.com,jianglingqin@gainit.cn");
        mail.setCC("");

        this.mailService.sendHtmlEmail(mail);

        return common.getImgStr(QRCodeUtil.generateQRImage(code));
    }

    public static String failedReason(final int sentSerial, final String reason) {
        return MessageFormat.format("{0}. {1} \n", sentSerial, reason);
    }

    /**
     * 按照指定格式将时间转换成字符串，不指定格式，返回yyyy-MM-dd HH:mm:ss格式
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String date2String(final java.util.Date date, String pattern) {
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    public static String format(final String pattern, final Object... args) {
        if (StringUtils.isEmpty(pattern)) {
            throw new java.lang.IllegalArgumentException();
        }
        return MessageFormat.format(pattern, args);
    }

}
