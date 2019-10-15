package cn.com.easyerp.core.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.batch.BatchInterceptor;
import cn.com.easyerp.core.api.ApiDescribe;

public class BatchDescribe<T> extends Object {
    private String batch_id;
    private String statement;
    private String update_statement;
    private String interceptor_service;
    private String service_param;
    private String memo;
    private String create_trigger;
    private BatchInterceptor interceptor;
    private ApiDescribe api;
    private ApiDescribe update_api;
    private T data;

    public String getBatch_id() {
        return this.batch_id;
    }

    public void setBatch_id(String batch_id) {
        this.batch_id = batch_id;
    }

    public String getStatement() {
        return this.statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getInterceptor_service() {
        return this.interceptor_service;
    }

    public void setInterceptor_service(String interceptor_service) {
        this.interceptor_service = interceptor_service;
    }

    @JsonIgnore
    public String getService_param() {
        return this.service_param;
    }

    public void setService_param(String service_param) {
        this.service_param = service_param;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @JsonIgnore
    public BatchInterceptor getInterceptor() {
        return this.interceptor;
    }

    public void setInterceptor(BatchInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    public ApiDescribe getApi() {
        return this.api;
    }

    public void setApi(ApiDescribe api) {
        this.api = api;
    }

    @JsonIgnore
    public T getData() {
        return (T) this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getUpdate_statement() {
        return this.update_statement;
    }

    public void setUpdate_statement(String update_statement) {
        this.update_statement = update_statement;
    }

    public ApiDescribe getUpdate_api() {
        return this.update_api;
    }

    public void setUpdate_api(ApiDescribe update_api) {
        this.update_api = update_api;
    }

    public String getCreate_trigger() {
        return this.create_trigger;
    }

    public void setCreate_trigger(String create_trigger) {
        this.create_trigger = create_trigger;
    }
}
