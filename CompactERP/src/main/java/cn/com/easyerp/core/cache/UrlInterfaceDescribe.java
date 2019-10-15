package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.easyerp.DeployTool.service.ColumnDeploy;

public class UrlInterfaceDescribe {
    private int updateType;
    private int urlIsCheck;
    private String id;
    private String name;
    private String url;
    private String param;
    private String memo;
    private String router_url;
    private int type;
    private String module;
    private String summary;
    private String prodSql;
    private int sqlChange;
    private I18nDescribe i18n;
    private Map<String, ColumnDeploy> cRUDColumns;
    private List<ColumnDeploy> deleteColumns;
    private Map<String, ColumnDescribe> urlParam;
    private List<TableCheckRuleDescribe> urlCheck;

    public int getUpdateType() {
        return this.updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public int getUrlIsCheck() {
        return this.urlIsCheck;
    }

    public void setUrlIsCheck(int urlIsCheck) {
        this.urlIsCheck = urlIsCheck;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRouter_url() {
        return this.router_url;
    }

    public void setRouter_url(String router_url) {
        this.router_url = router_url;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }

    public Map<String, ColumnDescribe> getUrlParam() {
        return this.urlParam;
    }

    public void setUrlParam(Map<String, ColumnDescribe> urlParam) {
        this.urlParam = urlParam;
    }

    public List<TableCheckRuleDescribe> getUrlCheck() {
        return this.urlCheck;
    }

    public void setUrlCheck(List<TableCheckRuleDescribe> urlCheck) {
        this.urlCheck = urlCheck;
    }

    public void addUrlParam(ColumnDescribe param) {
        if (this.urlParam == null) {
            this.urlParam = new HashMap<>();
        }
        if (param.getColumn_name() == null || "".equals(param.getColumn_name())) {
            return;
        }
        this.urlParam.put(param.getColumn_name(), param);
    }

    public void addUrlCheck(TableCheckRuleDescribe param) {
        if (this.urlCheck == null) {
            this.urlCheck = new ArrayList<>();
        }
        this.urlCheck.add(param);
    }

    public Map<String, ColumnDeploy> getcRUDColumns() {
        return this.cRUDColumns;
    }

    public void setcRUDColumns(Map<String, ColumnDeploy> cRUDColumns) {
        this.cRUDColumns = cRUDColumns;
    }

    public List<ColumnDeploy> getDeleteColumns() {
        return this.deleteColumns;
    }

    public void setDeleteColumns(List<ColumnDeploy> deleteColumns) {
        this.deleteColumns = deleteColumns;
    }

    public String getProdSql() {
        return this.prodSql;
    }

    public void setProdSql(String prodSql) {
        this.prodSql = prodSql;
    }

    public int getSqlChange() {
        return this.sqlChange;
    }

    public void setSqlChange(int sqlChange) {
        this.sqlChange = sqlChange;
    }

    public static class UrlType {
        public static final int URL = 1;
        public static final int JAVA = 2;
        public static final int PROC = 3;
        public static final int OUT_INTERFACE = 4;
        public static final int OUT_PAGE = 5;
    }
}
