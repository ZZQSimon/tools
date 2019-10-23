package com.g1extend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.g1extend.expection.ApplicationException;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class common {
    private static ObjectMapper objectMapper;
    private static TypeReference<HashMap<String, String>> stringMapJsonRef = new TypeReference<HashMap<String, String>>() {
    };
    private static EncrypDES des;

    private static EncrypDES getEncry() {
        try {
            if (des == null)
                return new EncrypDES("G1");
        } catch (Exception e) {
            throw new ApplicationException("EncrypDES false");
        }
        return des;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null)
            objectMapper = new ObjectMapper();
        return objectMapper;
    }

    public static boolean isBlank(String val) {
        return StringUtils.isBlank(val);
    }

    public static String toJson(Object value) {
        try {
            return getObjectMapper().writeValueAsString(value);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        if (isBlank(json))
            return null;
        try {
            return (T) getObjectMapper().readValue(json, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApplicationException(e);
        }
    }

    public static Map<String, String> stringMapJson(String json) {
        try {
            return (Map) getObjectMapper().readValue(json, stringMapJsonRef);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public static Map<String, Object> paseEventParam(String event_param) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (event_param != null && !"".equals(event_param)) {
            try {
                JSONObject jsonObject = JSONObject.fromObject(event_param);
                for (Iterator<?> iter = jsonObject.keys(); iter.hasNext();) {
                    String key = (String) iter.next();
                    String value = jsonObject.get(key).toString();
                    try {
                        Map<String, Object> resultMap = paseEventParam(value);
                        map.put(key, resultMap);
                    } catch (Exception e) {
                        map.put(key, value);
                    }
                }
            } catch (Exception e) {
                throw new ApplicationException("param \"" + event_param + "\" not json string");
            }
        }
        return map;
    }

    public static String getEncryption(String encryptionStr) {
        String strDES = "";
        try {
            strDES = (new EncrypDES("G1")).encrypt(encryptionStr);
        } catch (Exception e) {
            return "encrypt lose";
        }
        return strDES;
    }

    public static String getDecode(String decodeStr) {
        String decryptStr = "";
        try {
            decryptStr = (new EncrypDES("G1")).decrypt(decodeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptStr;
    }

    public static Properties getSystemParam() {
        InputStream inStream = common.class.getClassLoader().getResourceAsStream("param.properties");
        Properties prop = new Properties();
        try {
            prop.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    public static String getImgStr(BufferedImage imgBar) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        byte[] data = null;

        try {
            ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
            ImageIO.write(imgBar, "png", imOut);
            InputStream in = new ByteArrayInputStream(bs.toByteArray());
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        return new String(Base64.encodeBase64(data));
    }

    public static void saveFile(String path, String content) throws IOException {
        File file = new File(path);
        FileWriter fw = null;
        BufferedWriter bw = null;
        if (!file.exists()) {
            file.createNewFile();
        }
        fw = new FileWriter(file.getAbsoluteFile());
        bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
    }
}
