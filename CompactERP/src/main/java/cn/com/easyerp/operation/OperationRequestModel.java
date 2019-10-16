package cn.com.easyerp.operation;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.api.ApiRequestParam;
import cn.com.easyerp.core.cache.TriggerDescribe;

public class OperationRequestModel {
    private String table;
    private String column;
    private String event_id;
    private String operationId;
    private ApiRequestParam param;
    private Map<String, String> newState;
    private int isIndexView;
    private Map<String, Object> isIndexViewParam;
    private Map<String, TriggerDescribe> childTriggerRequestParams;
    private List<Map<String, Object>> outInterfaceParam;

    public String getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getOperationId() {
        return this.operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public ApiRequestParam getParam() {
        return this.param;
    }

    public void setParam(ApiRequestParam param) {
        this.param = param;
    }

    public Map<String, String> getNewState() {
        return this.newState;
    }

    public void setNewState(Map<String, String> newState) {
        this.newState = newState;
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

    public List<Map<String, Object>> getOutInterfaceParam() {
        return this.outInterfaceParam;
    }

    public void setOutInterfaceParam(List<Map<String, Object>> outInterfaceParam) {
        this.outInterfaceParam = outInterfaceParam;
    }
}
