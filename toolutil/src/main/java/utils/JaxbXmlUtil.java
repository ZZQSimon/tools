/**
 * 
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * 
 * @createDate May 20, 2010 1:13:13 PM
 * @author zy
 * @version 1.0.0
 * @注释 JAXB操作XML
 */
public class JaxbXmlUtil {

	public static boolean objectToXmlFile(Object object, String filePath) {
		boolean bResult = false;
		// 文件目录
		File file = new File(filePath);
		// 文件否
		if (file.isDirectory()) {
			// 目录自动创建，并文件名自动获取，文件名使用类名
			if (!file.exists()) {
				if (!file.mkdir()) {
					System.out.println("[" + JaxbXmlUtil.class.getName() + "]目录创建失败!!!");
					return false;
				}
			}
			if (!filePath.endsWith("/") || !filePath.endsWith("\\")) {
				filePath = filePath + System.getProperty("file.separator");
			}
			filePath = filePath + object.getClass().getSimpleName() + ".xml";
		} else {
			int fileSeparatorIndex = filePath.lastIndexOf(System.getProperty("file.separator"));
			String dirStr = filePath.substring(0, fileSeparatorIndex);
			File testFile = new File(dirStr);
			if (!testFile.exists()) {
				if (!testFile.mkdir()) {
					System.out.println("[" + JaxbXmlUtil.class.getName() + "]目录创建失败!!!");
					return false;
				}
			}
		}

		File xmlFile = new File(filePath);
		try {
			JAXBContext context = JAXBContext.newInstance(object.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.marshal(object, new FileOutputStream(xmlFile));
			bResult = true;
		} catch (JAXBException e) {
			System.out.println("JAXB失败!!!");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("文件未找到!!!");
			e.printStackTrace();
		}
		return bResult;
	}

	public static Object xmlFileToObject(Class<?> class1, String filePath) {
		Object rObject = null;
		File xmlFile = new File(filePath);
		if (xmlFile.isDirectory()) {
			if (!filePath.endsWith("/") || !filePath.endsWith("\\")) {
				filePath = filePath + System.getProperty("file.separator");
			}
			filePath = filePath + class1.getSimpleName() + ".xml";
			xmlFile = new File(filePath);
			if (!xmlFile.exists()) {
				System.out.println("文件不存在!!!");
				return null;
			}
		} else {
			if (!xmlFile.exists()) {
				System.out.println("文件不存在!!!");
				return null;
			}
		}
		try {
			JAXBContext context = JAXBContext.newInstance(class1);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			rObject = unmarshaller.unmarshal(xmlFile);
		} catch (JAXBException e) {
			System.out.println("JAXB失败!!!");
			e.printStackTrace();
		}
		return rObject;
	}
}
