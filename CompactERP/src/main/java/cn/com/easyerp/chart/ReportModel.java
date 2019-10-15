package cn.com.easyerp.chart;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.report.ReportService;

public class ReportModel<T> extends Object {
    private String id;
    private String international_id;
    private String table_id;
    private int report_type;
    private String report_column;
    private String group_column;
    private String date_column;
    private int date_type;
    private String count_column;
    private String condition;
    private String sql;
    private String sql2;
    private String report_column_name;
    private String file_name;
    private int report_file_type;
    private int report_disp_type;
    private String api_json;
    private ApiDescribe api;
    private String pre_api_json;
    private ApiDescribe pre_api;
    private String service_name;
    private String service_param;
    private int hidden;
    private T param;
    public static final int REPORT_TYPE_REPORT = 4;
    private ReportService<T> service;
    private String datas;

    public String getDatas() {
        return this.datas;
    }

    public void setDatas(String datas) {
        this.datas = datas;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public int getReport_type() {
        return this.report_type;
    }

    public void setReport_type(int report_type) {
        this.report_type = report_type;
    }

    public String getReport_column() {
        return this.report_column;
    }

    public void setReport_column(String report_column) {
        this.report_column = report_column;
    }

    public String getGroup_column() {
        return this.group_column;
    }

    public void setGroup_column(String group_column) {
        this.group_column = group_column;
    }

    public String getDate_column() {
        return this.date_column;
    }

    public void setDate_column(String date_column) {
        this.date_column = date_column;
    }

    public int getDate_type() {
        return this.date_type;
    }

    public void setDate_type(int date_type) {
        this.date_type = date_type;
    }

    public String getCount_column() {
        return this.count_column;
    }

    public void setCount_column(String count_column) {
        this.count_column = count_column;
    }

    public String getCondition() {
        return this.condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @JsonIgnore
    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @JsonIgnore
    public String getSql2() {
        return this.sql2;
    }

    public void setSql2(String sql2) {
        this.sql2 = sql2;
    }

    public String getReport_column_name() {
        return this.report_column_name;
    }

    public void setReport_column_name(String report_column_name) {
        this.report_column_name = report_column_name;
    }

    public String getFile_name() {
        return this.file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getReport_file_type() {
        return this.report_file_type;
    }

    public void setReport_file_type(int report_file_type) {
        this.report_file_type = report_file_type;
    }

    public int getReport_disp_type() {
        return this.report_disp_type;
    }

    public void setReport_disp_type(int report_disp_type) {
        this.report_disp_type = report_disp_type;
    }

    public String getApi_json() {
        return this.api_json;
    }

    public void setApi_json(String api_json) {
        this.api_json = api_json;
        if (api_json != null) {
            this.api = (ApiDescribe) ApiDescribe.parseStatement(api_json).get(0);
        }
    }

    public ApiDescribe getApi() {
        return this.api;
    }

    public String getPre_api_json() {
        return this.pre_api_json;
    }

    public void setPre_api_json(String pre_api_json) {
        this.pre_api_json = pre_api_json;
        if (pre_api_json != null) {
            this.pre_api = (ApiDescribe) ApiDescribe.parseStatement(pre_api_json).get(0);
        }
    }

    public ApiDescribe getPre_api() {
        return this.pre_api;
    }

    public String getService_name() {
        return this.service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public ReportService<T> getService() {
        return this.service;
    }

    public void setService(ReportService<T> service) {
        this.service = service;
    }

    public String getService_param() {
        return this.service_param;
    }

    public void setService_param(String service_param) {
        this.service_param = service_param;
    }

    public int getHidden() {
        return this.hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public T getParam() {
        return (T) this.param;
    }

    public void setParam(T param) {
        this.param = param;
    }
}
