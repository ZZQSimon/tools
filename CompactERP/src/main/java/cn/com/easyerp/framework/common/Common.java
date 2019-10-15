package cn.com.easyerp.framework.common;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.easyerp.core.ApplicationContextProvider;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.ExportColumn;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.storage.StorageService;

public class Common {
    public static final ActionResult ActionOk = new ActionResult(true);
    public static final DateFormat defaultDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final DateFormat defaultTimeFormat = new SimpleDateFormat("HH:mm");
    public static final DateFormat defaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final char mapKeySplitter = '|';
    public static final Pattern SQL_VAR_PATTERN = Pattern.compile("#\\{[^}]+}");
    private static final Pattern SQL_BIND_PARAMETER_PATTERN = Pattern.compile("[$#]\\{([^}]+)}");

    private static final Pattern SQL_PARAMETER_PATTERN = Pattern.compile("[\\$#]\\{([^}]+)}");
    private static DataService dataService = null;
    private static StorageService storageService = null;
    private static ObjectMapper objectMapper;
    private static TypeReference<HashMap<String, String>> stringMapJsonRef = new TypeReference<HashMap<String, String>>() {
    };
    public static String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
            "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
            "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    public static boolean isBlank(String val) {
        return StringUtils.isBlank(val);
    }

    public static boolean notBlank(String val) {
        return !isBlank(val);
    }

    public static HttpSession getSession() {
        try {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attr.getRequest().getSession();
        } catch (IllegalStateException e) {
            throw new ApplicationException(e);
        }
    }

    public static Object getSessionObject(String key) {
        return getSession().getAttribute(key);
    }

    public static void putSessionObject(String key, Object obj) {
        getSession().setAttribute(key, obj);
    }

    public static Object removeSessionObject(String key) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        Object ret = attr.getRequest().getSession().getAttribute(key);
        attr.getRequest().getSession().removeAttribute(key);
        return ret;
    }

    public static Object getRequestObject(String key) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        return attr.getRequest().getAttribute(key);
    }

    public static void setRequestObject(String key, Object obj) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        attr.getRequest().setAttribute(key, obj);
    }

    public static DataService getDataService() {
        if (dataService == null) {
            dataService = (DataService) ApplicationContextProvider.getBean("dataService");
        }
        return dataService;
    }

    public static Timestamp now() {
        return new Timestamp((new Date()).getTime());
    }

    public static String leftPad(String str, int length) {
        return leftPad(str, length, ' ');
    }

    public static String rightPad(String str, int length) {
        return rightPad(str, length, ' ');
    }

    public static String leftPad(String str, int length, char ch) {
        return StringUtils.leftPad(str, length, ch);
    }

    public static String rightPad(String str, int length, char ch) {
        return StringUtils.rightPad(str, length, ch);
    }

    public static Timestamp convertToSqlDate(Date date) {
        return new Timestamp(date.getTime());
    }

    public static Timestamp convertToSqlDate(long s) {
        return new Timestamp(s);
    }

    public static Timestamp convertToSqlDate(String date) {
        return new Timestamp(Long.parseLong(date));
    }

    public static Timestamp convertToSqlDatetime(String date) {
        return new Timestamp(Long.parseLong(date));
    }

    public static Timestamp convertToSqlTime(String date) {
        return new Timestamp(Long.parseLong(date));
    }

    public static Date convertToDate(Timestamp ts) {
        return new Date(ts.getTime());
    }

    public static Date convertToDate(long s) {
        return new Date(s);
    }

    public static Timestamp convertTimestamp(long time) {
        return new Timestamp(time);
    }

    public static String convertToDateText(Timestamp ts) {
        return convertToDateText(convertToDate(ts));
    }

    public static String convertToDateText(long s) {
        return convertToDateText(new Date(s));
    }

    public static String convertToDateText(Date date) {
        return defaultDateFormat.format(date);
    }

    public static String convertToDatetimeText(long s) {
        return defaultDateTimeFormat.format(convertToDate(s));
    }

    public static String convertToDatetimeText(Timestamp ts) {
        return defaultDateTimeFormat.format(convertToDate(ts));
    }

    public static String convertToTimeText(Timestamp ts) {
        return defaultTimeFormat.format(convertToDate(ts));
    }

    private static ObjectMapper getObjectMapper() {
        if (objectMapper == null)
            objectMapper = (ObjectMapper) ApplicationContextProvider.getBean("objectMapper");
        return objectMapper;
    }

    public static String toJson(Object value) {
        try {
            return getObjectMapper().writeValueAsString(value);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    @SuppressWarnings("unchecked")
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
                JSONObject jsonObject = JSONObject.parseObject(event_param);
                for (Iterator<?> iter = jsonObject.keySet().iterator(); iter.hasNext();) {
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

    public static void rollback() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
        if (storageService == null)
            storageService = (StorageService) ApplicationContextProvider.getBean("storageService");
        storageService.rollback();
    }

    public static String[] split(String str, String splitter, boolean returnEmptyIfNull) {
        if (str == null && returnEmptyIfNull)
            return new String[0];
        return split(str, splitter);
    }

    public static String[] split(String str, String splitter) {
        return StringUtils.split(str, splitter);
    }

    public static String join(Object[] array) {
        return join(array, ",");
    }

    public static String join(Object[] array, String splitter) {
        return StringUtils.join(array, splitter);
    }

    public static String join(Collection<?> collection) {
        return join(collection, ",");
    }

    public static String join(Collection<?> collection, String splitter) {
        return join(collection, splitter, "'");
    }

    public static String join(Collection<?> collection, String splitter, String quota) {
        if (collection.size() == 0)
            return "";
        return quota + StringUtils.join(collection, quota + splitter + quota) + quota;
    }

    public static String joinValue(List<ExportColumn> collection, String splitter, String quota) {
        if (collection.size() == 0)
            return "";
        StringBuffer sb = new StringBuffer();
        for (ExportColumn column : collection) {
            if (column.getColumn().getData_type() == 4 || column.getColumn().getData_type() == 12) {
                sb.append(column.getValue());
                sb.append(splitter);
                continue;
            }
            sb.append(quota);
            sb.append(column.getValue());
            sb.append(quota);
            sb.append(splitter);
        }
        String lineValue = sb.toString();
        return lineValue.substring(0, lineValue.length() - 1);
    }

    public static boolean isViewDataMap(DataMap map) {
        return cn.com.easyerp.core.data.ViewDataMap.class.isInstance(map);
    }

    public static boolean isNumber(String num) {
        return NumberUtils.isNumber(num);
    }

    public static boolean notNumber(String num) {
        return !isNumber(num);
    }

    public static boolean isDigits(String digits) {
        return NumberUtils.isDigits(digits);
    }

    public static boolean notDigits(String digits) {
        return !isDigits(digits);
    }

    public static String makeMapKey(String key1, String key2, String... keys) {
        String ret = key1 + '|' + key2;
        for (String key : keys)
            ret = ret + '|' + key;
        return ret;
    }

    public static boolean isDate(String val, SimpleDateFormat format) {
        try {
            format.parse(val);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean notDate(String val, SimpleDateFormat format) {
        return !isDate(val, format);
    }

    public static void writeSessionTimeoutResult(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getWriter().print("{\"sessionTimeout\":true}");
    }

    public static void writeInvalidSessionResult(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getWriter().print("{\"invalidSession\":true}");
    }

    public static String getSqlParameterName(String var) {
        Matcher matcher = SQL_PARAMETER_PATTERN.matcher(var);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    public static int sqlBindParameterCount(String sql) {
        Matcher matcher = SQL_BIND_PARAMETER_PATTERN.matcher(sql);
        int ret = 0;
        while (matcher.find())
            ret++;
        return ret;
    }

    public static String replaceVars(String str, Map<String, Object> map, boolean withQuota) {
        Matcher matcher = SQL_VAR_PATTERN.matcher(str);
        // String quota = withQuota ? "'" : "";
        while (matcher.find()) {
            String varName = matcher.group(1);
            Object val = map.get(varName);
            String newValue = (val == null) ? "" : val.toString();
            String token = "\\$\\{" + varName + "}";
            str = str.replaceAll(token, (newValue == null) ? "null" : StringEscapeUtils.escapeSql(newValue));

            String token1 = "#\\{" + varName + "}";
            str = str.replaceAll(token1,
                    (newValue == null) ? "null" : ("'" + StringEscapeUtils.escapeSql(newValue) + "'"));
        }
        return str;
    }

    public static String callStack(String title) {
        StringBuilder sb = new StringBuilder(title);
        StackTraceElement[] elements = (new Throwable()).getStackTrace();
        if (elements.length > 1)
            for (int i = 1; i < elements.length; i++) {
                String stack = elements[i].toString();
                if (stack.startsWith("com.fujitsu.dx"))
                    sb.append("\n\t").append(stack);
            }
        return sb.toString();
    }

    public static String string(Object obj) {
        if (obj == null)
            return "null";
        return obj.toString();
    }

    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 62]);
        }
        return shortBuffer.toString();
    }
}
