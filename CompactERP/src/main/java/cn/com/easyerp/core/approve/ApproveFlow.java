package cn.com.easyerp.core.approve;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApproveFlow {
    public static class ApproveStatus {
        public static String WAIT = "1";
        public static String PROGRESS = "2";
        public static String FINISH = "3";
        public static String REJECT = "4";
        public static String CANCEL = "5";
        public static String HOLD = "6";
        public static String TERMINATION = "7";
        public static String CONDITION_PASS = "8";
    }

    private String approve_id;
    private String table_id;
    private Object data_id;
    private Date creation_time;
    private Date end_time;
    private int state;
    private String module;
    private List<ApproveFlowNode> approveFlowNodes;
    private String approveOver;
    private Map<String, Map<String, ApproveBlockEventExec>> blockEventIsExec;

    public String getApproveOver() {
        return this.approveOver;
    }

    public void setApproveOver(String approveOver) {
        this.approveOver = approveOver;
    }

    public String getApprove_id() {
        return this.approve_id;
    }

    public void setApprove_id(String approve_id) {
        this.approve_id = approve_id;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public Object getData_id() {
        return this.data_id;
    }

    public void setData_id(Object data_id) {
        this.data_id = data_id;
    }

    public Date getCreation_time() {
        return this.creation_time;
    }

    public void setCreation_time(Date creation_time) {
        this.creation_time = creation_time;
    }

    public Date getEnd_time() {
        return this.end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public int getState() {
        return this.state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<ApproveFlowNode> getApproveFlowNodes() {
        return this.approveFlowNodes;
    }

    public void setApproveFlowNodes(List<ApproveFlowNode> approveFlowNodes) {
        this.approveFlowNodes = approveFlowNodes;
    }

    public Map<String, Map<String, ApproveBlockEventExec>> getBlockEventIsExec() {
        return this.blockEventIsExec;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addBlockEventIsExec(ApproveBlockEventExec blockEvent) {
        if (null == this.blockEventIsExec)
            this.blockEventIsExec = new HashMap<>();
        if (null == this.blockEventIsExec.get(blockEvent.getBlock_id()))
            this.blockEventIsExec.put(blockEvent.getBlock_id(), new HashMap<>());
        ((Map) this.blockEventIsExec.get(blockEvent.getBlock_id())).put(blockEvent.getFlow_event_id(), blockEvent);
    }
}
