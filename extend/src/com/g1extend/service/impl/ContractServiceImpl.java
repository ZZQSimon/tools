package com.g1extend.service.impl;

import com.g1extend.entity.Contract_renewal;
import com.g1extend.entity.Contract_renewalVO;
import com.g1extend.entity.Contract_renewal_email;
import com.g1extend.entity.EmailTemplate;
import com.g1extend.entity.Labor_contract;
import com.g1extend.mail.MailDescribe;
import com.g1extend.mail.MailService;
import com.g1extend.mapper.ContractDao;
import com.g1extend.service.ContractService;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ContractServiceImpl implements ContractService {
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private MailService mailService;

    public List<Labor_contract> getLaborContract(String getDate) {
        return this.contractDao.getLaborContract(getDate);
    }

    public int selectCountById(String employee_id) {
        return this.contractDao.selectCountById(employee_id);
    }

    public Contract_renewal judgeContract(String employee_id, int num) {
        Contract_renewal contract_renewal = new Contract_renewal();
        Contract_renewalVO vo = new Contract_renewalVO();
        vo = this.contractDao.getVO(employee_id, num);
        String table_id = "t_contract_renewal";
        String id = this.contractDao.getIdByApi(table_id);
        contract_renewal.setId(id);
        if (vo == null) {
            return contract_renewal;
        }
        contract_renewal.setEmp_name(vo.getEmp_name());
        contract_renewal.setEmp_id(employee_id);
        contract_renewal.setApprove_status("1");
        contract_renewal.setCity(vo.getCity());
        contract_renewal.setRank(vo.getRank());
        contract_renewal.setPosition(vo.getPosition());
        contract_renewal.setNew_begin_date(vo.getBegin_date());
        contract_renewal.setNew_end_date(vo.getEnd_date());
        contract_renewal.setRenew_type("1");
        contract_renewal.setArea_manager(vo.getManager());
        contract_renewal.setShop_id(vo.getShop_id());
        contract_renewal.setShop_name(vo.getShop_name());
        contract_renewal.setRenew_date(new Date());
        contract_renewal.setOld_begin_date(vo.getOld_begin_date());
        contract_renewal.setOld_end_date(vo.getOld_end_date());
        contract_renewal.setRenew_date(new Date());
        contract_renewal.setCre_date(new Date());
        contract_renewal.setUpd_user(employee_id);
        contract_renewal.setUpd_date(new Date());
        String owner = vo.getSM();
        if (vo.getROM().equals(employee_id)) {
            owner = vo.getROM();
        } else if (vo.getManager().equals(employee_id)) {
            owner = vo.getManager();
        }
        contract_renewal.setCre_user(owner);
        contract_renewal.setOwner(employee_id);
        contract_renewal.setArea(vo.getShop_area());
        contract_renewal.setOld_work_hour_type(vo.getWork_hour_type());
        contract_renewal.setNew_work_hour_type(vo.getWork_hour_type());

        this.contractDao.insertContract(contract_renewal);
        return contract_renewal;
    }

    public Contract_renewal secondContract(String employee_id) {
        return new Contract_renewal();
    }

    public String getDomain() {
        return this.contractDao.getDomain();
    }

    public Map<String, Object> getSystemParam() {
        return this.contractDao.getSystemParam();
    }

    public int getNoSubmitNum() {
        return this.contractDao.getNoSubmitNum();
    }

    public int updateCreUser() {
        return this.contractDao.updateCreUser();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public boolean sendEmail(List<Contract_renewal> contract_renewal, String getDate) {
        boolean flag = false;
        List<Contract_renewal_email> contract_renewal_email = this.contractDao.sendEmail(contract_renewal);
        Map<String, List<Contract_renewal_email>> contract_renewal_map = new HashMap<String, List<Contract_renewal_email>>();
        for (int i = 0; i < contract_renewal_email.size(); i++) {
            if (contract_renewal_map
                    .get(((Contract_renewal_email) contract_renewal_email.get(i)).getEmaliUser()) == null) {
                List<Contract_renewal_email> contract_renewals = new ArrayList<Contract_renewal_email>();
                contract_renewal_map.put(((Contract_renewal_email) contract_renewal_email.get(i)).getEmaliUser(),
                        contract_renewals);
            }
            ((List) contract_renewal_map.get(((Contract_renewal_email) contract_renewal_email.get(i)).getEmaliUser()))
                    .add((Contract_renewal_email) contract_renewal_email.get(i));
        }

        for (Map.Entry<String, List<Contract_renewal_email>> contract_renewal_entry : contract_renewal_map.entrySet()) {

            List<Contract_renewal_email> emailList = (List) contract_renewal_entry.getValue();
            String email = ((Contract_renewal_email) emailList.get(0)).getEmail();

            EmailTemplate emailTemplate = this.contractDao.getEmailTemplate();
            StringBuilder body = new StringBuilder();
            body.append("\n");
            for (Contract_renewal_email emailAll : emailList) {
                body.append("\n" + emailAll.getName());
            }
            String template = emailTemplate.getTemplate().replace("{StaffList}", body.toString());
            String dateStr = String.valueOf(getDate.split("-")[0]) + "年" + getDate.split("-")[1] + "月";
            String title = emailTemplate.getTitle().replace("{date}", dateStr);

            MailDescribe mail = new MailDescribe(title, template);
            mail.setRecipients(email);
            mail.setCC("");
            this.mailService.send(mail);
        }
        return flag;
    }
}

/*
 * Location: E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend
 * 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\impl\ContractServiceImpl
 * .class Java compiler version: 8 (52.0) JD-Core Version: 1.0.6
 */