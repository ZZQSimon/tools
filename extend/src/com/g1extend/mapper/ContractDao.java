package com.g1extend.mapper;

import com.g1extend.entity.Contract_renewal;
import com.g1extend.entity.Contract_renewalVO;
import com.g1extend.entity.Contract_renewal_email;
import com.g1extend.entity.EmailTemplate;
import com.g1extend.entity.Labor_contract;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractDao {
  List<Labor_contract> getLaborContract(@Param("getDate") String paramString);
  
  int selectCountById(@Param("employee_id") String paramString);
  
  void insertContract(@Param("contract_renewal") Contract_renewal paramContract_renewal);
  
  void insert2Contract(@Param("renewal") Contract_renewal paramContract_renewal);
  
  String getIdByApi(@Param("table_id") String paramString);
  
  String getDomain();
  
  Contract_renewalVO getVO(@Param("employee_id") String paramString, @Param("num") int paramInt);
  
  Map<String, Object> getSystemParam();
  
  int getNoSubmitNum();
  
  int updateCreUser();
  
  List<Contract_renewal_email> sendEmail(@Param("contract_renewal") List<Contract_renewal> paramList);
  
  EmailTemplate getEmailTemplate();
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\mapper\ContractDao.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */