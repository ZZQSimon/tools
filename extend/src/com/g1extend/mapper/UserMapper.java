package com.g1extend.mapper;

import com.g1extend.entity.User;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserMapper {
  void save(@Param("user") User paramUser);
  
  boolean update(@Param("user") User paramUser);
  
  boolean delete(@Param("id") int paramInt);
  
  User findById(@Param("id") int paramInt);
  
  List<User> findAll();
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\mapper\UserMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */