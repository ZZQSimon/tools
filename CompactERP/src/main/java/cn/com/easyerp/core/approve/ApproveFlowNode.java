package cn.com.easyerp.core.approve;

import java.util.List;

public class ApproveFlowNode {
    public static class State {
        public static String WAIT = "wait";
        public static String FINISH = "finish";
        public static String REJECT = "reject";
        public static String HOLD = "hold";
        public static String TERMINATION = "termination";
    }

    private String approve_id;
    private String block_id;
    private int seq;
    private int node_seq;
    private int sequence;
    private String module;
    private String state;
    private List<ApproveFlowUser> approveFlowUsers;
    private int is_addApprove;
    private int addApprove_count;
    private int need_agree_count_people;
    private int hasWaitApprove;
    private List<ApproveFlowUser> addApproveFlowUsers;
    private int is_addApproveNode;
    private int is_alreadyAddApprove;
    private int is_until_block;
    private int is_approve_block;
    private int is_work_block;
    private String belong_block;
    private String data_id;

    public int getIs_until_block() {
        return this.is_until_block;
    }

    public void setIs_until_block(int is_until_block) {
        this.is_until_block = is_until_block;
    }

    public int getIs_approve_block() {
        return this.is_approve_block;
    }

    public void setIs_approve_block(int is_approve_block) {
        this.is_approve_block = is_approve_block;
    }

    public int getIs_addApproveNode() {
        return this.is_addApproveNode;
    }

    public void setIs_addApproveNode(int is_addApproveNode) {
        this.is_addApproveNode = is_addApproveNode;
    }

    public int getIs_alreadyAddApprove() {
        return this.is_alreadyAddApprove;
    }

    public void setIs_alreadyAddApprove(int is_alreadyAddApprove) {
        this.is_alreadyAddApprove = is_alreadyAddApprove;
    }

    public int getAddApprove_count() {
        return this.addApprove_count;
    }

    public void setAddApprove_count(int addApprove_count) {
        this.addApprove_count = addApprove_count;
    }

    public List<ApproveFlowUser> getAddApproveFlowUsers() {
        return this.addApproveFlowUsers;
    }

    public void setAddApproveFlowUsers(List<ApproveFlowUser> addApproveFlowUsers) {
        this.addApproveFlowUsers = addApproveFlowUsers;
    }

    public int getIs_addApprove() {
        return this.is_addApprove;
    }

    public void setIs_addApprove(int is_addApprove) {
        this.is_addApprove = is_addApprove;
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

    public String getBlock_id() {
        return this.block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public int getNode_seq() {
        return this.node_seq;
    }

    public void setNode_seq(int node_seq) {
        this.node_seq = node_seq;
    }

    public int getSequence() {
        return this.sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public List<ApproveFlowUser> getApproveFlowUsers() {
        return this.approveFlowUsers;
    }

    public void setApproveFlowUsers(List<ApproveFlowUser> approveFlowUsers) {
        this.approveFlowUsers = approveFlowUsers;
    }

    public int getNeed_agree_count_people() {
        return this.need_agree_count_people;
    }

    public void setNeed_agree_count_people(int need_agree_count_people) {
        this.need_agree_count_people = need_agree_count_people;
    }

    public int getHasWaitApprove() {
        return this.hasWaitApprove;
    }

    public void setHasWaitApprove(int hasWaitApprove) {
        this.hasWaitApprove = hasWaitApprove;
    }

    public int getIs_work_block() {
        return this.is_work_block;
    }

    public void setIs_work_block(int is_work_block) {
        this.is_work_block = is_work_block;
    }

    public String getBelong_block() {
        return this.belong_block;
    }

    public void setBelong_block(String belong_block) {
        this.belong_block = belong_block;
    }

    public String getData_id() {
        return this.data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }
}
