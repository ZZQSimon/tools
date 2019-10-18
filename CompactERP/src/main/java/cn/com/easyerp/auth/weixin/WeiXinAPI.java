package cn.com.easyerp.auth.weixin;

import java.io.IOException;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.easyerp.framework.httpclient.HttpClientUtil;

@Repository
public class WeiXinAPI {
    public String createWeiXinUser(CompanyWXData companyWXData, String mobile, String userName, String sex,
            String telePhone, String email) {
        String corpId = "wwc850d1173477c1f0";
        String secret = "btFuciRAVVnuRsIymPiH0J3vXi7bXnSSynchKzj9ndg";
        String token_url = getAccessToken(corpId, secret);

        String str = "{\"userid\": \"" + mobile + "\"," + "\"name\": \"" + userName + "\"," + "\"mobile\": \"" + mobile
                + "\"," + "\"department\": [1]," + "\"gender\": \"" + sex + "\"," + "\"email\": \"" + email + "\","
                + "\"isleader\": 1," + "\"enable\":1," + "\"telephone\": \"" + telePhone + "\"," + "}";

        String result = HttpClientUtil
                .doPost("https://qyapi.weixin.qq.com/cgi-bin/user/create?access_token=" + token_url, str);
        System.out.println("创建成员：-----------" + result);
        return "";
    }

    @Autowired
    private WeChatTokenCacheService weChatTokenCacheService;

    public String getAccessToken(String corpId, String corpsecret) {
        String token = this.weChatTokenCacheService.getToken(corpsecret);
        if (token == null || "".equals(token)) {
            String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpId=";
            url = url + corpId + "&corpsecret=";
            url = url + corpsecret;
            WeChatAccessToken weixin = null;
            String s = HttpClientUtil.doGet(url);
            ObjectMapper mapper = new ObjectMapper();
            try {
                weixin = (WeChatAccessToken) mapper.readValue(s, WeChatAccessToken.class);
            } catch (IOException e) {
            }

            if (weixin == null || weixin.getAccess_token() == null || "".equals(weixin.getAccess_token()))
                return null;
            this.weChatTokenCacheService.addToken(corpsecret, new Token(weixin.getAccess_token(), new Date()));
            return weixin.getAccess_token();
        }
        return token;
    }

    public String sendMessages(String userId, String content, String corpId, String secret, String agentId) {
        String url = "http://192.168.2.110:8080/gc_mobile/webservice/rs/webService/sendMessage_text";
        String postjson = "{\"userId\":[\"" + userId + "\"]," + "\"content\":\"" + content + "\"," + "\"corpId\":\""
                + corpId + "\"," + "\"secret\":\"" + secret + "\"," + "\"agentId\":\"" + agentId + "\"," + "}";

        String result = HttpClientUtil.doPost(url, postjson);
        System.out.println("发消息：：：：：：：" + result);
        return "";
    }
}
