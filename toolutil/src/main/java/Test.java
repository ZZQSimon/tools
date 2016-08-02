import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {
	public enum DriverName  {
		AC,AMMETER,ATS,BATTERY,HEADERCABINET,GENERATOR,LEAK,TEMPHUM,NEWBLOWER,UPS
	}
	
	protected static final String name=DriverName.AC.toString();
	
	/**
	 * @param args
	 * @throws ParseException
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
//		System.out.println(Str2TimeStamp("2015-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(Str2TimeStamp("2015-01-01 00:30:00", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(Str2TimeStamp("2015-01-01 01:00:00", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(Str2TimeStamp("2015-01-01 01:30:00", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(Str2TimeStamp("2015-01-01 02:00:00", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(Str2TimeStamp("2015-01-01 02:30:00", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(Str2TimeStamp("2015-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
//		System.out.println(Str2TimeStamp("2015-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		System.out.println(getFormatDateStr(new Date(1453454401053l)));
	}
	
	public static long Str2TimeStamp(String time, String fmt)
			throws ParseException {
		return new SimpleDateFormat(fmt).parse(time).getTime();
	}
	
	public static String getFormatDateStr(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowDateStr = sdf.format(date);
		return nowDateStr;
	}
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
}
