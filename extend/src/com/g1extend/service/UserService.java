package com.g1extend.service;

import com.g1extend.entity.User;
import java.util.List;

public interface UserService {
  void save(User paramUser);
  
  boolean update(User paramUser);
  
  boolean delete(int paramInt);
  
  User findById(int paramInt);
  
  List<User> findAll();
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\UserService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */