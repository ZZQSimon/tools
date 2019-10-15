package cn.com.easyerp.auth.weixin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ToJson {
    public static String toJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String products = null;
        try {
            products = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
        return products;
    }
}
