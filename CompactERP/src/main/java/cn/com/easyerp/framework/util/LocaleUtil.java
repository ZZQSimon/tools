package cn.com.easyerp.framework.util;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import cn.com.easyerp.framework.enums.Language;

/**
 * 注入形式取得国际化配置类
 * 
 * @author Simon Zhang
 */
@Component
public class LocaleUtil {
    /** 配置系统语言 */
    @Value("${system.locale}")
    public String localeString;

    /** 静态系统语言 */
    private static String localeTemp;

    /** 注入国际化资源 */
    @Autowired
    private MessageSource messageSource;

    /** 静态国际化资源 */
    private static MessageSource messageSourceTemp;

    /** 系统语言，通过配置文件确定，默认简体中文 */
    public static Language locale = Language.Chinese;

    /**
     * 使用注解方式初始化静态资源
     */
    @PostConstruct
    public void init() {
        messageSourceTemp = messageSource;
        localeTemp = localeString;
        locale = getSystemLocale();
    }

    /**
     * 国际化取值
     * 
     * @param key
     * @return
     */
    public static String get(String key) {
        switch (locale) {
        case Chinese: {
            return messageSourceTemp.getMessage(key, null, Locale.SIMPLIFIED_CHINESE);
        }
        case English: {
            return messageSourceTemp.getMessage(key, null, Locale.US);
        }
        case Japanese: {
            return messageSourceTemp.getMessage(key, null, Locale.JAPAN);
        }
        default: {
            return messageSourceTemp.getMessage(key, null, Locale.SIMPLIFIED_CHINESE);
        }
        }
    }

    /**
     * 确定国际化语言
     * 
     * @return
     */
    private static Language getSystemLocale() {
        if ("English".equals(localeTemp)) {
            return Language.English;
        } else if ("Chinese".equals(localeTemp)) {
            return Language.Chinese;
        } else if ("Japanese".equals(localeTemp)) {
            return Language.Japanese;
        } else {
            return Language.Chinese;
        }
    }

}
