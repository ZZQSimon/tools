package cn.com.easyerp.verification;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CCPRestSmsSDK {
    public static enum BodyType {
        Type_XML, Type_JSON;
    }

    private static final int Request_Get = 0;
    private static final int Request_Post = 1;
    private static final String TemplateSMS = "SMS/TemplateSMS";
    private String SERVER_IP;
    private String SERVER_PORT;
    private String ACCOUNT_SID;
    private String ACCOUNT_TOKEN;
    public String App_ID;
    private BodyType BODY_TYPE;

    public CCPRestSmsSDK() {
        this.BODY_TYPE = BodyType.Type_JSON;
    }

    public void init(String serverIP, String serverPort) {
        if (isEmpty(serverIP) || isEmpty(serverPort)) {
            LoggerUtil.fatal("鍒濆鍖栧紓甯�:serverIP鎴杝erverPort涓虹┖");
            throw new IllegalArgumentException("蹇呴�夊弬鏁�:" + (isEmpty(serverIP) ? " 鏈嶅姟鍣ㄥ湴鍧� " : "")
                    + (isEmpty(serverPort) ? " 鏈嶅姟鍣ㄧ鍙� " : "") + "涓虹┖");
        }
        this.SERVER_IP = serverIP;
        this.SERVER_PORT = serverPort;
    }

    public void setAccount(String accountSid, String accountToken) {
        if (isEmpty(accountSid) || isEmpty(accountToken)) {
            LoggerUtil.fatal("鍒濆鍖栧紓甯�:accountSid鎴朼ccountToken涓虹┖");
            throw new IllegalArgumentException("蹇呴�夊弬鏁�:" + (isEmpty(accountSid) ? " 涓诲笎鍙峰悕绉�" : "")
                    + (isEmpty(accountToken) ? " 涓诲笎鍙蜂护鐗� " : "") + "涓虹┖");
        }
        this.ACCOUNT_SID = accountSid;
        this.ACCOUNT_TOKEN = accountToken;
    }

    public void setAppId(String appId) {
        if (isEmpty(appId)) {
            LoggerUtil.fatal("鍒濆鍖栧紓甯�:appId涓虹┖");
            throw new IllegalArgumentException("蹇呴�夊弬鏁�: 搴旂敤Id 涓虹┖");
        }
        this.App_ID = appId;
    }

    public HashMap<String, Object> sendTemplateSMS(String to, String templateId, String[] datas) {
        HashMap<String, Object> validate = accountValidate();
        if (validate != null)
            return validate;
        if (isEmpty(to) || isEmpty(this.App_ID) || isEmpty(templateId))
            throw new IllegalArgumentException(
                    "蹇呴�夊弬鏁�:" + (isEmpty(to) ? " 鎵嬫満鍙风爜 " : "") + (isEmpty(templateId) ? " 妯℃澘Id " : "") + "涓虹┖");
        CcopHttpClient chc = new CcopHttpClient();
        DefaultHttpClient httpclient = null;
        try {
            httpclient = chc.registerSSL(this.SERVER_IP, "TLS", Integer.parseInt(this.SERVER_PORT), "https");
        } catch (Exception e1) {
            e1.printStackTrace();
            throw new RuntimeException("鍒濆鍖杊ttpclient寮傚父" + e1.getMessage());
        }
        String result = "";
        try {
            HttpPost httppost = (HttpPost) getHttpRequestBase(1, TemplateSMS);
            String requsetbody = "";
            if (this.BODY_TYPE == BodyType.Type_JSON) {
                JsonObject json = new JsonObject();
                json.addProperty("appId", this.App_ID);
                json.addProperty("to", to);
                json.addProperty("templateId", templateId);
                if (datas != null) {
                    StringBuilder sb = new StringBuilder("[");
                    for (String s : datas) {
                        sb.append("\"" + s + "\"" + ",");
                    }
                    sb.replace(sb.length() - 1, sb.length(), "]");
                    JsonParser parser = new JsonParser();
                    JsonArray Jarray = parser.parse(sb.toString()).getAsJsonArray();
                    json.add("datas", Jarray);
                }
                requsetbody = json.toString();
            } else {
                StringBuilder sb = new StringBuilder("<?xml version='1.0' encoding='utf-8'?><TemplateSMS>");
                sb.append("<appId>").append(this.App_ID).append("</appId>").append("<to>").append(to).append("</to>")
                        .append("<templateId>").append(templateId).append("</templateId>");
                if (datas != null) {
                    sb.append("<datas>");
                    for (String s : datas) {
                        sb.append("<data>").append(s).append("</data>");
                    }
                    sb.append("</datas>");
                }
                sb.append("</TemplateSMS>").toString();
                requsetbody = sb.toString();
            }

            LoggerUtil.info("sendTemplateSMS Request body =  " + requsetbody);
            BasicHttpEntity requestBody = new BasicHttpEntity();
            requestBody.setContent(new ByteArrayInputStream(requsetbody.getBytes("UTF-8")));
            requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
            httppost.setEntity(requestBody);
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, "UTF-8");
            }
            EntityUtils.consume(entity);
        } catch (IOException e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("172001", "缃戠粶閿欒");
        } catch (Exception e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
            return getMyError("172002", "鏃犺繑鍥�");
        } finally {
            if (httpclient != null) {
                httpclient.getConnectionManager().shutdown();
            }
        }
        LoggerUtil.info("sendTemplateSMS response body = " + result);

        try {
            if (this.BODY_TYPE == BodyType.Type_JSON) {
                return jsonToMap(result);
            }
            return xmlToMap(result);
        } catch (Exception e) {

            return getMyError("172003", "杩斿洖鍖呬綋閿欒");
        }
    }

    private HashMap<String, Object> jsonToMap(String result) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        JsonParser parser = new JsonParser();
        JsonObject asJsonObject = parser.parse(result).getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entrySet = asJsonObject.entrySet();
        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();

        for (Map.Entry<String, JsonElement> m : entrySet) {
            if ("statusCode".equals(m.getKey()) || "statusMsg".equals(m.getKey())) {
                hashMap.put(m.getKey(), ((JsonElement) m.getValue()).getAsString());
                continue;
            }
            if ("SubAccount".equals(m.getKey()) || "totalCount".equals(m.getKey()) || "token".equals(m.getKey())
                    || "downUrl".equals(m.getKey())) {
                if (!"SubAccount".equals(m.getKey())) {
                    hashMap2.put(m.getKey(), ((JsonElement) m.getValue()).getAsString());
                } else {
                    try {
                        if (((JsonElement) m.getValue()).toString().trim().length() <= 2
                                && !((JsonElement) m.getValue()).toString().contains("[")) {
                            hashMap2.put(m.getKey(), ((JsonElement) m.getValue()).getAsString());
                            hashMap.put("data", hashMap2);
                            break;
                        }
                        if (((JsonElement) m.getValue()).toString().contains("[]")) {
                            hashMap2.put(m.getKey(), new JsonArray());
                            hashMap.put("data", hashMap2);
                            continue;
                        }
                        JsonArray asJsonArray = parser.parse(((JsonElement) m.getValue()).toString()).getAsJsonArray();
                        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
                        for (JsonElement j : asJsonArray) {
                            Set<Map.Entry<String, JsonElement>> entrySet2 = j.getAsJsonObject().entrySet();
                            HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                            for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                                hashMap3.put(m2.getKey(), ((JsonElement) m2.getValue()).getAsString());
                            }
                            arrayList.add(hashMap3);
                        }
                        hashMap2.put("SubAccount", arrayList);
                    } catch (Exception e) {
                        JsonObject asJsonObject2 = parser.parse(((JsonElement) m.getValue()).toString())
                                .getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entrySet2 = asJsonObject2.entrySet();
                        HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                        for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                            hashMap3.put(m2.getKey(), ((JsonElement) m2.getValue()).getAsString());
                        }
                        hashMap2.put(m.getKey(), hashMap3);
                        hashMap.put("data", hashMap2);
                    }
                }

                hashMap.put("data", hashMap2);
                continue;
            }
            JsonObject asJsonObject2 = parser.parse(((JsonElement) m.getValue()).toString()).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entrySet2 = asJsonObject2.entrySet();
            HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
            for (Map.Entry<String, JsonElement> m2 : entrySet2) {
                hashMap3.put(m2.getKey(), ((JsonElement) m2.getValue()).getAsString());
            }
            if (hashMap3.size() != 0) {
                hashMap2.put(m.getKey(), hashMap3);
            } else {
                hashMap2.put(m.getKey(), ((JsonElement) m.getValue()).getAsString());
            }
            hashMap.put("data", hashMap2);
        }
        return hashMap;
    }

    @SuppressWarnings({ "rawtypes" })
    private HashMap<String, Object> xmlToMap(String xml) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        Document doc = null;
        try {
            doc = DocumentHelper.parseText(xml);
            Element rootElt = doc.getRootElement();
            HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
            for (Iterator i = rootElt.elementIterator(); i.hasNext();) {
                Element e = (Element) i.next();
                if ("statusCode".equals(e.getName()) || "statusMsg".equals(e.getName())) {
                    map.put(e.getName(), e.getText());
                    continue;
                }
                if ("SubAccount".equals(e.getName()) || "totalCount".equals(e.getName()) || "token".equals(e.getName())
                        || "downUrl".equals(e.getName())) {
                    if (!"SubAccount".equals(e.getName())) {
                        hashMap2.put(e.getName(), e.getText());
                    } else {
                        ArrayList<HashMap<String, Object>> arrayList = new ArrayList<HashMap<String, Object>>();
                        HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                        for (Iterator i2 = e.elementIterator(); i2.hasNext();) {
                            Element e2 = (Element) i2.next();
                            hashMap3.put(e2.getName(), e2.getText());
                            arrayList.add(hashMap3);
                        }
                        hashMap2.put("SubAccount", arrayList);
                    }
                    map.put("data", hashMap2);
                    continue;
                }
                HashMap<String, Object> hashMap3 = new HashMap<String, Object>();
                for (Iterator i2 = e.elementIterator(); i2.hasNext();) {
                    Element e2 = (Element) i2.next();

                    hashMap3.put(e2.getName(), e2.getText());
                }
                if (hashMap3.size() != 0) {
                    hashMap2.put(e.getName(), hashMap3);
                } else {
                    hashMap2.put(e.getName(), e.getText());
                }
                map.put("data", hashMap2);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
            LoggerUtil.error(e.getMessage());
        } catch (Exception e) {
            LoggerUtil.error(e.getMessage());
            e.printStackTrace();
        }
        return map;
    }

    private HttpRequestBase getHttpRequestBase(int get, String action)
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String timestamp = DateUtil.dateToStr(new Date(), 5);
        EncryptUtil eu = new EncryptUtil();
        String sig = "";
        String acountName = "";
        String acountType = "Accounts";

        acountName = this.ACCOUNT_SID;
        sig = this.ACCOUNT_SID + this.ACCOUNT_TOKEN + timestamp;
        String signature = eu.md5Digest(sig);

        String url = getBaseUrl().append("/" + acountType + "/").append(acountName).append("/" + action + "?sig=")
                .append(signature).toString();
        LoggerUtil.info(getmethodName(action) + " url = " + url);
        HttpPost httpPost = null;
        if (get == Request_Get) {
            // HttpGet httpGet = new HttpGet(url);
        } else if (get == Request_Post) {
            httpPost = new HttpPost(url);
        }
        setHttpHeader(httpPost);
        String src = acountName + ":" + timestamp;
        String auth = eu.base64Encoder(src);
        httpPost.setHeader("Authorization", auth);
        return httpPost;
    }

    private String getmethodName(String action) {
        if (action.equals(TemplateSMS)) {
            return "sendTemplateSMS";
        }
        return "";
    }

    private void setHttpHeader(AbstractHttpMessage httpMessage) {
        if (this.BODY_TYPE == BodyType.Type_JSON) {
            httpMessage.setHeader("Accept", "application/json");
            httpMessage.setHeader("Content-Type", "application/json;charset=utf-8");
        } else {
            httpMessage.setHeader("Accept", "application/xml");
            httpMessage.setHeader("Content-Type", "application/xml;charset=utf-8");
        }
    }

    private StringBuffer getBaseUrl() {
        StringBuffer sb = new StringBuffer("https://");
        sb.append(this.SERVER_IP).append(":").append(this.SERVER_PORT);
        sb.append("/2013-12-26");
        return sb;
    }

    private boolean isEmpty(String str) {
        return ("".equals(str) || str == null);
    }

    private HashMap<String, Object> getMyError(String code, String msg) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("statusCode", code);
        hashMap.put("statusMsg", msg);
        return hashMap;
    }

    private HashMap<String, Object> accountValidate() {
        if (isEmpty(this.SERVER_IP)) {
            return getMyError("172004", "IP涓虹┖");
        }
        if (isEmpty(this.SERVER_PORT)) {
            return getMyError("172005", "绔彛閿欒");
        }
        if (isEmpty(this.ACCOUNT_SID)) {
            return getMyError("172006", "涓诲笎鍙蜂负绌�");
        }
        if (isEmpty(this.ACCOUNT_TOKEN)) {
            return getMyError("172007", "涓诲笎鍙蜂护鐗屼负绌�");
        }
        if (isEmpty(this.App_ID)) {
            return getMyError("172012", "搴旂敤ID涓虹┖");
        }
        return null;
    }
}