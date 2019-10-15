package cn.com.easyerp.core.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OperationRuleModel {
    private String id;
    private String action_id;
    private String cond;
    private String status_id_to;
    private int seq;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getAction_id() {
        return this.action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getCond() {
        return this.cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getStatus_id_to() {
        return this.status_id_to;
    }

    public void setStatus_id_to(String status_id_to) {
        this.status_id_to = status_id_to;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
