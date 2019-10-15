 package cn.com.easyerp.DeployTool.service;
 
 import java.util.List;
 
 public class DictionaryAddDetails
 {
   private String dicId;
   
   public String getDicName() { return this.dicName; }
   private List<DictionaryDetails> dicList; private String dicName;
   
   public void setDicName(String dicName) { this.dicName = dicName; }
 
   
   public String getDicId() { return this.dicId; }
 
   
   public void setDicId(String dicId) { this.dicId = dicId; }
 
   
   public List<DictionaryDetails> getDicList() { return this.dicList; }
 
   
   public void setDicList(List<DictionaryDetails> dicList) { this.dicList = dicList; }
 }


