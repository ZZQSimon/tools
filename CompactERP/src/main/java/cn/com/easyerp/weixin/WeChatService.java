package cn.com.easyerp.weixin;

import java.util.List;
import java.util.Map;

public interface WeChatService {
    String getToken();

    String getDatabase();

    String getOpenid(String paramString);

    boolean sendTextMessage(String paramString1, String paramString2, Map<String, Map<String, Object>> paramMap,
            String paramString3);

    boolean deleteUser(List<String> paramList);

    String getUser(String paramString);

    String getUserPhone(String paramString);
}
