package cn.com.easyerp.framework.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * 对象判空工具类
 * 
 * @author Simon Zhang
 */
public class EmptyUtil {
    /**
     * 判断对象是否为Null
     * 
     * @param o
     * @return Null为TRUE，非Null为FALSE
     * @throws IllegalArgumentException
     */
    public static boolean isNull(Object o) throws IllegalArgumentException {
        if (null == o) {
            return true;
        }
        return false;
    }

    /**
     * 判断对象是否为空
     * 
     * @param o
     * @return 空为TRUE，非空为FALSE
     * @throws IllegalArgumentException
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object o) throws IllegalArgumentException {
        if (isNull(o)) {
            return true;
        }
        if ((o instanceof String)) {
            if (((String) o).length() == 0) {
                return true;
            }
        } else if ((o instanceof Collection)) {
            if (((Collection) o).isEmpty()) {
                return true;
            }
        } else if (o.getClass().isArray()) {
            if (Array.getLength(o) == 0) {
                return true;
            }
        } else if ((o instanceof Map)) {
            if (((Map) o).isEmpty()) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * 判断对象是否为非空
     * 
     * @param o
     * @return 空为FALSE，非空为TRUE
     */
    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    /**
     * String类型非空判断
     * 
     * @param str
     * @return 非空为TRUE，空或0长度为FALSE
     */
    public static boolean isNotBlank(String str) {
        return !isStrBlank(str);
    }

    /**
     * String类型为空判断
     * 
     * @param str
     * @return 非空为FALSE，空或0长度为TRUE
     */
    public static boolean isStrBlank(String str) {
        if ((str == null) || (str.length() == 0)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
