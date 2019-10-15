package cn.com.easyerp.core.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.api.ApiDescribe;

public class TriggerDescribe {
    private String table_id;
    private String system_type;
    private int seq;
    private String action_id;
    private String action_name_international;
    private I18nDescribe action_name_I18N;
    private String memo;
    private int is_using;
    private String url;
    private int is_one_data;
    private String report_id;
    private List<ActionPrerequistieDescribe> condition;
    private List<ApiDescribe> api;
    private Map<String, ApiDescribe> apiMap;
    private List<ApiDescribe> before_api;
    private List<ApiDescribe> after_api;

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setIs_using(int is_using) {
        this.is_using = is_using;
    }

    public int getIs_using() {
        return this.is_using;
    }

    public void setIs_one_data(int is_one_data) {
        this.is_one_data = is_one_data;
    }

    public int getIs_one_data() {
        return this.is_one_data;
    }

    public Map<String, ApiDescribe> getApiMap() {
        return this.apiMap;
    }

    public List<ActionPrerequistieDescribe> getCondition() {
        return this.condition;
    }

    public void setCondition(List<ActionPrerequistieDescribe> condition) {
        this.condition = condition;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getSystem_type() {
        return this.system_type;
    }

    public void setSystem_type(String system_type) {
        this.system_type = system_type;
    }

    public String getAction_id() {
        return this.action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getAction_name_international() {
        return this.action_name_international;
    }

    public void setAction_name_international(String action_name_international) {
        this.action_name_international = action_name_international;
    }

    public I18nDescribe getAction_name_I18N() {
        return this.action_name_I18N;
    }

    public void setAction_name_I18N(I18nDescribe action_name_I18N) {
        this.action_name_I18N = action_name_I18N;
    }

    public String getReport_id() {
        return this.report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public List<ApiDescribe> getApi() {
        return this.api;
    }

    public void setApi(List<ApiDescribe> api) {
        this.api = api;
        this.before_api = api;
        this.after_api = api;
        if (api != null) {
            for (ApiDescribe apiDescribe : api) {
                if (this.apiMap == null)
                    this.apiMap = new HashMap<>();
                this.apiMap.put(apiDescribe.getEvent_id(), apiDescribe);
            }
        }
    }

    public List<ApiDescribe> getBefore_api() {
        return this.before_api;
    }

    public void setBefore_api(List<ApiDescribe> before_api) {
        this.before_api = before_api;
    }

    public List<ApiDescribe> getAfter_api() {
        return this.after_api;
    }

    public void setAfter_api(List<ApiDescribe> after_api) {
        this.after_api = after_api;
    }

    public enum TriggerType {
        delete, edit, add, view, operation;
    }

    public static class IsOneDayType {
        public static final int NO_CHOOSE = 0;
        public static final int ONE_OR_MORE = 1;
        public static final int ONLY_ONE = 2;
    }
}
