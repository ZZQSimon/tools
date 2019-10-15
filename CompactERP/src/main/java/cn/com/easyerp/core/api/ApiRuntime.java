package cn.com.easyerp.core.api;

import java.lang.reflect.Method;

public class ApiRuntime {
    Object instance;
    Method method;
    String[] params;

    public ApiRuntime(Object instance, Method method, String[] params) {
        this.instance = instance;
        this.method = method;
        this.params = params;
    }
}
