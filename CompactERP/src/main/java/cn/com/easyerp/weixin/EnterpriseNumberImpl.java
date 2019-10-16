package cn.com.easyerp.weixin;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

@Service("serviceNumber")
public class EnterpriseNumberImpl implements WeChatService {
    @Autowired
    private AuthDao authDao;
    @Autowired
    private SystemDao systemDao;
    private String CORPID = null;
    private String CORPSERCRET = null;
    private String AGENTID = null;
    private String database = null;
    private String token = null;
    private Date token_modify = null;
    private static final long EXPIRES_IN = 7000000L;
    private static final String UTF8 = "utf-8";
    private SystemParameter sysParam = null;
    private String server_address = null;
    private String address_book = null;

    public EnterpriseNumberImpl() {
        InputStream inStream = cn.com.easyerp.weixin.ServiceNumberImpl.class.getClassLoader()
                .getResourceAsStream("param.properties");
        Properties prop = new Properties();
        try {
            prop.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.database = prop.getProperty("database");
        this.server_address = prop.getProperty("server_address");
        this.address_book = prop.getProperty("address_book");
        this.token_modify = new Date();
    }

    private void initConst() {
        if (this.sysParam == null) {
            this.sysParam = this.systemDao.selectSystemParam();
            this.CORPID = this.sysParam.getApp_id();
            this.CORPSERCRET = this.sysParam.getSecret();
            this.AGENTID = this.sysParam.getAgent_id();
        }
    }

    public String getToken() {
        initConst();
        if (this.token == null || (new Date()).getTime() - this.token_modify.getTime() > EXPIRES_IN) {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=";
            try {
                url = url + URLEncoder.encode(this.CORPID, UTF8) + "&corpsecret="
                        + URLEncoder.encode(this.CORPSERCRET, UTF8);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            try {
                JSONObject result = HttpClientUtil.doGetJson(url);
                this.token = result.getString("access_token");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.token;
    }

    public String getAddressBookToken() {
        String token = "";
        String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid={CORPID}&corpsecret={CORPSERCRET}";
        try {
            url = url.replace("{CORPID}", URLEncoder.encode(this.CORPID, UTF8));
            url = url.replace("{CORPSERCRET}", URLEncoder.encode(this.address_book, UTF8));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try {
            JSONObject result = HttpClientUtil.doGetJson(url);
            token = result.getString("access_token");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getOpenid(String code) {
        String openid = "";
        StringBuilder url = new StringBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=");
        url.append(getToken());
        url.append("&code=");
        url.append(code);

        try {
            JSONObject jsonObj = HttpClientUtil.doGetJson(url.toString());
            openid = jsonObj.getString("UserId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return openid;
    }

    public boolean sendTextMessage(String templateId, String touser, Map<String, Map<String, Object>> datas,
            String urlParam) {
        initConst();
        String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token={ACCESS_TOKEN}";

        url = url.replace("{ACCESS_TOKEN}", getToken());

        touser = this.authDao.selectUserByid(touser).getOpenid();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("touser", touser);
        param.put("msgtype", "textcard");
        param.put("agentid", this.AGENTID);
        param.put("textcard", buildMessage(templateId, datas, urlParam));

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
        initConst();
        // String openid = "";
        StringBuilder url = new StringBuilder("https://qyapi.weixin.qq.com/cgi-bin/user/batchdelete?access_token=");
        url.append(getAddressBookToken());

        Map<String, List<String>> list = new HashMap<String, List<String>>();
        list.put("useridlist", this.authDao.getOpenids(users));
        try {
            String useridlist = Common.toJson(list);
            if (useridlist != null) {
                HttpClientUtil.doPost(url.toString(), useridlist);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private Map<String, String> buildMessage(String templateId, Map<String, Map<String, Object>> datas,
            String urlParam) {
        initConst();
        Map<String, String> textcard = new HashMap<String, String>();
        MessageTemplateModel template = this.systemDao.getTemplateById(templateId);
        String body = template.getTemplate();

        body = body.replace("{now}", Common.defaultDateFormat.format(new Date()));
        for (Map.Entry<String, Map<String, Object>> data : datas.entrySet()) {
            String key = (String) data.getKey();
            String value = String.valueOf(((Map) data.getValue()).get("value"));
            body = body.replace("{" + key + "}", value);
        }
        textcard.put("title", template.getTitle());
        textcard.put("description", body);
        if (!"".equals(urlParam) && urlParam != null) {
            textcard.put("url", this.server_address + urlParam);
        } else {
            textcard.put("url", "#");
        }
        textcard.put("btntxt", template.getBtn_text());
        return textcard;
    }

    public String getUser(String userid) {
        String photo = "";
        if (Common.isBlank(userid))
            return photo;
        Map<String, Object> user = getUserParam(userid);
        if (user != null && user.get("avatar") != null)
            photo = user.get("avatar").toString();
        return photo;
    }

    private Map<String, Object> getUserParam(String userid) {
        if (Common.isBlank(userid))
            return null;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("access_token", getToken());
        params.put("userid", userid);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/get";
        String result = HttpClientUtil.doGet(url, params);
        return Common.paseEventParam(result);
    }

    public String getUserPhone(String userid) {
        String phone = "";
        if (Common.isBlank(userid))
            return phone;
        Map<String, Object> user = getUserParam(userid);
        if (user != null && user.get("mobile") != null)
            phone = user.get("mobile").toString();
        return phone;
    }
}
