package cn.com.easyerp.core.approve;

import java.util.Date;

import cn.com.easyerp.auth.AuthDetails;

public class ApproveFlowUser {
    public static class State {
        public static String WAIT = "wait";
        public static String AGREE = "agree";
        public static String REJECT = "reject";
        public static String DISAGREE = "disagree";
        public static String HOLD = "hold";
        public static String TERMINATION = "termination";
    }

    private String approve_id;
    private String block_id;
    private int seq;
    private int sequence;
    private int is_default_approve;
    private String module;
    private String userId;
    private String state;
    private String remark;
    private Date creation_time;
    private AuthDetails user;
    private int is_add_approve;
    private float add_approve_seq;
    private boolean takeBack;
    private boolean hasApproveButton;
    private String nextBlockId;
    private String flow_event_id;

    public String getNextBlockId() {
        return this.nextBlockId;
    }

    public void setNextBlockId(String nextBlockId) {
        this.nextBlockId = nextBlockId;
    }

    public boolean isTakeBack() {
        return this.takeBack;
    }

    public void setTakeBack(boolean takeBack) {
        this.takeBack = takeBack;
    }

    public float getAdd_approve_seq() {
        return this.add_approve_seq;
    }

    public void setAdd_approve_seq(float add_approve_seq) {
        this.add_approve_seq = add_approve_seq;
    }

    public int getIs_add_approve() {
        return this.is_add_approve;
    }

    public void setIs_add_approve(int is_add_approve) {
        this.is_add_approve = is_add_approve;
    }

    public String getApprove_id() {
        return this.approve_id;
    }

    public void setApprove_id(String approve_id) {
        this.approve_id = approve_id;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreation_time() {
        return this.creation_time;
    }

    public void setCreation_time(Date creation_time) {
        this.creation_time = creation_time;
    }

    public String getBlock_id() {
        return this.block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public int getIs_default_approve() {
        return this.is_default_approve;
    }

    public void setIs_default_approve(int is_default_approve) {
        this.is_default_approve = is_default_approve;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AuthDetails getUser() {
        return this.user;
    }

    public void setUser(AuthDetails user) {
        this.user = user;
    }

    public boolean isHasApproveButton() {
        return this.hasApproveButton;
    }

    public void setHasApproveButton(boolean hasApproveButton) {
        this.hasApproveButton = hasApproveButton;
    }

    public String getFlow_event_id() {
        return this.flow_event_id;
    }

    public void setFlow_event_id(String flow_event_id) {
        this.flow_event_id = flow_event_id;
    }
}
