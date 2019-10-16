package cn.com.easyerp.DeployTool.service;

import java.util.List;

public class PassageRowMap {
    private List<PassageRow> inserted;

    public List<PassageRow> getInserted() {
        return this.inserted;
    }

    private List<PassageRow> updated;
    private List<PassageRow> deleted;

    public void setInserted(List<PassageRow> inserted) {
        this.inserted = inserted;
    }

    public List<PassageRow> getUpdated() {
        return this.updated;
    }

    public void setUpdated(List<PassageRow> updated) {
        this.updated = updated;
    }

    public List<PassageRow> getDeleted() {
        return this.deleted;
    }

    public void setDeleted(List<PassageRow> deleted) {
        this.deleted = deleted;
    }
}
