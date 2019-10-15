package cn.com.easyerp.core.authGroup;

import java.util.ArrayList;
import java.util.List;

import cn.com.easyerp.core.cache.I18nDescribe;

public class AuthDataGroup {
    private String group_id;
    private String filter;
    private String international_id;
    private I18nDescribe i18n;
    private List<AuthDataGroupDetail> details;

    public String getGroup_id() {
        return this.group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getFilter() {
        return this.filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }

    public List<AuthDataGroupDetail> getDetails() {
        return this.details;
    }

    public void addDetail(AuthDataGroupDetail detail) {
        if (this.details == null) {
            this.details = new ArrayList<>();
        }
        this.details.add(detail);
    }
}
