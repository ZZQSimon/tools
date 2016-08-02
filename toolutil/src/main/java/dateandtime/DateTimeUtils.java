/**
 * 功能：包含常用的关于日期和时间类型的操作
 * 实现的功能：
 * 		
 * 
 * 遗留问题：本代码没有添加异常处理，所有的异常将被抛出到调用本类方法的方法中
 * 
 * 
 * 作者：Jackeen Zhang
 * 时间：2013-03-22 10:24:30
 * 版本：1.0
 * 
 * 备注： 如果后期有具体的功能要求，可以对本程序进行改进
 */
package dateandtime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.sql.Timestamp;
import java.text.*;

public final class DateTimeUtils {
	/**
	 * 将输入的字符串格式化成Date类型，String中有空格，则格式化到时间
	 * 
	 * @param s
	 *            需满足“yyyy-MM-dd HH:mm:ss”或者“yyyy-MM-dd”
	 * @return
	 * @throws ParseException
	 */
	public static Date toDate(String s) throws ParseException {
		if (null == s || s.trim().equals("")) {
			return null;
		} else {
			String dateFormate = null;
			if (s.trim().indexOf(" ") > 0) {
				dateFormate = "yyyy-MM-dd HH:mm:ss";
			} else {
				dateFormate = "yyyy-MM-dd";
			}
			SimpleDateFormat format = new SimpleDateFormat(dateFormate);
			Date date = format.parse(s);
			return date;
		}
	}

