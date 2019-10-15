package cn.com.easyerp.addCompany;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.core.dao.AddCompanyDao;
import cn.com.easyerp.framework.common.Common;

@Service
public class AddCompanyService {
    @Autowired
    private AddCompanyDao addCompanyDao;
    @Autowired
    private AddCompanyUtil addCompanyUtil;

    public int createCompany(String companyId) {
        return createCompany(companyId, "HR");
    }

    @Transactional
    public int createCompany(String companyId, String module) {
        if (module == null || "".equals(module)) {
            this.addCompanyDao.createDB(companyId);
        } else {

            try {
                this.addCompanyDao.createDB(companyId);

                addModule(companyId, module);
            } catch (Exception e) {
                e.printStackTrace();
                Common.rollback();
            }
        }

        return 1;
    }

    public int addModule(String companyId, String module) {
        if (module == null || "".equals(module))
            return 0;
        this.addCompanyUtil.initCompany(companyId, module);
        return 1;
    }
}