package cn.com.easyerp.core.evaluate;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.framework.exception.ApplicationException;

@Service
public class Formula {
    @Autowired
    private Eval eval;
    @Autowired
    private CacheService cacheService;

    public Object evaluate(String str, Object param) {
        if (str == null || "".equals(str))
            return null;
        String regex = "[\\$#]\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        Object value = null;
        while (matcher.find()) {
            String matcherV = matcher.group();
            String formulaV = matcherV.substring(2, matcherV.length() - 1);
            value = evaluateVar(formulaV, param);
            if (value != null) {
                str = str.replace(matcherV, value.toString());
                continue;
            }
            throw new ApplicationException("variable:" + matcherV + " not exists");
        }

        if (str != null) {
            try {
                String newStr = str.trim();
                if ("'".equals(newStr.substring(0, 1))
                        && "'".equals(newStr.substring(newStr.length() - 1, newStr.length()))) {
                    return newStr.substring(1, newStr.length() - 1).trim();
                }
                return this.eval.calculate(str);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    @SuppressWarnings({ "rawtypes" })
    private Object evaluateVar(String formulaV, Object param) {
        String reg = "(__dx|dx)\\.(user|sys)\\.(.*)";
        if (formulaV.matches(reg)) {
            BeanWrapperImpl beanWrapperImpl2;
            SystemParameter systemParam;
            String columnNameSys;
            BeanWrapperImpl beanWrapperImpl1;
            AuthDetails currentUser;
            String columnNameUser;
            Pattern p = Pattern.compile(reg);
            Matcher m = p.matcher(formulaV);
            m.find();
            switch (m.group(2)) {
            case "user":
                columnNameUser = m.group(3);
                currentUser = AuthService.getCurrentUser();
                if (currentUser == null)
                    return null;
                beanWrapperImpl1 = new BeanWrapperImpl(currentUser);
                if (beanWrapperImpl1.isReadableProperty(columnNameUser)) {
                    return beanWrapperImpl1.getPropertyValue(columnNameUser);
                }
                break;
            case "sys":
                columnNameSys = m.group(3);
                systemParam = this.cacheService.getSystemParam();
                if (systemParam == null)
                    return null;
                beanWrapperImpl2 = new BeanWrapperImpl(systemParam);
                if (beanWrapperImpl2.isReadableProperty(columnNameSys)) {
                    return beanWrapperImpl2.getPropertyValue(columnNameSys);
                }
                break;
            }

        }
        if (param == null)
            return null;
        BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(param);
        String[] name = formulaV.split("\\.");
        Object value = new Object();
        if (name.length == 1) {
            if (param instanceof Map) {
                return ((Map) param).get(name[0]);
            }
            if (beanWrapperImpl.isReadableProperty(name[0])) {
                return beanWrapperImpl.getPropertyValue(name[0]);
            }
        } else if (name.length == 2) {
            if (param instanceof Map) {
                value = ((Map) param).get(name[0]);
            } else {
                Object propertyValue = beanWrapperImpl.getPropertyValue(name[0]);
                if (propertyValue instanceof Map) {
                    return ((Map) propertyValue).get(name[1]);
                }
                BeanWrapperImpl beanWrapperImpl2 = new BeanWrapperImpl(propertyValue);
                if (beanWrapperImpl2.isReadableProperty(name[1])) {
                    return beanWrapperImpl.getPropertyValue(name[1]);
                }
            }

            if (value instanceof Map) {
                return ((Map) value).get(name[1]);
            }
            BeanWrapperImpl beanWrapperImpl1 = new BeanWrapperImpl(value);
            if (beanWrapperImpl1.isReadableProperty(name[1])) {
                return beanWrapperImpl1.getPropertyValue(name[1]);
            }
        }

        return null;
    }
}
