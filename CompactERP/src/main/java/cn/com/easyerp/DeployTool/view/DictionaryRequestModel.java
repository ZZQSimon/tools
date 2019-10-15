 package cn.com.easyerp.DeployTool.view;
 
 import java.util.List;

import cn.com.easyerp.DeployTool.service.DictionaryAddDetails;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 public class DictionaryRequestModel extends FormRequestModelBase<DictionaryModel> {
   private List<DictionaryAddDetails> dictionaryAdd;
   private String retrieveValue;
   private String dicId;
   
   public String getDicId() { return this.dicId; }
 
 
   
   public void setDicId(String dicId) { this.dicId = dicId; }
 
 
   
   public String getRetrieveValue() { return this.retrieveValue; }
 
 
   
   public void setRetrieveValue(String retrieveValue) { this.retrieveValue = retrieveValue; }
 
 
   
   public List<DictionaryAddDetails> getDictionaryAdd() { return this.dictionaryAdd; }
 
 
   
   public void setDictionaryAdd(List<DictionaryAddDetails> dictionaryAdd) { this.dictionaryAdd = dictionaryAdd; }
 }


