/**
 * 
 */
package string;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @createDate May 31, 2010 9:44:56 AM
 * @author zy
 * @version 1.0.0
 * @注释 字符串填充
 */
public class StringUtil {

	/**
	 * 去除字符串首尾出现的某个字符.
	 * 
	 * @param source
	 *            源字符串.
	 * @param element
	 *            需要去除的字符.
	 * @return String.
	 */
	public static String trimFirstAndLastChar(String source, char element) {
		boolean beginIndexFlag = true;
		boolean endIndexFlag = true;
		do {
			int beginIndex = source.indexOf(element) == 0 ? 1 : 0;
			int endIndex = source.lastIndexOf(element) + 1 == source.length() ? source
					.lastIndexOf(element) : source.length();
			source = source.substring(beginIndex, endIndex);
			beginIndexFlag = (source.indexOf(element) == 0);
			endIndexFlag = (source.lastIndexOf(element) + 1 == source.length());
		} while (beginIndexFlag || endIndexFlag);
		return source;
	}

	/**
	 * 截取报文
	 * 
	 * @param src
	 *            源
	 * @param startLen
	 *            起始位置
	 * @param len
	 *            截取长度
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public static String interceptProtocolByte(byte[] src, int startLen, int len)
			throws UnsupportedEncodingException {
		return interceptProtocolByte(src, startLen, len, "");
	}

	/**
	 * 截取报文
	 * 
	 * @param src
	 *            源
	 * @param startLen
	 *            起始位置
	 * @param len
	 *            截取长度
	 * @param charsetName
	 *            字符集格式
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String interceptProtocolByte(byte[] src, int startLen,
			int len, String charsetName) throws UnsupportedEncodingException {

		String result = "";

		// 设置临时报文存储
		byte[] tmpByte = new byte[len];
		// 截取报文长度内容
		System.arraycopy(src, startLen, tmpByte, 0, len);

		if (charsetName == null || charsetName.equals("")) {
			// 默认当前系统字符集
			result = new String(tmpByte).trim();
		} else {
			// 指定字符集
			result = new String(tmpByte, charsetName).trim();
		}
		return result;
	}

	/**
	 * 截取报文- 默认中文为2位，其他为1位
	 * 
	 * @param String
	 * @param startLen
	 *            起始位置
	 * @param len
	 *            截取长度
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	public static String interceptProtocolString(String command, int startLen,
			int len) {
		// 重新计算开始长度，先运算startLen位置前的字符长度(主要计算中文)
		for (int i = 0, j = 0; i < startLen; i++, j++) {
			int ii = (int) command.charAt(j);
			// 只支持ASCII表中的0-126
			if (ii <= 0 || ii >= 126) {
				// 如果是汉字-1
				startLen--;
				i++;
			}
		}
		int interceptLen = len;
		// 计算截取内容长度（主要计算中文）
		for (int i = startLen, j = startLen; i < startLen + len; i++, j++) {
			int ii = (int) command.charAt(j);
			// 只支持ASCII表中的0-126
			if (ii <= 0 || ii >= 126) {
				// 如果是汉字-1
				interceptLen--;
				i++;
			}
		}
		return command.substring(startLen, startLen + interceptLen);
	}

	/**
	 * 计算字符串长度，中文2位，其它一位
	 * 
	 * @param command
	 * @return int
	 */
	public static int getStringLength(String command) {
		int len = 0;
		// 计算长度
		for (int i = 0; i < command.length(); i++) {
			int ii = (int) command.charAt(i);
			// 只支持ASCII表中的0-126
			if (ii <= 0 || ii >= 126) {
				// 如果是汉字-1
				len = len + 2;
			} else {
				len++;
			}
		}
		return len;
	}

