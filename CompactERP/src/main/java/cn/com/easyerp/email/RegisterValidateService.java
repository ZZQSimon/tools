package cn.com.easyerp.email;

import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.mail.MailDescribe;
import cn.com.easyerp.core.mail.MailServer;
import cn.com.easyerp.core.mail.MailService;

@Service
public class RegisterValidateService {
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private MailService mailService;

    public boolean processregister(String email, HttpServletRequest request) {
        String subject = "注册激活验证";
        StringBuffer sb = new StringBuffer("点击下面链接激活账号，48小时生效，否则重新注册账号，链接只能使用一次，请尽快激活！\r\n");
        sb.append(request.getRequestURL() + "?action=activate&email=");
        sb.append(email);
        sb.append("&validateCode=");
        sb.append(MD5Util.encode2hex(email));
        sb.append("");

        MailDescribe mail = new MailDescribe(subject, sb.toString());
        mail.setRecipients(email);
        mail.setCC("");

        SystemParameter systemParameter = this.systemDao.selectSystemParam_master();

        MailServer mailServer = new MailServer(systemParameter.getMail_host(), systemParameter.getMail_user(),
                systemParameter.getMail_addr(), systemParameter.getMail_pwd());
        if (this.mailService.send(mail, mailServer)) {
            return true;
        }
        return false;
    }

    public void processActivate(String email, String validateCode) throws ServiceException, ParseException {
        UserModel user = this.userDao.find(email);

        if (user != null) {

            if (user.getStatus() == 0) {

                Date currentTime = new Date();

                currentTime.before(user.getRegisterTime());
                if (currentTime.before(user.getLastActivateTime()))

                {
                    if (validateCode.equals(user.getValidateCode())) {

                        System.out.println("==sq===" + user.getStatus());
                        user.setStatus(1);
                        System.out.println("==sh===" + user.getStatus());
                        this.userDao.update(user);
                    } else {
                        throw new ServiceException("激活码不正确");
                    }
                } else {
                    throw new ServiceException("激活码已过期！");
                }

            } else {
                throw new ServiceException("邮箱已激活，请登录！");
            }
        } else {
            throw new ServiceException("该邮箱未注册（邮箱地址不存在）！");
        }
    }
}
