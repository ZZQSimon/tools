package cn.com.easyerp.core.approve;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.authGroup.AuthGroup;

public class Approve {
    private List<FlowBlock> flowBlock;
    private List<FlowLine> flowLine;
    private List<List<AuthGroup>> authGroup;
    private List<FlowConditionDetail> flowConditionDetail;

    public Map<String, Map<String, FlowEvent>> getInitApproveButtonEvent() {
        return this.initApproveButtonEvent;
    }

    private List<FlowEvent> flowEvent;
    private Map<String, Map<String, FlowEvent>> approveButtonEvent;
    private Map<String, Map<String, ApproveBlockEvent>> approveBlockEvent;
    private Map<String, Map<String, FlowEvent>> initApproveButtonEvent;
    private Map<String, Map<String, ApproveBlockEvent>> initApproveBlockEvent;

    public void setInitApproveButtonEvent(Map<String, Map<String, FlowEvent>> initApproveButtonEvent) {
        this.initApproveButtonEvent = initApproveButtonEvent;
    }

    public Map<String, Map<String, ApproveBlockEvent>> getInitApproveBlockEvent() {
        return this.initApproveBlockEvent;
    }

    public void setInitApproveBlockEvent(Map<String, Map<String, ApproveBlockEvent>> initApproveBlockEvent) {
        this.initApproveBlockEvent = initApproveBlockEvent;
    }

    public Map<String, Map<String, FlowEvent>> getApproveButtonEvent() {
        return this.approveButtonEvent;
    }

    public void setApproveButtonEvent(Map<String, Map<String, FlowEvent>> approveButtonEvent) {
        this.approveButtonEvent = approveButtonEvent;
    }

    public Map<String, Map<String, ApproveBlockEvent>> getApproveBlockEvent() {
        return this.approveBlockEvent;
    }

    public void setApproveBlockEvent(Map<String, Map<String, ApproveBlockEvent>> approveBlockEvent) {
        this.approveBlockEvent = approveBlockEvent;
    }

    public List<FlowEvent> getFlowEvent() {
        return this.flowEvent;
    }

    public void setFlowEvent(List<FlowEvent> flowEvent) {
        this.flowEvent = flowEvent;
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

    public List<List<AuthGroup>> getAuthGroup() {
        return this.authGroup;
    }

    public void setAuthGroup(List<List<AuthGroup>> authGroup) {
        this.authGroup = authGroup;
    }

    public List<FlowConditionDetail> getFlowConditionDetail() {
        return this.flowConditionDetail;
    }

    public void setFlowConditionDetail(List<FlowConditionDetail> flowConditionDetail) {
        this.flowConditionDetail = flowConditionDetail;
    }
}
