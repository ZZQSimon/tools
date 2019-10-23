/**
	* Title: NoticeTask.java <br>
	* Description:[] <br>
	* Copyright (c)  2018<br>
	* Company: <br>
	* @Date 2019.09.26 <br>
	* 
	* @author Simon Zhang
	* @version V1.0
	*/
package com.g1extend.task;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.g1extend.entity.Notice;
import com.g1extend.expection.ApplicationException;
import com.g1extend.mail.MailService;
import com.g1extend.service.NoticeService;
import com.g1extend.service.OuterPageService;
import com.g1extend.service.SmsService;
import com.g1extend.utils.common;

/**
 * @ClassName: NoticeTask <br>
 * @Description: [] <br>
 * @date 2019.09.26 <br>
 * 
 * @author Simon Zhang
 */
@Component
public class NoticeTask {

    private static Log log = LogFactory.getLog(NoticeTask.class);

    @Autowired
    private NoticeService noticeService;
    @Autowired
    private OuterPageService outerPageService;
    @Autowired
    @Qualifier("aliSmsServer")
    private SmsService smsService;
    @Autowired
    private MailService mailService;

    // @Scheduled(cron = "0/10 * * * * ? ")
    // public void resent3() {
    // String link =
    // "http://172.16.0.116:8080/adidas/externalForm/externalForm.view";
    // String param =
    // "{tableId:'t_advance_entry_empInfo',database:'gc_pre_onboarding',dataId:{id:
    // '0000032163'}}";
    // String name = "dfszzc";
    // String notice_to = "13940372392";
    // String baseUrl = "http://172.16.0.116:8080/root0624";
    // String errMessage = NoticeLog.smsLog11("1.dfsdfdsfdsfsddd");
    // Notice notice = new Notice(link, param, name, notice_to, baseUrl,
    // errMessage);
    // log.info(NoticeLog.smsLog17());
    // NoticeUtils.saveUnsentSMS(noticeService, notice);
    // log.info(NoticeLog.smsLog18());
    // }

    // @Scheduled(cron = "0/20 * * * * ? ") // 间隔20秒执行
    @Scheduled(cron = "30 3/5 * * * ? ") // 第三分钟开始，每间隔五分钟执行一次，执行时间是当前分钟内30秒时
    public void resentSMS() {
        long startMillis = System.currentTimeMillis();
        log.info(NoticeLog.smsLog01(startMillis));

        List<Notice> list = noticeService.findAllToSent(NoticeUtils.T_SMS, NoticeUtils.MAX_COUNTS);
        int size = list.size();
        if (!list.isEmpty()) {
            Map<String, Object> tmp = new HashMap<String, Object>();
            String key = this.outerPageService.selectDataBase();
            log.info(NoticeLog.smsLog02(key));
            for (Notice notice : list) {
                log.info(NoticeLog.smsLog03(notice.toString()));
                tmp.clear();
                tmp.put("domain", key);

                String param = notice.getParam();
                String link = notice.getLink();
                String baseUrl = notice.getBase_url();
                String name = notice.getName();
                String telephone = notice.getNotice_to();
                int sentTimes = notice.getSent_counts();
                int sentSerial = sentTimes + 1;

                try {
                    tmp.put("str", URLEncoder.encode(param, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    NoticeUtils.unsentUpdateSMS(NoticeLog.log_10, sentSerial, noticeService, notice);
                    continue;
                }
                log.info(NoticeLog.smsLog04(tmp.toString()));
                log.info(NoticeLog.smsLog05(baseUrl));
                String data = null;
                try {
                    data = NoticeUtils.getFromCodeStrOut_do_SMS(baseUrl, tmp);
                } catch (SSLException | ApplicationException e) {
                    String errMessage = NoticeLog.smsLog11(e.getMessage());
                    NoticeUtils.unsentUpdateSMS(errMessage, sentSerial, noticeService, notice);
                    continue;
                }
                log.info(NoticeLog.smsLog08(data));

                link = NoticeUtils.genLink(link, data, key);

                Properties sys_param = common.getSystemParam();

                Map<String, Object> tmpData = new HashMap<String, Object>();
                tmpData.put("name", name);
                tmpData.put("link", (data == null) ? "" : data);
                String smsMessage = common.toJson(tmpData);

                boolean resentFlag = NoticeUtils.sendSMS(telephone, smsMessage, sys_param, smsService);
                log.info(NoticeLog.smsLog09(resentFlag));
                NoticeUtils.updateSMS(resentFlag, sentSerial, noticeService, notice);
            }
        }
        long endMillis = System.currentTimeMillis();
        log.info(NoticeLog.smsLog12(startMillis, endMillis, size));
    }

    @Scheduled(cron = "0 0/5 * * * ? ") // 间隔五分钟执行
    public void resentEmail() {
        long startMillis = System.currentTimeMillis();
        log.info(NoticeLog.mailLog01(startMillis));
        List<Notice> list = noticeService.findAllToSent(NoticeUtils.T_MAIL, NoticeUtils.MAX_COUNTS);
        int size = list.size();

        if (!list.isEmpty()) {
            Map<String, Object> tmp = new HashMap<String, Object>();
            String key = this.outerPageService.selectDataBase();
            log.info(NoticeLog.mailLog02(key));
            for (Notice notice : list) {
                log.info(NoticeLog.mailLog03(notice.toString()));
                tmp.clear();
                tmp.put("domain", key);

                String param = notice.getParam();
                String link = notice.getLink();
                String baseUrl = notice.getBase_url();
                String name = notice.getName();
                String email = notice.getNotice_to();
                int sentTimes = notice.getSent_counts();
                int sentSerial = sentTimes + 1;

                try {
                    tmp.put("str", URLEncoder.encode(param, "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    NoticeUtils.unsentUpdateMAIL(NoticeLog.log_10, sentSerial, noticeService, notice);
                    continue;
                }
                log.info(NoticeLog.mailLog04(tmp.toString()));
                log.info(NoticeLog.mailLog05(baseUrl));

                String data = null;
                try {
                    data = NoticeUtils.getFromCodeStrOut_do_MAIL(baseUrl, tmp);
                } catch (SSLException | ApplicationException e) {
                    String errMessage = NoticeLog.mailLog11(e.getMessage());
                    NoticeUtils.unsentUpdateMAIL(errMessage, sentSerial, noticeService, notice);
                    continue;
                }

                log.info(NoticeLog.mailLog08(data));

                link = NoticeUtils.genLink(link, data, key);
                boolean resentFlag = NoticeUtils.sendEmail(email, name, link, mailService);
                log.info(NoticeLog.mailLog09(resentFlag));
                NoticeUtils.updateMail(resentFlag, sentSerial, noticeService, notice);
            }
        }
        long endMillis = System.currentTimeMillis();
        log.info(NoticeLog.mailLog12(startMillis, endMillis, size));
    }

}
