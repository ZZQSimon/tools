package com.g1extend.service.impl;

import com.g1extend.entity.EmailTemplate;
import com.g1extend.entity.ProbationExpire;
import com.g1extend.mapper.ProbationRemindDao;
import com.g1extend.service.ProbationRemindService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProbationRemindServiceImpl implements ProbationRemindService {
    @Autowired
    private ProbationRemindDao probationRemindDao;

    public List<ProbationExpire> getALLProbationExpire(Date date) {
        return this.probationRemindDao.getALLProbationExpire(date);
    }

    public String getDirectSupervisorEmail(String directSupervisor) {
        return this.probationRemindDao.getDirectSupervisorEmail(directSupervisor);
    }

    public EmailTemplate getEmailTemplate() {
        return this.probationRemindDao.getEmailTemplate();
    }
}

/*
 * Location: E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend
 * 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\impl\
 * ProbationRemindServiceImpl.class Java compiler version: 8 (52.0) JD-Core
 * Version: 1.0.6
 */