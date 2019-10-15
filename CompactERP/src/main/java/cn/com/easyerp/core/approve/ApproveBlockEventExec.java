package cn.com.easyerp.core.approve;

public class ApproveBlockEventExec {
    private String approve_id;
    private String block_id;
    private String flow_event_id;
    private int is_exec;

    public String getApprove_id() {
        return this.approve_id;
    }

    public void setApprove_id(String approve_id) {
        this.approve_id = approve_id;
    }

    public String getBlock_id() {
        return this.block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public String getFlow_event_id() {
        return this.flow_event_id;
    }

    public void setFlow_event_id(String flow_event_id) {
        this.flow_event_id = flow_event_id;
    }

    public int getIs_exec() {
        return this.is_exec;
    }

    public void setIs_exec(int is_exec) {
        this.is_exec = is_exec;
    }
}
