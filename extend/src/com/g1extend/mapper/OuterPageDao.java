package com.g1extend.mapper;

import com.g1extend.entity.CityMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OuterPageDao {
  CityMessage getCityMessage(@Param("citys") String paramString);
  
  String selectDataBase();
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\mapper\OuterPageDao.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */