package cn.com.easyerp.framework.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Date工具类
 * 
 * @author Simon Zhang
 */
public class DateUtil {
    /** 日期时间格式：yyyy-MM-dd HH:mm:ss.SSS */
    public static final String ALL_ = "yyyy-MM-dd HH:mm:ss.SSS";
    /** 日期时间格式：yyyyMMddHHmmssSSS */
    public static final String ALL = "yyyyMMddHHmmssSSS";
    /** 日期时间格式：yyyyMMddHHmmss */
    public static final String DT = "yyyyMMddHHmmss";
    /** 日期时间格式：yyyy-MM-dd HH:mm:ss */
    public static final String DT_ = "yyyy-MM-dd HH:mm:ss";
    /** 日期时间格式：yyMMdd */
    public static final String D6 = "yyMMdd";
    /** 日期时间格式：yy-MM-dd */
    public static final String D6_ = "yy-MM-dd";
    /** 日期时间格式：yyyyMMdd */
    public static final String D8 = "yyyyMMdd";
    /** 日期时间格式：yyyy-MM-dd */
    public static final String D8_ = "yyyy-MM-dd";
    /** 日期时间格式：HHmmss */
    public static final String TM = "HHmmss";
    /** 日期时间格式：HH:mm:ss */
    public static final String TM_ = "HH:mm:ss";

    /**
     * 日期转字符串：yyyyMMddHHmmssSSS
     * 
     * @param date
     * @return
     */
    public static String toStrAll(final java.util.Date date) {
        return date2String(date, ALL);
    }

    /**
     * 日期转字符串：yyyy-MM-dd HH:mm:ss.SSS
     * 
     * @param date
     * @return
     */
    public static String toStrAll_(final java.util.Date date) {
        return date2String(date, ALL_);
    }

    /**
     * 日期转字符串：yyyyMMddHHmmss
     * 
     * @param date
     * @return
     */
    public static String toStrDT(final java.util.Date date) {
        return date2String(date, DT);
    }

    /**
     * 日期转字符串：yyyy-MM-dd HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static String toStrDT_(final java.util.Date date) {
        return date2String(date, DT_);
    }

    /**
     * 日期转字符串：yyMMdd
     * 
     * @param date
     * @return
     */
    public static String toStrD6(final java.util.Date date) {
        return date2String(date, D6);
    }

    /**
     * 日期转字符串：yy-MM-dd
     * 
     * @param date
     * @return
     */
    public static String toStrD6_(final java.util.Date date) {
        return date2String(date, D6_);
    }

    /**
     * 日期转字符串：yyyyMMdd
     * 
     * @param date
     * @return
     */
    public static String toStrD8(final java.util.Date date) {
        return date2String(date, D8);
    }

    /**
     * 日期转字符串：yyyy-MM-dd
     * 
     * @param date
     * @return
     */
    public static String toStrD8_(final java.util.Date date) {
        return date2String(date, D8_);
    }

    /**
     * 日期转字符串：HHmmss
     * 
     * @param date
     * @return
     */
    public static String toStrTM(final java.util.Date date) {
        return date2String(date, TM);
    }

    /**
     * 日期转字符串：HH:mm:ss
     * 
     * @param date
     * @return
     */
    public static String toStrTM_(final java.util.Date date) {
        return date2String(date, TM_);
    }

    /**
     * 按照指定格式将时间转换成字符串，不指定格式，返回yyyy-MM-dd HH:mm:ss格式
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static String date2String(final java.util.Date date, String pattern) {
        if (EmptyUtil.isEmpty(date)) {
            throw new java.lang.IllegalArgumentException("date null illegal");
        }
        if (EmptyUtil.isEmpty(pattern)) {
            pattern = DT_;
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 字符串格式日期 增加一天 返回
     * 
     * @param dateString
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static String stringDatePlusOneDay(final String dateString, String pattern) throws ParseException {
        Date date = string2Date(dateString, pattern);
        Date date1 = plusDays(date, 1);
        return date2String(date1, pattern);
    }

    /**
     * 字符串转时间
     * 
     * @param dateString
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date string2Date(final String dateString, String pattern) throws ParseException {

        DateFormat formatter = new SimpleDateFormat(pattern);
        Date date = formatter.parse(dateString);

        return date;
    }

    /**
     * 将yyyy-MM-dd格式的日期字符串转化成指定格式的字符串
     * 
     * @param source
     *            yyyy-MM-dd形式的日期字符串
     * @param pattern
     *            格式化后的格式
     * @return
     * @throws ParseException
     */
    public static String dateStrFormat(String source, String pattern) throws ParseException {
        if (EmptyUtil.isEmpty(source)) {
            throw new java.lang.IllegalArgumentException("date null illegal");
        }
        if (!isDateStrLegal(source, D8_)) {
            throw new java.lang.IllegalArgumentException("date not yyyy-MM-dd illegal");
        }
        if (EmptyUtil.isEmpty(pattern)) {
            pattern = D8_;
        }
        try {
            // 创建原始日期的时间对象
            DateFormat formatter = new SimpleDateFormat(D8_);
            Date date = formatter.parse(source);
            // 格式化成目标字符串
            final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        } catch (ParseException e) {
            return source;
        }
    }

    /**
     * 将yyyyMMdd格式的日期字符串转化yyMMdd的字符串
     * 
     * @param source
     *            yyyyMMdd形式的日期字符串
     * @return
     * @throws ParseException
     */
    public static String format001(String source) throws ParseException {
        if (EmptyUtil.isEmpty(source)) {
            throw new java.lang.IllegalArgumentException("date null illegal");
        }
        if (!isDateStrLegal(source, D8)) {
            throw new java.lang.IllegalArgumentException("date not D8 illegal");
        }
        try {
            // 创建原始日期的时间对象
            DateFormat formatter = new SimpleDateFormat(D8);
            Date date = formatter.parse(source);
            // 格式化成目标字符串
            final SimpleDateFormat sdf = new SimpleDateFormat("D6");
            return sdf.format(date);
        } catch (ParseException e) {
            return source;
        }
    }

    /**
     * 将HHmmss格式的时间字符串转化HHmm的字符串
     * 
     * @param source
     *            HHmmss形式的日期字符串
     * @return
     * @throws ParseException
     */
    public static String format002(String source) throws ParseException {
        if (EmptyUtil.isEmpty(source)) {
            throw new java.lang.IllegalArgumentException("date null illegal");
        }
        if (!isDateStrLegal(source, TM)) {
            throw new java.lang.IllegalArgumentException("date not D8 illegal");
        }
        try {
            // 创建原始日期的时间对象
            DateFormat formatter = new SimpleDateFormat(TM);
            Date date = formatter.parse(source);
            // 格式化成目标字符串
            final SimpleDateFormat sdf = new SimpleDateFormat("HHmm");
            return sdf.format(date);
        } catch (ParseException e) {
            return source;
        }
    }

    /**
     * 将HH:mm:ss格式的时间字符串转化成指定格式的字符串
     * 
     * @param source
     *            HH:mm:ss形式的时间字符串
     * @param pattern
     *            格式化后的格式
     * @return
     */
    public static String timeStrFormat(String source, String pattern) {
        if (EmptyUtil.isEmpty(source)) {
            throw new java.lang.IllegalArgumentException("time null illegal");
        }
        if (!isDateStrLegal(source, TM_)) {
            throw new java.lang.IllegalArgumentException("time not HH:mm:ss illegal");
        }
        if (EmptyUtil.isEmpty(pattern)) {
            pattern = TM_;
        }
        try {
            // 创建原始时间的时间对象
            DateFormat formatter = new SimpleDateFormat(TM_);
            Date date = formatter.parse(source);
            // 格式化成目标字符串
            final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            return sdf.format(date);
        } catch (ParseException e) {
            return source;
        }

    }

    /**
     * 判断日期时间格式是否合法
     * 
     * @param dateStr
     * @param pattern
     * @return true 合法
     */
    public static boolean isDateStrLegal(String dateStr, String pattern) {
        if (EmptyUtil.isEmpty(dateStr) || EmptyUtil.isEmpty(pattern)) {
            return false;
        }
        if (dateStr.length() != pattern.length()) {
            return false;
        }
        DateFormat formatter = new SimpleDateFormat(pattern);
        try {
            formatter.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 从字符串里截取两位月份，格式不合法返回“”
     * 
     * @param dateStr
     * @return
     */
    public static String getMon(String dateStr) {
        if (isDateStrLegal(dateStr, DT_)) {
            return dateStr.substring(5, 7);
        }
        if (isDateStrLegal(dateStr, D8_)) {
            return dateStr.substring(5, 7);
        }
        if (isDateStrLegal(dateStr, D6)) {
            return dateStr.substring(2, 4);
        }
        if (isDateStrLegal(dateStr, D8)) {
            return dateStr.substring(4, 6);
        }
        return "";
    }

    /**
     * 日期字符串年份修复<br>
     * "000000" 返回 “”<br>
     * "D6" 返回 “20yyMMdd”<br>
     * "yy-MM-dd" 返回 “20yy-MM-dd”<br>
     * 其他情况 返回原值
     * 
     * @param dateStr
     * @return
     */
    public static String fixYear(String dateStr) {
        // 判断如果为000000 则返回空
        if (dateStr.equals("000000")) {
            return "";
        }
        if (isDateStrLegal(dateStr, D6)) {
            return "20" + dateStr;
        }
        if (isDateStrLegal(dateStr, "yy-MM-dd")) {
            return "20" + dateStr;
        }
        return dateStr;
    }

    /**
     * <p>
     * Description:[在指定日期上增加指定的天数获得新的日期]
     * </p>
     * 
     * @param date
     * @param dayToPlus
     *            增加天数，可正可负
     * @return
     * @author:[Simon Zhang]
     * @update:[2018.07.04] [更改人姓名][变更描述]
     */
    public static Date plusDays(Date date, int dayToPlus) {
        long dateLong = date.getTime();
        long newDate = dateLong + dayToPlus * 24 * 60 * 60 * 1000L;
        return new Date(newDate);
    }

    /**
     * <p>
     * Description:[将MM-dd 转换成 D8]
     * </p>
     * 
     * @param date
     *            MM-dd格式的日期
     * @return
     * @author:[Simon Zhang]
     * @update:[2018.07.04] [更改人姓名][变更描述]
     */
    public static String trans2yyyyMMdd(String MM_dd) {
        if (EmptyUtil.isEmpty(MM_dd)) {
            return "";
        }
        if (!isDateStrLegal(MM_dd, "MM-dd")) {
            return MM_dd;
        }
        String year = date2String(new Date(), "yyyy");

        String[] md = MM_dd.split("-");

        String month = "00" + md[0];
        month = month.substring(month.length() - 2);

        String day = "00" + md[1];
        day = day.substring(day.length() - 2);

        return year + month + day;
    }

    public static void testJapaneseCalendar() throws ParseException {
        SimpleDateFormat d = new SimpleDateFormat("yyyy/MM/dd");

        Locale locale = new Locale("ja", "JP", "JP");
        // 必须这样实例化 Locale，不可使用 Locale.JAPAN 或 Locale.JAPANESE，否则后续的格式化无效

        DateFormat ja = DateFormat.getDateInstance(DateFormat.FULL, locale);
        System.out.println(ja.format(d.parse("2012/11/12"))); // 结果是：平成24年11月12日

        ja = new SimpleDateFormat("GGGG yy年MM月dd日", locale);
        System.out.println(ja.format(d.parse("2012/11/12"))); // 结果是：平成_24年11月12日（下划线代表空格）

        ja = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        System.out.println(ja.format(d.parse("2012/11/12"))); // 结果是：H24.11.12

        ja = new SimpleDateFormat("G yy.MM.dd", locale);
        System.out.println(ja.format(d.parse("2012/11/12"))); // 结果是：H_24.11.12（下划线代表空格）

        ja = new SimpleDateFormat("GGGG yy年MM月dd日", locale);
        System.out.println(ja.format(d.parse("1868/01/01"))); // 结果是：明治_01年01月01日（下划线代表空格）

        ja = new SimpleDateFormat("G yy.MM.dd", locale);
        System.out.println(ja.format(d.parse("1868/01/01"))); // 结果是：M_01.01.01（下划线代表空格）

        ja = new SimpleDateFormat("GGGG yy年MM月dd日", locale);
        System.out.println(ja.format(d.parse("1867/12/31"))); // 结果是：西暦_1867年12月31日（下划线代表空格）

        ja = new SimpleDateFormat("G yy.MM.dd", locale);
        System.out.println(ja.format(d.parse("1867/12/31"))); // 结果是：_1867.12.31（下划线代表空格）
    }

    /**
     * 日本邦历转公历<br>
     * 
     * @param jDate
     *            日本邦历
     * @param jPattern
     *            邦历格式
     * @param wPattern
     *            公历格式
     * @return
     * @throws ParseException
     */
    public static String Japanese2World(String jDate, String jPattern, String wPattern) throws ParseException {
        if (EmptyUtil.isEmpty(jDate)) {
            return "";
        }
        if (EmptyUtil.isStrBlank(jDate.trim())) {
            return "";
        }
        if ("000000".equals(jDate.trim())) {
            return "";
        }
        SimpleDateFormat d = new SimpleDateFormat(wPattern);

        Locale locale = new Locale("ja", "JP", "JP");
        // 必须这样实例化 Locale，不可使用 Locale.JAPAN 或 Locale.JAPANESE，否则后续的格式化无效
        DateFormat ja = DateFormat.getDateInstance(DateFormat.FULL, locale);
        ja = new SimpleDateFormat(jPattern, locale);

        return d.format(ja.parse(jDate));
    }

    /**
     * 公历转日本邦历<br>
     * 
     * @param wDate
     *            公历日期
     * @param jPattern
     *            邦历格式
     * @param wPattern
     *            公历格式
     * @return
     * @throws ParseException
     */
    public static String World2Japanese(String wDate, String jPattern, String wPattern) throws ParseException {
        if (EmptyUtil.isEmpty(wDate)) {
            return "";
        }
        if (EmptyUtil.isStrBlank(wDate.trim())) {
            return "";
        }
        if ("000000".equals(wDate.trim())) {
            return "";
        }
        Locale locale = new Locale("ja", "JP", "JP");
        // 必须这样实例化 Locale，不可使用 Locale.JAPAN 或 Locale.JAPANESE，否则后续的格式化无效
        DateFormat ja = DateFormat.getDateInstance(DateFormat.FULL, locale);
        ja = new SimpleDateFormat(jPattern, locale);

        SimpleDateFormat d = new SimpleDateFormat(wPattern);

        return ja.format(d.parse(wDate));
    }

    /**
     * 从字符串里截取两位日期，格式不合法返回“”
     * 
     * @param dateStr
     * @return
     */
    public static String getDay(String dateStr) {
        if (isDateStrLegal(dateStr, DT_)) {
            return dateStr.substring(8, 10);
        }
        if (isDateStrLegal(dateStr, D8_)) {
            return dateStr.substring(8, 10);
        }
        if (isDateStrLegal(dateStr, D6)) {
            return dateStr.substring(4, 6);
        }
        if (isDateStrLegal(dateStr, D8)) {
            return dateStr.substring(6, 8);
        }
        return "";
    }
}