package com.g1extend.service;

import com.g1extend.entity.ContractRenewal;
import com.g1extend.entity.EmailTemplate;
import java.util.List;

public interface ContractRenewalService {
  String getApproveId(String paramString);
  
  String getDistinctCount(String paramString);
  
  String getCount(String paramString);
  
  List<ContractRenewal> getAllUser(String paramString);
  
  String getEmail(String paramString);
  
  EmailTemplate getEmailTemplate();
  
  String getEmpName(String paramString);
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\ContractRenewalService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */