package cn.com.easyerp.core.approve;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.cache.TableApproveEventDescribe;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class ApproveRequestModel extends FormRequestModelBase<ApproveModel> {
    private List<ViewDataMap> param;
    private Approve approve;
    private List<AuthGroup> authGroup;
    private String tableId;
    private List<FlowBlock> flowBlock;
    private List<FlowLine> flowLine;
    private List<FlowConditionDetail> flowConditionDetail;
    private FlowEvent flowEvent;
    private Map<String, ApproveBlockEvent> approveBlockEvent;
    private List<TableApproveEventDescribe> approveEvent;
    private Map<String, FlowEvent> batchFlowEvents;
    private Map<String, Map<String, ApproveBlockEvent>> batchBlockEvents;
    private Map<String, List<TableApproveEventDescribe>> batchApproveEvents;
    private String approveId;
    private String blockId;

    public String getFormId() {
        return this.formId;
    }

    private String approveReason;
    private String dataId;
    private String type;
    private List<Object> ids;
    private List<String> recordIds;
    private List<ApproveDescribe> batchApprove;
    private Map<String, Object> userId;
    private String user_Id;
    private float node_seq;
    private String nextBlockId;
    private String domain;
    private String user_id;
    private Map<String, Map<String, FlowEvent>> saveApproveButtonEvent;
    private Map<String, Map<String, ApproveBlockEvent>> saveApproveBlockEvent;
    private String formId;

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public List<ViewDataMap> getParam() {
        return this.param;
    }

    public void setParam(List<ViewDataMap> param) {
        this.param = param;
    }

    public Map<String, Map<String, FlowEvent>> getSaveApproveButtonEvent() {
        return this.saveApproveButtonEvent;
    }

    public void setSaveApproveButtonEvent(Map<String, Map<String, FlowEvent>> saveApproveButtonEvent) {
        this.saveApproveButtonEvent = saveApproveButtonEvent;
    }

    public Map<String, Map<String, ApproveBlockEvent>> getSaveApproveBlockEvent() {
        return this.saveApproveBlockEvent;
    }

    public void setSaveApproveBlockEvent(Map<String, Map<String, ApproveBlockEvent>> saveApproveBlockEvent) {
        this.saveApproveBlockEvent = saveApproveBlockEvent;
    }

    public String getNextBlockId() {
        return this.nextBlockId;
    }

    public void setNextBlockId(String nextBlockId) {
        this.nextBlockId = nextBlockId;
    }

    public String getUser_Id() {
        return this.user_Id;
    }

    public void setUser_Id(String user_Id) {
        this.user_Id = user_Id;
    }

    public Map<String, Object> getUserId() {
        return this.userId;
    }

    public void setUserId(Map<String, Object> userId) {
        this.userId = userId;
    }

    public float getNode_seq() {
        return this.node_seq;
    }

    public void setNode_seq(float node_seq) {
        this.node_seq = node_seq;
    }

    public FlowEvent getFlowEvent() {
        return this.flowEvent;
    }

    public void setFlowEvent(FlowEvent flowEvent) {
        this.flowEvent = flowEvent;
    }

    public List<TableApproveEventDescribe> getApproveEvent() {
        return this.approveEvent;
    }

    public void setApproveEvent(List<TableApproveEventDescribe> approveEvent) {
        this.approveEvent = approveEvent;
    }

    public Map<String, List<TableApproveEventDescribe>> getBatchApproveEvents() {
        return this.batchApproveEvents;
    }

    public void setBatchApproveEvents(Map<String, List<TableApproveEventDescribe>> batchApproveEvents) {
        this.batchApproveEvents = batchApproveEvents;
    }

    public Map<String, ApproveBlockEvent> getApproveBlockEvent() {
        return this.approveBlockEvent;
    }

    public void setApproveBlockEvent(Map<String, ApproveBlockEvent> approveBlockEvent) {
        this.approveBlockEvent = approveBlockEvent;
    }

    public Map<String, Map<String, ApproveBlockEvent>> getBatchBlockEvents() {
        return this.batchBlockEvents;
    }

    public void setBatchBlockEvents(Map<String, Map<String, ApproveBlockEvent>> batchBlockEvents) {
        this.batchBlockEvents = batchBlockEvents;
    }

    public String getDataId() {
        return this.dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public List<FlowBlock> getFlowBlock() {
        return this.flowBlock;
    }

    public void setFlowBlock(List<FlowBlock> flowBlock) {
        this.flowBlock = flowBlock;
    }

    public List<FlowLine> getFlowLine() {
        return this.flowLine;
    }

    public void setFlowLine(List<FlowLine> flowLine) {
        this.flowLine = flowLine;
    }

    public List<FlowConditionDetail> getFlowConditionDetail() {
        return this.flowConditionDetail;
    }

    public void setFlowConditionDetail(List<FlowConditionDetail> flowConditionDetail) {
        this.flowConditionDetail = flowConditionDetail;
    }

    public String getTableId() {
        return this.tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public List<AuthGroup> getAuthGroup() {
        return this.authGroup;
    }

    public void setAuthGroup(List<AuthGroup> authGroup) {
        this.authGroup = authGroup;
    }

    public Approve getApprove() {
        return this.approve;
    }

    public void setApprove(Approve approve) {
        this.approve = approve;
    }

    public String getApproveId() {
        return this.approveId;
    }

    public void setApproveId(String approveId) {
        this.approveId = approveId;
    }

    public String getBlockId() {
        return this.blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getApproveReason() {
        return this.approveReason;
    }

    public void setApproveReason(String approveReason) {
        this.approveReason = approveReason;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getIds() {
        return this.ids;
    }

    public void setIds(List<Object> ids) {
        this.ids = ids;
    }

    public List<String> getRecordIds() {
        return this.recordIds;
    }

    public void setRecordIds(List<String> recordIds) {
        this.recordIds = recordIds;
    }

    public Map<String, FlowEvent> getBatchFlowEvents() {
        return this.batchFlowEvents;
    }

    public void setBatchFlowEvents(Map<String, FlowEvent> batchFlowEvents) {
        this.batchFlowEvents = batchFlowEvents;
    }

    public List<ApproveDescribe> getBatchApprove() {
        return this.batchApprove;
    }

    public void setBatchApprove(List<ApproveDescribe> batchApprove) {
        this.batchApprove = batchApprove;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
