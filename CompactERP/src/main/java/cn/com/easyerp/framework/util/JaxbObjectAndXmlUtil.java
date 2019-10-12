package cn.com.easyerp.framework.util;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import cn.com.easyerp.framework.listener.MarshallerListener;

/**
 * Jaxb2.0 处理Xml与Object转换 <br>
 * 
 * @date 2018.07.04 <br>
 * @author Simon Zhang
 */
public class JaxbObjectAndXmlUtil {

    /**
     * 转换xml为指定的对象
     * 
     * @param xmlStr
     *            字符串
     * @param c
     *            对象Class类型
     * @return 对象实例
     * @throws JAXBException
     */
    @SuppressWarnings("unchecked")
    public static <T> T xml2Object(String xmlStr, Class<T> c) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(c);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        T t = (T) unmarshaller.unmarshal(new StringReader(xmlStr));
        return t;
    }

    /**
     * 对象转XML
     * 
     * @param object
     *            对象
     * @return 返回xmlStr
     * @throws JAXBException
     */
    public static String object2Xml(Object object) throws JAXBException {
        StringWriter writer = new StringWriter();
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshal = context.createMarshaller();

        marshal.setListener(new MarshallerListener());
        marshal.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); // 格式化输出
        marshal.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// 编码格式,默认为utf-8
        marshal.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xml头信息
        marshal.marshal(object, writer);

        return new String(writer.getBuffer());
    }
}
