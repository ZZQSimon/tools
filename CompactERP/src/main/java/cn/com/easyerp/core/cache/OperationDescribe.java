package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.api.ApiDescribe;

public class OperationDescribe {
    private String id;
    private String table_id;
    private String column_name;
    private String status_id_from;
    private Integer allow_multi;
    private boolean backlog_event;
    private String statement;
    private String condition;
    private String alert_condition;
    private String alert_message;
    private ApiDescribe api;
    private List<OperationRuleModel> rules = null;

    private I18nDescribe i18n;

    private boolean authNeeded = false;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getStatus_id_from() {
        return this.status_id_from;
    }

    public void setStatus_id_from(String status_id_from) {
        this.status_id_from = status_id_from;
    }

    public Integer getAllow_multi() {
        return this.allow_multi;
    }

    public void setAllow_multi(Integer allow_multi) {
        this.allow_multi = allow_multi;
    }

    public boolean isBacklog_event() {
        return this.backlog_event;
    }

    public void setBacklog_event(boolean backlog_event) {
        this.backlog_event = backlog_event;
    }

    public String getStatement() {
        return this.statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getAlert_condition() {
        return this.alert_condition;
    }

    public void setAlert_condition(String alert_condition) {
        this.alert_condition = alert_condition;
    }

    public String getAlert_message() {
        return this.alert_message;
    }

    public void setAlert_message(String alert_message) {
        this.alert_message = alert_message;
    }

    public ApiDescribe getApi() {
        return this.api;
    }

    public void setApi(ApiDescribe api) {
        this.api = api;
    }

    public void addRules(OperationRuleModel rule) {
        if (this.rules == null)
            this.rules = new ArrayList<>();
        this.rules.add(rule);
    }

    public List<OperationRuleModel> getRules() {
        return this.rules;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }

    public boolean isAuthNeeded() {
        return this.authNeeded;
    }

    public void setAuthNeeded(boolean authNeeded) {
        this.authNeeded = authNeeded;
    }
}
