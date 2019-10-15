package cn.com.easyerp.auth;

import java.util.Date;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.http.MethodType;

import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.mail.MailDescribe;
import cn.com.easyerp.core.mail.MailService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.sms.SmsService;
import cn.com.easyerp.framework.common.Common;

@Service
public class PwdService {
    @Autowired
    private AuthDao authDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    @Qualifier("aliSmsServer")
    private SmsService smsService;
    @Autowired
    private MailService mailService;
    @Autowired
    private DataService dataService;
    @Autowired
    private CacheService cacheService;

    public boolean sendValidCode(String type, AuthDetails user, HttpSession session) {
        String code = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        boolean result = "phone".equals(type) ? sendSmsValidCode(user, code) : sendEmailValidCode(user, code);
        session.setAttribute("login_valid_code", code);
        session.setAttribute("login_valid_user", user);
        return true;
    }

    private boolean sendEmailValidCode(AuthDetails user, String code) {
        String date = Common.defaultDateFormat.format(new Date());
        String title = "瀵嗙爜閲嶇疆楠岃瘉";

        String body = "<p>鎮ㄦ鍦ㄨ繘琛屽瘑鐮侀噸缃搷浣滐紝鍔ㄦ�侀獙璇佺爜涓猴細${code}锛屽闈炴湰浜烘搷浣滐紝璇峰拷鐣ャ��</p><br/><br/><br/><p>${date}</p>";
        body = body.replace("${code}", code);
        body = body.replace("${date}", date);
        MailDescribe mail = new MailDescribe(title, body);
        mail.setRecipients(user.getEmail());
        mail.setCC("");
        return this.mailService.sendHtmlEmail(mail);
    }

    private boolean sendSmsValidCode(AuthDetails user, String code) {
        SendSmsRequest sms = new SendSmsRequest();
        sms.setMethod(MethodType.POST);
        sms.setPhoneNumbers(user.getMobile());
        Properties prop = this.dataService.getSysProper();
        sms.setSignName(prop.getProperty("sms_sign"));
        sms.setTemplateCode(prop.getProperty("sms_vaild_code"));
        sms.setTemplateParam("{\"code\":\"" + code + "\"}");
        return this.smsService.sendSms(sms);
    }

    public boolean updatePassword(HttpSession session, String password) {
        String domain = (String) session.getAttribute("domain");
        // this.dxRoutingDataSource;
        domain = Common.isBlank(domain) ? DxRoutingDataSource.DX_DEFAULT_DATASOURCE : domain;
        this.dxRoutingDataSource.setDomainKey(domain);
        if (Common.isBlank((String) session.getAttribute("domain"))) {
            return (this.authDao.updateSysPassword((AuthDetails) session.getAttribute("login_valid_user"), password,
                    this.cacheService.getDecryptKey()) != 1);
        }
        return (this.authDao.updatePassword((AuthDetails) session.getAttribute("login_valid_user"), password,
                this.cacheService.getDecryptKey()) != 1);
    }
}
