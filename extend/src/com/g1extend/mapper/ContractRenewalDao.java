package com.g1extend.mapper;

import com.g1extend.entity.ContractRenewal;
import com.g1extend.entity.EmailTemplate;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ContractRenewalDao {
  String getApproveId(@Param("dataId") String paramString);
  
  String getDistinctCount(@Param("approveId") String paramString);
  
  String getCount(@Param("approveId") String paramString);
  
  List<ContractRenewal> getAllUser(@Param("approveId") String paramString);
  
  String getEmail(@Param("userId") String paramString);
  
  String getEmpName(@Param("userId") String paramString);
  
  EmailTemplate getEmailTemplate();
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\mapper\ContractRenewalDao.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */