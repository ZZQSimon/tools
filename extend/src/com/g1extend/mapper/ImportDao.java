package com.g1extend.mapper;

import com.g1extend.entity.MonthlyIncentiveReport;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImportDao {
  void importFile(@Param("list") List<MonthlyIncentiveReport> paramList);
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\mapper\ImportDao.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */