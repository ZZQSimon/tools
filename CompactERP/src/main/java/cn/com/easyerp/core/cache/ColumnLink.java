package cn.com.easyerp.core.cache;

import com.fasterxml.jackson.core.type.TypeReference;

public class ColumnLink {
    public static final TypeReference<ColumnLink> jsonRef = new TypeReference<ColumnLink>() {

    };
    private String url;
    private String param;
    private Method method = Method.dialog;
    private String title;
    private String valid;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValid() {
        return this.valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public enum Method {
        tab, dialog, external, func, dx_default;
    }
}