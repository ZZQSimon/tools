package utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 * 
 * @createDate Apr 8, 2011 9:52:30 AM
 * @author zy
 * @version 1.0.0
 * @modifyAuthor
 * @modifyDate
 */
public class PropertiesUtil {

	/**
	 * 读取属性文件
	 * 
	 * @param filePath
	 *            文件路径
	 * @return Map<String, String>
	 * @throws IOException
	 */
	public static Map<String, String> loadProperties(String filePath) throws IOException {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (filePath == null || filePath.equals("")) {
			throw new NullPointerException("filePath参数不能是NULL.");
		}
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(filePath));
			Properties prop = new Properties();
			prop.load(is);
			Iterator<Entry<Object, Object>> it = prop.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Object, Object> entry = (Entry<Object, Object>) it.next();
				String key = (String) entry.getKey();
				String value = (String) entry.getValue();
				resultMap.put(key, value);
			}
		} catch (FileNotFoundException e1) {
			throw new FileNotFoundException(filePath + " 文件不存在.");
		} catch (IOException e) {
			throw new IOException(filePath + " 读取文件异常.");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new IOException(filePath + " 文件关闭失败.");
			}
		}
		return resultMap;
	}

	/**
	 * 读取属性值
	 * 
	 * @param filePath
	 *            文件路径
	 * @param key
	 *            键
	 * @return value
	 * @throws IOException
	 */
	public static String loadPropertie(String filePath, String key) throws IOException {
		String result = "";
		if (filePath == null || filePath.equals("")) {
			throw new NullPointerException("filePath参数不能是NULL.");
		}
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(filePath));
			Properties prop = new Properties();
			prop.load(is);
			result = prop.getProperty(key);
		} catch (FileNotFoundException e1) {
			throw new FileNotFoundException(filePath + " 文件不存在.");
		} catch (IOException e) {
			throw new IOException(filePath + " 读取文件异常.");
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new IOException(filePath + " 文件关闭失败.");
			}
		}
		return result;
	}

	public static boolean addNewPropertites(String filePath, String key, String value) throws IOException {
		return PropertiesUtil.addNewProperties(filePath, key, value, "");
	}

	/**
	 * 新加属性值
	 * 
	 * @param filePath
	 *            文件路径
	 * @param key
	 *            属性键
	 * @param value
	 *            属性值
	 * @param comments
	 *            属性注释
	 * @return boolean true,false
	 * @throws IOException
	 */
	public static boolean addNewProperties(String filePath, String key, String value, String comments)
			throws IOException {
		boolean result = false;
		Properties props = new Properties();
		OutputStream ops;
		try {
			File file = new File(filePath);
			if (file.exists() == false) {
				if (file.createNewFile() == false) {
					// 创建失败
					throw new IOException(filePath + "文件创建失败.");
				}
			}
			ops = new FileOutputStream(filePath);
			props.setProperty(key, value);
			props.store(ops, comments);
			result = true;
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(filePath + "文件不存在.");
		} catch (IOException e) {
			throw new IOException(filePath + "写文件失败.");
		}
		return result;
	}

	/**
	 * 追加属性
	 * 
	 * @param filePath
	 *            文件路径
	 * @param key
	 *            属性键
	 * @param value
	 *            属性值
	 * @return boolean true,false
	 * @throws IOException
	 */
	public static boolean appendProperties(String filePath, String key, String value) throws IOException {
		return PropertiesUtil.appendProperties(filePath, key, value, "");
	}

	/**
	 * 追加属性
	 * 
	 * @param filePath
	 *            文件路径
	 * @param key
	 *            属性键
	 * @param value
	 *            属性值
	 * @param comments
	 *            属性注释
	 * @return boolean true,false
	 * @throws IOException
	 */
	public static boolean appendProperties(String filePath, String key, String value, String comments)
			throws IOException {
		boolean result = false;
		Properties props = new Properties();
		try {
			OutputStream ops = new FileOutputStream(filePath);
			props.setProperty(key, value);
			props.store(ops, comments);
			result = true;
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException(filePath + "文件不存在.");
		} catch (IOException e) {
			throw new IOException(filePath + "写文件失败.");
		}
		return result;
	}
}
