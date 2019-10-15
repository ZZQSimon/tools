 package cn.com.easyerp.module.interfaces.xmlAnalysis;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
 
 public class XMLHandler
   implements ContentHandler
 {
   String recordSpliter;
   List<String> fields;
   String currentTag;
   boolean isSpliter = false;
   int repeated = 0;
   
   Map<String, String> record;
   
   List<Map<String, String>> recordList;
   
   public XMLHandler(List<String> fields) {
     this.fields = fields;
     this.currentTag = "";
   }
   public XMLHandler() {
     this.fields = null;
     this.currentTag = "";
   }
 
 
   
   public List<Map<String, String>> getXMLContent() { return this.recordList; }
 
 
 
   
   public void characters(char[] ch, int start, int length) throws SAXException {
     String content = new String(ch, start, length);
     if (this.recordSpliter != null && this.recordSpliter.equals(this.currentTag) && this.repeated == 0) {
       return;
     }
     
     if (this.record != null && !this.record.containsKey(this.currentTag) && (
       this.fields == null || this.fields.size() == 0 || this.fields.contains(this.currentTag))) {
       this.record.put(this.currentTag, content);
     }
   }
 
 
 
 
   
   public void endDocument() {}
 
 
 
 
   
   public void endElement(String uri, String localName, String qName) throws SAXException {
     if (this.recordSpliter != null && this.recordSpliter.equals(qName)) {
       if (this.repeated == 0) {
         this.recordList.add(this.record);
         this.record = null;
         this.currentTag = "";
       }
       else {
         
         this.repeated--;
       } 
     }
   }
 
 
 
 
   
   public void endPrefixMapping(String arg0) throws SAXException {}
 
 
 
 
   
   public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {}
 
 
 
 
   
   public void processingInstruction(String arg0, String arg1) throws SAXException {}
 
 
 
   
   public void setDocumentLocator(Locator arg0) {}
 
 
 
   
   public void skippedEntity(String arg0) throws SAXException {}
 
 
 
   
   public void startDocument() { this.recordList = new ArrayList<>(); }
 
 
 
 
 
   
   public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
     this.currentTag = qName;
 
     
     if (this.isSpliter) {
       this.isSpliter = false;
       this.recordSpliter = qName;
     } 
     if ("ufinterface".equals(qName)) {
       this.isSpliter = true;
     }
     
     if (this.recordSpliter != null && this.recordSpliter.equals(qName))
       if (this.record == null) {
         this.record = new HashMap<>();
       }
       else {
         
         this.repeated++;
       }  
   }
   
   public void startPrefixMapping(String arg0, String arg1) throws SAXException {}
 }


