package cn.com.easyerp.framework.i18n;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.com.easyerp.framework.enums.Language;
import cn.com.easyerp.framework.util.FileUtils;
import cn.com.easyerp.framework.util.LogUtil;

/**
 * @ClassName: I18NBundle <br>
 * @Description: [] <br>
 * @date 2018.11.26 <br>
 * 
 * @author Simon Zhang
 */
public class I18NBundle {
    public static final String INTL_JSON_FILE_PATH = "i18n/messages_%s.json";

    private static I18NBundle instance = new I18NBundle();

    private Map<String, Map<String, String>> i18nBundles;

    public static I18NBundle getInstance() {
        if (null == instance) {
            instance = new I18NBundle();
        }
        return instance;
    }

    public static String getString(String resourceId, Language locale) {
        return getInstance().getIntlString(resourceId, locale);
    }

    public static String getString(String resourceId, Language locale, Object... objects) {
        return MessageFormat.format(getString(resourceId, locale), objects);
    }

    private I18NBundle() {
        i18nBundles = new HashMap<>();
    }

    private String getIntlString(String resourceId, Language locale) {
        if (locale == null) {
            locale = Language.Chinese;
        }
        if (i18nBundles.containsKey(locale.getId())) {
            Map<String, String> i18nBundle = i18nBundles.get(locale.getId());
            if (i18nBundle.containsKey(resourceId)) {
                return i18nBundle.get(resourceId);
            }
        } else {
            loadBundle(locale);
            return this.getIntlString(resourceId, locale);
        }
        return resourceId;
    }

    private void loadBundle(Language locale) {
        String fileName = String.format(INTL_JSON_FILE_PATH, locale.getId());
        // String fileName = I18NBundle.class.getResource(jsonFile).getPath();
        try {
            String str = FileUtils.getFileContentInJar(I18NBundle.class, fileName, "UTF-8");
            @SuppressWarnings("unchecked")
            Map<String, String> i18nBundle = JSONObject.parseObject(str, Map.class);
            i18nBundles.put(locale.getId(), i18nBundle);
            LogUtil.info(String.format("Load resouce json file for language: %s successfully", locale.getId()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