	/**
	 * 取字符长度
	 * 
	 * @param src
	 *            源
	 * @param len
	 *            截取长度
	 * @return
	 */
	public static String FirstNBytes(String src, int len) {
		// UTF-8,中文编码范围
		Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5]$");
		int i = 0, j = 0;
		for (char c : src.toCharArray()) {
			Matcher m = p.matcher(String.valueOf(c));
			i += m.find() ? 2 : 1;
			++j;
			if (i == len)
				break;
			if (i > len) {
				--j;
				break;
			}
		}
		return src.substring(0, j);
	}

	/**
	 * 
	 * @createDate May 31, 2010 10:07:38 AM
	 * @author zy
	 * @version 1.0.0
	 * @注释 填充位置
	 */
	public enum FILL_WAY {
		LEFT, RIGHT;
	}

	/**
	 * 填充字符
	 * 
	 * @param src
	 *            源串
	 * @param len
	 *            总长度
	 * @param fillChar
	 *            填充字符
	 * @param fill_way
	 *            填充方向（左，右填充）
	 * @return
	 */
	private static String getFill(String src, int len, char fillChar,
			FILL_WAY fill_way) {
		src = (src == null) ? src = "" : src;
		String strResult = "";
		try {

			// 取字符串长度,通过ASCII码0-126的打印字符为单字节，此外的打印字符多为两字节（三字节的不太用）。
			int length = 0;
			for (int i = 0; i < src.length(); i++) {
				length++;
				int ii = (int) src.charAt(i);
				if (ii <= 0 || ii >= 126)
					length++;// 双字节字符
			}
			// 长度相同则直接返回
			if (length == len) {
				return src;
			}
			// 长度大于指定长度，则截取，默认为左
			if (length > len) {
				byte[] tmpByte = new byte[len];
				System.arraycopy(src.getBytes(), 0, tmpByte, 0, len);
				return new String(tmpByte);
			}
			// 长度小于指定长度，按要求补位
			if (length < len) {
				int fillLen = len - length;
				String tmp = "";
				for (int i = 0; i < fillLen; i++) {
					tmp = tmp + fillChar;
				}
				if (fill_way.equals(FILL_WAY.LEFT)) {
					src = tmp + src;
				} else {
					src = src + tmp;
				}
			}
			strResult = src;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * 右对齐,左补0
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getFillLeftZero(String str, int len) {
		String strResult = "";
		try {
			// 右对齐,左补0
			String temp = getFill(str, len, '0', FILL_WAY.LEFT);
			strResult = temp;
		} catch (Exception e) {
		}
		return strResult;
	}

	/**
	 * 左对齐,右补0
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getFillRightZero(String str, int len) {
		String strResult = "";
		try {
			// 左对齐,右补0
			String temp = getFill(str, len, '0', FILL_WAY.RIGHT);
			strResult = temp;
		} catch (Exception e) {
		}
		return strResult;
	}

	/**
	 * 左对齐,右补空格
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getFillRightSpace(String str, int len) {
		String strResult = "";
		try {
			// 左对齐,右补空格
			String temp = getFill(str, len, ' ', FILL_WAY.RIGHT);
			strResult = temp;
		} catch (Exception e) {
		}
		return strResult;
	}

	/**
	 * 右对齐,左补空格
	 * 
	 * @param str
	 * @param len
	 * @return
	 */
	public static String getFillLeftSpace(String str, int len) {
		String strResult = "";
		try {
			// 右对齐,左补空格
			String temp = getFill(str, len, ' ', FILL_WAY.LEFT);
			strResult = temp;
		} catch (Exception e) {
		}
		return strResult;
	}

	/**
	 * 查看字符串在文件中出现的次数
	 * 
	 * @param filename
	 * @param target
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * 
	 * @since 2015-07-21 14:47:08
	 */
	public static int count(String filename, String target)
			throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		StringBuilder strb = new StringBuilder();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			strb.append(line);
		}
		String result = strb.toString();
		int count = 0;
		int index = 0;
		while (true) {
			index = result.indexOf(target, index + 1);
			if (index > 0) {
				count++;
			} else {
				break;
			}
		}
		br.close();
		fr.close();
		return count;
	}

}
