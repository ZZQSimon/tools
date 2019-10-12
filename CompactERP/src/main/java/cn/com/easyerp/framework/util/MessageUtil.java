package cn.com.easyerp.framework.util;

import java.text.MessageFormat;

/**
 * 格式化信息工具类
 * 
 * @author Simon Zhang
 */
public class MessageUtil {
    /**
     * format string with parameters
     * 
     * @param pattern
     *            String
     * @param args
     *            ...
     * @return String
     */
    public static String formmatString(final String pattern, final Object... args) {
        if (EmptyUtil.isEmpty(pattern)) {
            throw new java.lang.IllegalArgumentException();
        }
        return MessageFormat.format(pattern, args);
    }
}
