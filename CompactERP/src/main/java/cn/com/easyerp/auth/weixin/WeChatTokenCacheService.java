package cn.com.easyerp.auth.weixin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class WeChatTokenCacheService {
    private Map<String, Token> tokenCache;

    public String getToken(String corpsecret) {
        if (this.tokenCache == null)
            return null;
        return ((Token) this.tokenCache.get(corpsecret)).getToken();
    }

    public void addToken(String corpsecret, Token token) {
        if (this.tokenCache == null) {
            this.tokenCache = new HashMap<>();
            this.tokenCache.put(corpsecret, token);
        } else {
            this.tokenCache.put(corpsecret, token);
        }
    }
}
