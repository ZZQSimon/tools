package cn.com.easyerp.batch;

import java.util.Map;

import cn.com.easyerp.core.api.ApiRequestParam;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class BatchFormRequestModel extends FormRequestModelBase<BatchFormModel> {
    private String batchId;
    private ApiRequestParam param;
    private String table;
    private int isIndexView;
    private Map<String, Object> isIndexViewParam;
    private Map<String, TriggerDescribe> childTriggerRequestParams;

    public String getBatchId() {
        return this.batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public ApiRequestParam getParam() {
        return this.param;
    }

    public void setParam(ApiRequestParam param) {
        this.param = param;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getIsIndexView() {
        return this.isIndexView;
    }

    public void setIsIndexView(int isIndexView) {
        this.isIndexView = isIndexView;
    }

    public Map<String, Object> getIsIndexViewParam() {
        return this.isIndexViewParam;
    }

    public void setIsIndexViewParam(Map<String, Object> isIndexViewParam) {
        this.isIndexViewParam = isIndexViewParam;
    }

    public Map<String, TriggerDescribe> getChildTriggerRequestParams() {
        return this.childTriggerRequestParams;
    }

    public void setChildTriggerRequestParams(Map<String, TriggerDescribe> childTriggerRequestParams) {
        this.childTriggerRequestParams = childTriggerRequestParams;
    }
}
