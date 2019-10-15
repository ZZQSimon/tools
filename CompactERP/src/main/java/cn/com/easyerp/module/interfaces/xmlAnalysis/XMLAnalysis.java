 package cn.com.easyerp.module.interfaces.xmlAnalysis;
 
 import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
 
 
 
 
 
 public class XMLAnalysis
 {
   public List<Map<String, String>> getContent(String XMLPath, List<String> fields) {
     SAXParserFactory sf = SAXParserFactory.newInstance();
     XMLHandler handler = new XMLHandler(fields);
     try {
       SAXParser sp = sf.newSAXParser();
       XMLReader reader = sp.getXMLReader();
       reader.setContentHandler(handler);
       reader.parse(XMLPath);
     } catch (ParserConfigurationException e) {
       e.printStackTrace();
     } catch (SAXException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     } 
     
     return handler.getXMLContent();
   }
 
   
   public List<Map<String, String>> getContent(InputStream inputStream, List<String> fields) {
     SAXParserFactory sf = SAXParserFactory.newInstance();
     XMLHandler handler = new XMLHandler(fields);
     try {
       SAXParser sp = sf.newSAXParser();
       XMLReader reader = sp.getXMLReader();
       reader.setContentHandler(handler);
       InputSource is = new InputSource(inputStream);
       reader.parse(is);
     } catch (ParserConfigurationException e) {
       e.printStackTrace();
       return null;
     } catch (SAXException e) {
       e.printStackTrace();
       return null;
     } catch (IOException e) {
       e.printStackTrace();
       return null;
     } 
     
     return handler.getXMLContent();
   }
 }


