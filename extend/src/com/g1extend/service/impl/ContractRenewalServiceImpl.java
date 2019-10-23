package com.g1extend.service.impl;

import com.g1extend.entity.ContractRenewal;
import com.g1extend.entity.EmailTemplate;
import com.g1extend.mapper.ContractRenewalDao;
import com.g1extend.service.ContractRenewalService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContractRenewalServiceImpl implements ContractRenewalService {
    @Autowired
    private ContractRenewalDao contractRenewalDao;

    public String getApproveId(String dataId) {
        return this.contractRenewalDao.getApproveId(dataId);
    }

    public String getDistinctCount(String approveId) {
        return this.contractRenewalDao.getDistinctCount(approveId);
    }

    public String getCount(String approveId) {
        return this.contractRenewalDao.getCount(approveId);
    }

    public List<ContractRenewal> getAllUser(String approveId) {
        return this.contractRenewalDao.getAllUser(approveId);
    }

    public String getEmail(String userId) {
        return this.contractRenewalDao.getEmail(userId);
    }

    public EmailTemplate getEmailTemplate() {
        return this.contractRenewalDao.getEmailTemplate();
    }

    public String getEmpName(String userId) {
        return this.contractRenewalDao.getEmpName(userId);
    }
}

/*
 * Location: E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend
 * 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\impl\
 * ContractRenewalServiceImpl.class Java compiler version: 8 (52.0) JD-Core
 * Version: 1.0.6
 */