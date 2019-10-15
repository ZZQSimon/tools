package cn.com.easyerp.core.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import cn.com.easyerp.core.ApplicationContextProvider;
import cn.com.easyerp.core.api.annotations.ApiMethod;
import cn.com.easyerp.core.api.annotations.ApiParam;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.logger.Loggable;

@Service
public class JApiService {
    @Loggable
    private Logger logger;
    @Autowired
    private ApplicationContextProvider acp;
    @Autowired
    private CacheService cacheService;
    private Map<String, ApiRuntime> apis = null;

    public static final String UUID_KEY = "UUID";

    private void init() {
        this.apis = new HashMap<>();
        Collection<Object> beans = this.acp.getApplicationContext()
                .getBeansWithAnnotation(cn.com.easyerp.core.api.annotations.Api.class).values();
        for (Object obj : beans) {
            Class<?> clazz = obj.getClass();
            if (AopUtils.isAopProxy(obj))
                clazz = AopUtils.getTargetClass(obj);
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method m : declaredMethods) {
                if (m.isAnnotationPresent(ApiMethod.class)) {

                    ApiMethod annotation = (ApiMethod) m.getAnnotation(ApiMethod.class);
                    String name = annotation.value();
                    if (StringUtils.isEmpty(name)) {
                        this.logger.error("japi method(" + m.getName() + ") has no Api annotation name defined");

                    } else if (this.apis.containsKey(name)) {
                        this.logger.error("japi method(" + m.getName() + ") has defined multiple times");
                    } else {

                        String[] params = buildParams(name, m);
                        if (params != null) {
                            this.apis.put(name, new ApiRuntime(obj, m, params));
                        }
                    }
                }
            }
        }
    }

    private String[] buildParams(String name, Method m) {
        final Set<String> params = new LinkedHashSet<String>();
        for (final Annotation[] all : m.getParameterAnnotations()) {
            boolean found = false;
            final Annotation[] array = all;
            final int length2 = array.length;
            int j = 0;
            while (j < length2) {
                final Annotation a = array[j];
                if (!a.annotationType().isAssignableFrom(ApiParam.class)) {
                    ++j;
                } else {
                    final ApiParam param = (ApiParam) a;
                    final String value = param.value();
                    if (params.contains(value)) {
                        this.logger.error("japi(" + name + ") parameter(" + value + ") defined multiple times");
                        return null;
                    }
                    params.add(value);
                    found = true;
                    break;
                }
            }
            if (!found) {
                this.logger.error("japi(" + name + ") has a parameter with no ApiParam annotation");
                return null;
            }
        }
        final TableDescribe table = this.cacheService.getTableDesc(name);
        if (table == null) {
            this.logger.error("japi method(" + name + ") not defined in sys_table");
            return null;
        }
        final List<ColumnDescribe> columns = (List<ColumnDescribe>) table.getColumns();
        if (!params.contains("UUID")) {
            this.logger.error("japi(" + name + ") has no parameter defined as UUID");
            return null;
        }
        if (columns != null && params.size() != columns.size() + 1) {
            this.logger.error("japi(" + name + ") parameters not same with the columns defined in sys_table");
            return null;
        }
        if (columns != null) {
            for (final ColumnDescribe column : columns) {
                if (!params.contains(column.getColumn_name())) {
                    this.logger.error("japi(" + name + ") parameter(" + column.getColumn_name()
                            + ") not defined using ApiParam annotation");
                    return null;
                }
            }
        }
        return params.toArray(new String[params.size()]);
    }

    public ApiResult exec(String uuid, String id, DataMap data) {
        init();
        ApiRuntime runtime = (ApiRuntime) this.apis.get(id);
        if (runtime == null) {
            throw new ApplicationException("no japi found: " + id);
        }
        Object[] params = new Object[runtime.params.length];
        for (int i = 0; i < runtime.params.length; i++) {
            String p = runtime.params[i];
            params[i] = "UUID".equals(p) ? uuid : data.get(p);
        }
        try {
            return (ApiResult) runtime.method.invoke(runtime.instance, params);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new ApplicationException(e);
        }
        /*
         * catch (IllegalAccessException|java.lang.reflect.InvocationTargetException i)
         * { ReflectiveOperationException e; e.printStackTrace(); throw new
         * ApplicationException(e); }
         */
    }

    @SuppressWarnings("rawtypes")
    public ApiResult execBatchApi(List<String> uuid, String id, List<Map<String, Object>> data) {
        init();
        ApiRuntime runtime = (ApiRuntime) this.apis.get(id);
        System.out.println(this.apis.size() + "aaaaaaaaaaaaaaaddddddddddddddddddddddddddd");
        if (runtime == null) {
            throw new ApplicationException("no japi found: " + id);
        }
        Object[] params = new Object[runtime.params.length];
        for (int i = 0; i < runtime.params.length; i++) {
            String p = runtime.params[i];
            if ("UUID".equals(p)) {
                params[i] = uuid;
            } else {
                List<Object> dataParam = new ArrayList<Object>();
                for (int j = 0; j < data.size(); j++) {
                    dataParam.add(((Map) data.get(j)).get(p));
                }
                params[i] = dataParam;
            }
        }
        try {
            return (ApiResult) runtime.method.invoke(runtime.instance, params);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            throw new ApplicationException(e);
        }
        /*
         * catch (IllegalAccessException|java.lang.reflect.InvocationTargetException i)
         * { ReflectiveOperationException e; e.printStackTrace(); throw new
         * ApplicationException(e); }
         */
    }
}
