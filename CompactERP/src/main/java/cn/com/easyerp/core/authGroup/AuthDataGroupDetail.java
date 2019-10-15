package cn.com.easyerp.core.authGroup;

import cn.com.easyerp.core.cache.I18nDescribe;

public class AuthDataGroupDetail {
    private String table_id;
    private String group_id;
    private String group_detail_id;
    private String column_name;
    private String symbol;
    private String value;
    private String international_id;
    private I18nDescribe i18n;

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }

    public String getGroup_id() {
        return this.group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getGroup_detail_id() {
        return this.group_detail_id;
    }

    public void setGroup_detail_id(String group_detail_id) {
        this.group_detail_id = group_detail_id;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }
}
