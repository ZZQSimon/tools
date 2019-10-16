package cn.com.easyerp.core.widget.menu;

import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class PageModel extends WidgetModelBase {
    private String table_id;
    private String url;
    private String url_id;
    private String name;
    private String param;
    private I18nDescribe i18n;

    public PageModel() {
        super("");
    }

    public String idPrefix() {
        return "m";
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl_id() {
        return this.url_id;
    }

    public void setUrl_id(String url_id) {
        this.url_id = url_id;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }
}
