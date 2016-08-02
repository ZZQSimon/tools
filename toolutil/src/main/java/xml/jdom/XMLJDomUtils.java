package xml.jdom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 * JDom读写xml
 * 
 * @author whwang
 */
public class XMLJDomUtils {
	public static void main(String[] args) {
		read();
		// write();
	}

	static StringBuffer jsonBuffer;

	public static void read() {
		try {
			SAXBuilder builder = new SAXBuilder();
			InputStream in = XMLJDomUtils.class.getClassLoader()
					.getResourceAsStream("test.xml");
			Document doc = builder.build(in);
			Element root = doc.getRootElement();
			jsonBuffer = new StringBuffer();
			jsonBuffer.append("{");
			readNode(root, "");
			jsonBuffer.append("}");
			System.out.println(jsonBuffer.toString());
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readNode(Element root, String prefix) {
		if (root == null)
			return;

		boolean flag = "".equals(prefix);
		if (flag) {
			jsonBuffer.append("\"" + root.getName() + "\":[");
		} else {
			jsonBuffer.append("\"" + root.getName() + "\":");
		}
		// 获取属性
		List<Attribute> attrs = root.getAttributes();
		if (attrs != null && attrs.size() > 0) {
			jsonBuffer.append("{");
			// System.out.print(prefix);
			for (Attribute attr : attrs) {
				jsonBuffer.append("\"" + attr.getName() + "\":\""
						+ attr.getValue() + "\",");
				// System.out.println(attr.getName()+":"+attr.getValue());
			}
		}
		jsonBuffer.append("\"children\":[{");
		// 获取他的子节点
		List<Element> childNodes = root.getChildren();
		prefix += "\t";
		for (Element e : childNodes) {
			readNode(e, prefix);
		}
		jsonBuffer.append("}]");
		if (flag) {
			jsonBuffer.append("]");
		} else {
			jsonBuffer.append("");
		}
	}

	public static void write() {
		boolean validate = false;
		try {
			@SuppressWarnings("deprecation")
			SAXBuilder builder = new SAXBuilder(validate);
			InputStream in = XMLJDomUtils.class.getClassLoader()
					.getResourceAsStream("test.xml");
			Document doc = builder.build(in);
			// 获取根节点 <university>
			Element root = doc.getRootElement();
			// 修改属性
			root.setAttribute("name", "tsu");
			// 删除
			boolean isRemoved = root.removeChildren("college");
			System.err.println(isRemoved);
			// 新增
			Element newCollege = new Element("college");
			newCollege.setAttribute("name", "new_college");
			Element newClass = new Element("class");
			newClass.setAttribute("name", "ccccc");
			newCollege.addContent(newClass);
			root.addContent(newCollege);
			XMLOutputter out = new XMLOutputter();
			File file = new File("src/jdom-modify.xml");
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			out.output(doc, fos);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
