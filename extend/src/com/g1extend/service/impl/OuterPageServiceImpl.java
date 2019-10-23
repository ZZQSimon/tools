package com.g1extend.service.impl;

import com.g1extend.entity.CityMessage;
import com.g1extend.mapper.OuterPageDao;
import com.g1extend.service.OuterPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OuterPageServiceImpl implements OuterPageService {
    @Autowired
    private OuterPageDao outerPageDao;

    public CityMessage getCityMessage(String citys) {
        CityMessage cityMessage = new CityMessage();
        cityMessage = this.outerPageDao.getCityMessage(citys);
        CityMessage cc = new CityMessage();
        cc.setPhone(cityMessage.getPhone());
        cc.setAddress(cityMessage.getAddress());
        cc.setContract(cityMessage.getContract());
        return cc;
    }

    public String selectDataBase() {
        return this.outerPageDao.selectDataBase();
    }
}

/*
 * Location: E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend
 * 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\impl\
 * OuterPageServiceImpl.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.0.6
 */