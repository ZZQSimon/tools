package cn.com.easyerp.core.cache;

public class TableShortcutDescribe {
    private String id;
    private String table_id;
    private String url;
    private String param;
    private I18nDescribe i18n;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
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
