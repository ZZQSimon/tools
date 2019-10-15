package cn.com.easyerp.core.approve;

public class ApproveNodeToDb {
    private String approve_id;
    private String block_id;
    private int num;
    private int maxSequence;
    private int is_until_block;
    private int is_approve_block;
    private String belong_block;

    public String getBelong_block() {
        return this.belong_block;
    }

    public void setBelong_block(String belong_block) {
        this.belong_block = belong_block;
    }

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

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getMaxSequence() {
        return this.maxSequence;
    }

    public void setMaxSequence(int maxSequence) {
        this.maxSequence = maxSequence;
    }

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
}
