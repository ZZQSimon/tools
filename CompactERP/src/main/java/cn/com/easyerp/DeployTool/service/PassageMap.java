package cn.com.easyerp.DeployTool.service;

import java.util.List;

public class PassageMap {
    private List<Passage> inserted;

    public List<Passage> getInserted() {
        return this.inserted;
    }

    private List<Passage> updated;
    private List<Passage> deleted;

    public void setInserted(List<Passage> inserted) {
        this.inserted = inserted;
    }

    public List<Passage> getUpdated() {
        return this.updated;
    }

    public void setUpdated(List<Passage> updated) {
        this.updated = updated;
    }

    public List<Passage> getDeleted() {
        return this.deleted;
    }

    public void setDeleted(List<Passage> deleted) {
        this.deleted = deleted;
    }
}
