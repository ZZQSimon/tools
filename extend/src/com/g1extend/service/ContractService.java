package com.g1extend.service;

import com.g1extend.entity.Contract_renewal;
import com.g1extend.entity.Labor_contract;
import java.util.List;
import java.util.Map;

public interface ContractService {
  List<Labor_contract> getLaborContract(String paramString);
  
  int selectCountById(String paramString);
  
  Contract_renewal judgeContract(String paramString, int paramInt);
  
  Contract_renewal secondContract(String paramString);
  
  String getDomain();
  
  Map<String, Object> getSystemParam();
  
  int getNoSubmitNum();
  
  int updateCreUser();
  
  boolean sendEmail(List<Contract_renewal> paramList, String paramString);
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\ContractService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */