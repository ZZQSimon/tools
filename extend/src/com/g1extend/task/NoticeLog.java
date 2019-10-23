package com.g1extend.task;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class NoticeLog {
    public static final String log_01 = "==={0}== 重新发送开始 {1} == ===";
    public static final String log_02 = "==={0}== selectDataBase --> {1}";
    public static final String log_03 = "==={0}== 数据库中待发送数据 --> {1}";
    public static final String log_04 = "==={0}== param tmp: [{1}]";
    public static final String log_05 = "==={0}== codeStrOut.do baseUrl :[{1}] ";
    public static final String log_06 = "doGetSSL failed [{0}]";
    public static final String log_07 = "==={0}== codeStrOut.do result :[{1}] ";
    public static final String log_08 = "==={0}== codeStrOut.do return data : [{1}] ";
    public static final String log_09 = "==={0}== sms sent result : [{1}] ";
    public static final String log_10 = "UnsupportedEncodingException";
    public static final String log_11 = "codeStrOut.do error:[{0}]";
    public static final String log_12 = "==={0}== {1}  重新发送结束，执行 {2} 条记录，耗时 {3} 毫秒 == ===";

    public static final String log_13 = "==={0}== sent {1}, insert data into database begin===========";
    public static final String log_14 = "==={0}== sent {1}, insert data into database over===========";
    public static final String log_15 = "==={0}== send begin===========";
    public static final String log_16 = "==={0}== send over===========";
    public static final String log_17 = "==={0}== get data failed  before send, insert data into database begin===========";
    public static final String log_18 = "==={0}== get data failed  before send, insert data into database over===========";

    public static String smsLog01(final long startMillis) {
        return format(log_01, NoticeUtils.SMS, date2String(startMillis));
    }

    public static String mailLog01(final long startMillis) {
        return format(log_01, NoticeUtils.MAIL, date2String(startMillis));
    }

    public static String smsLog02(final String key) {
        return format(log_02, NoticeUtils.SMS, key);
    }

    public static String mailLog02(final String key) {
        return format(log_02, NoticeUtils.MAIL, key);
    }

    public static String smsLog03(final String key) {
        return format(log_03, NoticeUtils.SMS, key);
    }

    public static String mailLog03(final String key) {
        return format(log_03, NoticeUtils.MAIL, key);
    }

    public static String smsLog04(final String key) {
        return format(log_04, NoticeUtils.SMS, key);
    }

    public static String mailLog04(final String key) {
        return format(log_04, NoticeUtils.MAIL, key);
    }

    public static String smsLog05(final String key) {
        return format(log_05, NoticeUtils.SMS, key);
    }

    public static String mailLog05(final String key) {
        return format(log_05, NoticeUtils.MAIL, key);
    }

    public static String log06(final String type, final String key) {
        return format(log_06, type, key);
    }

    public static String log07(final String type, final String key) {
        return format(log_07, type, key);
    }

    public static String smsLog08(final String key) {
        return format(log_08, NoticeUtils.SMS, key);
    }

    public static String mailLog08(final String key) {
        return format(log_08, NoticeUtils.MAIL, key);
    }

    public static String smsLog09(final boolean key) {
        return format(log_09, NoticeUtils.SMS, key);
    }

    public static String mailLog09(final boolean key) {
        return format(log_09, NoticeUtils.MAIL, key);
    }

    public static String smsLog11(final String key) {
        return format(log_11, key);
    }

    public static String mailLog11(final String key) {
        return format(log_11, key);
    }

    public static String smsLog12(final long startMillis, final long endMillis, final int size) {
        return format(log_12, NoticeUtils.SMS, date2String(endMillis), size, endMillis - startMillis);
    }

    public static String mailLog12(final long startMillis, final long endMillis, final int size) {
        return format(log_12, NoticeUtils.MAIL, date2String(endMillis), size, endMillis - startMillis);
    }

    public static String smsLog13(final String key) {
        return format(log_13, NoticeUtils.SMS, key);
    }

    public static String mailLog13(final String key) {
        return format(log_13, NoticeUtils.MAIL, key);
    }

    public static String smsLog14(final String key) {
        return format(log_14, NoticeUtils.SMS, key);
    }

    public static String mailLog14(final String key) {
        return format(log_14, NoticeUtils.MAIL, key);
    }

    public static String smsLog15() {
        return format(log_15, NoticeUtils.SMS);
    }

    public static String mailLog15() {
        return format(log_15, NoticeUtils.MAIL);
    }

    public static String smsLog16() {
        return format(log_16, NoticeUtils.SMS);
    }

    public static String mailLog16() {
        return format(log_16, NoticeUtils.MAIL);
    }

    public static String smsLog17() {
        return format(log_17, NoticeUtils.SMS);
    }

    public static String mailLog17() {
        return format(log_17, NoticeUtils.MAIL);
    }

    public static String smsLog18() {
        return format(log_18, NoticeUtils.SMS);
    }

    public static String mailLog18() {
        return format(log_18, NoticeUtils.MAIL);
    }

    public static String date2String(final long millis) {
        return date2String(new Date(millis), "yyyy-MM-dd HH:mm:ss");
    }

    public static String now() {
        return date2String(new Date(), "yyyy-MM-dd HH:mm:ss");
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
