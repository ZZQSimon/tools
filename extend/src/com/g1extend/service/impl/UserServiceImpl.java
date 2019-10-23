package com.g1extend.service.impl;

import com.g1extend.entity.User;
import com.g1extend.mapper.UserMapper;
import com.g1extend.service.UserService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper mapper;

    public boolean delete(int id) {
        return this.mapper.delete(id);
    }

    public List<User> findAll() {
        return this.mapper.findAll();
    }

    public User findById(int id) {
        return this.mapper.findById(id);
    }

    public void save(User user) {
        this.mapper.save(user);
    }

    public boolean update(User user) {
        return this.mapper.update(user);
    }
}

/*
 * Location: E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend
 * 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\impl\UserServiceImpl.
 * class Java compiler version: 8 (52.0) JD-Core Version: 1.0.6
 */