	/**
	 * 输入时间字符串以及格式化的格式字符串，输出格式化的Date类型
	 * 
	 * @param date
	 * @param dateFormate
	 * @return
	 * @throws ParseException
	 */
	public static Date Str2Date(String date, String dateFormate)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(dateFormate);
		Date d = format.parse(date);
		return d;
	}

	/**
	 * 输入时间字符串以及格式化的格式字符串，输出格式化的String类型
	 * 
	 * @param dateStr
	 * @param dateFormat
	 * @return
	 * @throws ParseException
	 */
	public static String formatDateStr(String dateStr, String dateFormat)
			throws ParseException {
		Date date = toDate(dateStr);
		return toStr(date, dateFormat);
	}

	/**
	 * 输入时间类型，以及格式字符串，输出格式化的字符串
	 * 
	 * @param d
	 * @param dateFormat
	 * @return
	 */
	public static String toStr(Date d, String dateFormat) {
		if (d == null) {
			return null;
		}
		SimpleDateFormat sy1 = new SimpleDateFormat(dateFormat);
		String dateString = sy1.format(d);
		return dateString;
	}

	/**
	 * 输入时间字符串，若有空格，则将时间定格为 12：01：01
	 * 
	 * @param s
	 * @return
	 */
	public static String convertToFormateDateTime(String s) {
		String str = "";
		if (s.trim().indexOf(" ") == -1) {
			str = s.trim() + " 12:01:01";
		} else {
			str = s.trim();
		}
		return str;
	}

	/**
	 * 输入开始时间串：yyyy-mm-dd,输出时间戳类型日期：yyyy-mi-dd
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	public static Date BeginTime(String str) throws ParseException {
		String ll = null;
		Date date = null;
		ll = str + " 00:00:00";
		date = DateTimeUtils.toDate(ll);
		return date;
	}

	/**
	 * 输入结束时间串：yyyy-mm-dd,输出时间戳类型日期：yyyy-mi-dd
	 * 
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	public static Date EndTime(String str) throws ParseException {
		// 23:59:59
		String ll = null;
		Date date = null;
		ll = str + " 23:59:59";
		date = DateTimeUtils.toDate(ll);
		return date;
	}

	/**
	 * 去掉日期型中的时间部分（时间部分变成 00:00:00）
	 * 
	 * @param date
	 * @return
	 */
	public static Date cutTimePart(Date date) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		rightNow.set(Calendar.HOUR_OF_DAY, 00);
		rightNow.set(Calendar.MINUTE, 00);
		rightNow.set(Calendar.SECOND, 00);
		rightNow.set(Calendar.MILLISECOND, 000);
		return rightNow.getTime();

	}

	/**
	 * 取得日期中的最小时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMinDate(Date date) {
		return cutTimePart(date);
	}

	/**
	 * 取得日期中的最大时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMaxDate(Date date) {
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		rightNow.set(Calendar.HOUR_OF_DAY, 23);
		rightNow.set(Calendar.MINUTE, 59);
		rightNow.set(Calendar.SECOND, 59);
		rightNow.set(Calendar.MILLISECOND, 999);
		return rightNow.getTime();
	}

	/**
	 * 取得下个日期中的最小时间
	 * 
	 * @param date
	 * @return
	 */
	public static Date getNextDayMinDate(Date date) {
		long dateLong = date.getTime() + 24 * 60 * 60 * 1000;
		date = new Date(dateLong);
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(date);
		rightNow.set(Calendar.HOUR_OF_DAY, 00);
		rightNow.set(Calendar.MINUTE, 00);
		rightNow.set(Calendar.SECOND, 00);
		rightNow.set(Calendar.MILLISECOND, 000);
		return rightNow.getTime();
	}

	/**
	 * 日期处理模块 (将日期加上某些天或减去天数)返回字符串
	 * 
	 * @param strTo
	 * @return
	 */
	@SuppressWarnings("static-access")
	public static String dateAddFormat(int strTo, String format) {
		Calendar strDate = Calendar.getInstance(); // java.util包,设置当前时间
		strDate.add(strDate.DATE, strTo); // 日期减 如果不够减会将月变动 //生成 (年-月-日)
		SimpleDateFormat sy1 = new SimpleDateFormat(format);
		String meStrDate = sy1.format(strDate.getTime());
		return meStrDate;
	}

	/**
	 * 获取明天
	 * 
	 * @param dateSrc
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("static-access")
	public static String nextDay(String dateSrc, String format)
			throws ParseException {
		Calendar strDate = Calendar.getInstance(); // java.util包,设置当前时间
		Date date = toDate(dateSrc);
		strDate.setTime(date);
		strDate.add(strDate.DATE, 1);
		SimpleDateFormat sy1 = new SimpleDateFormat(format);
		String meStrDate = sy1.format(strDate.getTime());
		return meStrDate;
	}

	/**
	 * 取当前日期字符串
	 */
	public static String getCurrDateStr(String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(new Date(System.currentTimeMillis()));
	}

	/**
	 * 输入两个时间，输出两个时间的相差秒数
	 * 
	 * @param sTime
	 * @param eTime
	 * @return
	 */
	public static int getDistance(Date sTime, Date eTime) {
		long a = sTime.getTime();
		long b = eTime.getTime();
		int c = (int) ((a - b) / 1000);
		return c;
	}

	/**
	 * 比较两个日期时间的大小
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDateTime(Date date1, Date date2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date1).compareTo(df.format(date2));
	}

	/**
	 * 比较两个时间的大小
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareTime(Date date1, Date date2) {
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		return df.format(date1).compareTo(df.format(date2));
	}

	/**
	 * 比较两个日期的大小
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(Date date1, Date date2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(date1).compareTo(df.format(date2));
	}

	/**
	 * 输入两个日期时间，输出相隔的秒数
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static long dateDifference(Date startTime, Date endTime) {
		long diff = endTime.getTime() - startTime.getTime();
		return diff / 1000;
	}

	/**
	 * 输入两个时间类型，将第二个的日期换成第一个，并返回第二个时间类型
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date changeDate(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);
		int year = c1.get(Calendar.YEAR);
		int month = c1.get(Calendar.MONTH);
		int date = c1.get(Calendar.DAY_OF_MONTH);
		c2.set(year, month, date);
		return c2.getTime();
	}

	/**
	 * 输入两个时间类型，将第二个的日期换成第一个时间的日期，再加上一天，并返回第二个时间类型
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static Date changeDatePlusOneday(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		c1.setTime(date1);
		c2.setTime(date2);
		int year = c1.get(Calendar.YEAR);
		int month = c1.get(Calendar.MONTH);
		int date = c1.get(Calendar.DAY_OF_MONTH);
		c2.set(year, month, date);
		c2.add(Calendar.DAY_OF_YEAR, 1);
		return c2.getTime();
	}

	/**
	 * 输入总秒数，输出对应秒数为-天-小时-分钟-秒
	 * 
	 * @param sec
	 * @return
	 */
	public static String sec2DayHourMin(long sec) {
		StringBuffer buffer = new StringBuffer();
		int day = (int) sec / 24 / 60 / 60;
		int hour = (int) (sec % (24 * 60 * 60) / (60 * 60));
		int min = (int) (sec % (24 * 60 * 60) % (60 * 60) / 60);
		int second = (int) (sec % (24 * 60 * 60) % (60 * 60) % 60);
		buffer.append(day);
		buffer.append("天");
		buffer.append(hour);
		buffer.append("小时");
		buffer.append(min);
		buffer.append("分钟");
		buffer.append(second);
		buffer.append("秒");
		return buffer.toString();
	}

	/**
	 * 两个时间段有效交集的情况
	 * 
	 * 
	 * @param d1s
	 *            时段1开始
	 * @param d1e
	 *            时段1结束
	 * @param d2s
	 *            时段2开始
	 * @param d2e
	 *            时段2结束
	 */
	public void commonCompare(Date d1s, Date d1e, Date d2s, Date d2e) {
		boolean b1 = DateTimeUtils.compareDateTime(d1s, d2s) <= 0;
		boolean b2 = DateTimeUtils.compareDateTime(d1s, d2e) <= 0;
		boolean b3 = DateTimeUtils.compareDateTime(d1e, d2s) <= 0;
		boolean b4 = DateTimeUtils.compareDateTime(d1e, d2e) <= 0;
		// boolean b11 = DateTimeFormatUtil.compareDateTime(d1s, d2s)<0;
		// boolean b21 = DateTimeFormatUtil.compareDateTime(d1s, d2e)<0;
		boolean b31 = DateTimeUtils.compareDateTime(d1e, d2s) < 0;
		// boolean b41 = DateTimeFormatUtil.compareDateTime(d1e, d2e)<0;
		// 有交集的所有情况
		if (b1 && b2 && !b3 && b4) {
			// d1e-d2s
		} else if (b1 && b2 && !b3 && !b4) {
			// d2e-d2s
		} else if (!b1 && b2 && !b3 && b4) {
			// d1e-d1s
		} else if (!b1 && b2 && !b3 && !b4) {
			// d2e-d1s
		} else {
		}
		// 有交集的充分条件
		if (b2 && !b3) {
			// 则可以断定两个时段有交集
		} else {
		}
		// 没有交集的充分条件
		if (b31 || !b2) {
			//
		}
	}

	/**
	 * 遍历d1 到 d2 之间的每一天
	 * 
	 * @param d1
	 * @param d2
	 */
	public void forReturn(Date d1, Date d2) {
		// 从起始日期遍历到结束日期
		Calendar dayc1 = new GregorianCalendar();
		Calendar dayc2 = new GregorianCalendar();
		// 设置calendar的日期
		dayc1.setTime(d1);
		dayc2.setTime(d2);

		for (; dayc1.compareTo(dayc2) <= 0;) {
			// dayc1在dayc2之前就循环
			StringBuffer buffer = new StringBuffer();
			buffer.append(dayc1.get(Calendar.YEAR));
			buffer.append("-");
			if (dayc1.get(Calendar.MONTH) < 9) {
				buffer.append("0");
				buffer.append(dayc1.get(Calendar.MONTH) + 1);
			} else {
				buffer.append(dayc1.get(Calendar.MONTH) + 1);
			}
			buffer.append("-");
			if (dayc1.get(Calendar.DATE) < 10) {
				buffer.append("0");
				buffer.append(dayc1.get(Calendar.DATE));
			} else {
				buffer.append(dayc1.get(Calendar.DATE));
			}
			dayc1.add(Calendar.DAY_OF_YEAR, 1);
			// 加1天
		}
	}

	/**
	 * java中对日期的加减操作 GregorianCalendar类的add(int field,int amount)方法表示年月日加减.
	 * field参数表示年,月.日等 field = 1 年 field = 2 月 field = 3 周 field = 5 日
	 * amount参数表示要加减的数量,取正加，取负减
	 */

	public static Date dayPlus(Date date, int field, int amount) {
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTime(date);
		gc.add(field, amount);
		return gc.getTime();
	}

	public static Timestamp getToday() throws ParseException {
		String Is_date = "";
		GregorianCalendar obj_date = new GregorianCalendar();
		Is_date = Is_date + obj_date.get(Calendar.YEAR);
		Is_date = Is_date + "-" + (obj_date.get(Calendar.MONTH) + 1);
		Is_date = Is_date + "-" + obj_date.get(Calendar.DATE);
		Is_date = Is_date + " " + obj_date.get(Calendar.HOUR_OF_DAY);
		Is_date = Is_date + ":" + obj_date.get(Calendar.MINUTE);
		Is_date = Is_date + ":" + obj_date.get(Calendar.SECOND);
		java.text.SimpleDateFormat f = new java.text.SimpleDateFormat(
				"yyyy-MM-dd hh:mm:ss");
		java.util.Date date;
		date = f.parse(Is_date);
		Timestamp t = new Timestamp(date.getTime());
		return t;
	}

	public static String TimeStamp2Str(Timestamp date) {
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
		String s = sim.format(date);
		return s;
	}

	public static Timestamp Str2Timestamp(String d) {
		Date date = new Date();
		SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd");
		try {
			date = sim.parse(d);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Timestamp t = new Timestamp(date.getTime());
		return t;
	}

	/**
	 * 获取当前时间戳
	 * 
	 * @return
	 */
	public static long getTimeStamp() {
		return new Date().getTime();
	}

	/**
	 * 将指定格式的时间字符串转化成long类型时间戳
	 * 
	 * @param time
	 * @param fmt
	 * @return
	 * @throws ParseException
	 */
	public static long Str2TimeStamp(String time, String fmt)
			throws ParseException {
		return new SimpleDateFormat(fmt).parse(time).getTime();
	}

}
