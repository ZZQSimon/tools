 package cn.com.easyerp.module.interfaces.export;
 
 import java.util.HashMap;
import java.util.Map;
 
 public class Entry
 {
   Map<String, String> elements;
   
   public Entry() { this.elements = new HashMap<>(); }
 
 
   
   public Entry(Map<String, String> attrs, Map<String, String> elements) { this.elements = elements; }
 
 
 
   
   public Map<String, String> getElements() { return this.elements; }
 
 
   
   public void setElements(Map<String, String> elements) { this.elements = elements; }
 
 
   
   public void addElement(String key, String value) { this.elements.put(key, value); }
 }


