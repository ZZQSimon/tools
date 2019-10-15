package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.DictionaryDetails;

@Repository
public interface DictionaryDao {
  List<DictionaryDetails> selectDictionary(@Param("languageId") String paramString1, @Param("userId") String paramString2);
  
  List<DictionaryDetails> retrieveDictionary(@Param("retrieveValue") String paramString1, @Param("languageId") String paramString2, @Param("userId") String paramString3);
  
  List<DictionaryDetails> dictionaryById(@Param("dicId") String paramString1, @Param("userId") String paramString2);
  
  boolean addDictionary(@Param("dictionary") DictionaryDetails paramDictionaryDetails);
  
  boolean addInternational(@Param("international") DictionaryDetails paramDictionaryDetails);
  
  boolean deleteDicById(@Param("dicId") String paramString);
}


