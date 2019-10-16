package cn.com.easyerp.weixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.widget.message.MessageTemplateModel;
import cn.com.easyerp.framework.common.Common;

@Service("enterpriseNumber")
public class ServiceNumberImpl implements WeChatService {
    @Autowired
    private AuthDao authDao;
    @Autowired
    private SystemDao systemDao;
    private String APPID = null;
    private String SECRET = null;
    // private String GRANT_TYPE = null;
    private String database = null;
    private String token = null;
    private Date token_modify = null;
    private static final long EXPIRES_IN = 7000000L;
    private SystemParameter sysParam = null;
    private String server_address = null;

    public ServiceNumberImpl() {
        // this.GRANT_TYPE = "authorization_code";

        InputStream inStream = ServiceNumberImpl.class.getClassLoader().getResourceAsStream("param.properties");
        Properties prop = new Properties();
        try {
            prop.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.database = prop.getProperty("database");
        this.server_address = prop.getProperty("server_address");
        this.token_modify = new Date();
    }

    private void initConst() {
        if (this.sysParam == null) {
            this.sysParam = this.systemDao.selectSystemParam();
            this.APPID = this.sysParam.getApp_id();
            this.SECRET = this.sysParam.getSecret();
        }
    }

    public String getToken() {
        initConst();
        if (this.token == null || (new Date()).getTime() - this.token_modify.getTime() > EXPIRES_IN) {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type={TYPE}&appid={APPID}&secret={APPSECRET}";
            url = url.replace("{TYPE}", "client_credential");
            url = url.replace("{APPID}", this.APPID);
            url = url.replace("{APPSECRET}", this.SECRET);
            try {
                JSONObject result = HttpClientUtil.doGetJson(url);
                this.token = result.getString("access_token");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.token;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getOpenid(String code) {
        initConst();
        String openid = "";
        StringBuilder url = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token?appid=");
        url.append(this.APPID);
        url.append("&secret=");
        url.append(this.SECRET);
        url.append("&code=");
        url.append(code);
        url.append("&grant_type=authorization_code");
        try {
            JSONObject jsonObj = HttpClientUtil.doGetJson(url.toString());
            openid = jsonObj.getString("openid");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openid;
    }

    public boolean sendTextMessage(String templateId, String touser, Map<String, Map<String, Object>> datas,
            String urlParam) {
        initConst();
        String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token={ACCESS_TOKEN}";
        url = url.replace("{ACCESS_TOKEN}", getToken());
        touser = this.authDao.selectUserByid(touser).getOpenid();
        MessageTemplateModel template = this.systemDao.getTemplateById(templateId);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("touser", touser);
        param.put("template_id", template.getTemplate_id());
        param.put("data", datas);
        if (urlParam != null && !"".equals(urlParam)) {
            param.put("url", this.server_address + urlParam);
        }
        try {
            String paramJson = Common.toJson(param);
            if (paramJson != null) {
                HttpClientUtil.doPost(url, paramJson);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(List<String> users) {
        return false;
    }

    public String getUser(String userid) {
        return null;
    }

    public String getUserPhone(String userid) {
        return null;
    }
}