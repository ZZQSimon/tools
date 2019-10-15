package cn.com.easyerp.core.dao;

import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.data.AutoKeyDaoModel;

@Repository
public interface AutoKeyDao {
    void updateId(AutoKeyDaoModel paramAutoKeyDaoModel);

    void getId(AutoKeyDaoModel paramAutoKeyDaoModel);
}
