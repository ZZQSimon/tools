package cn.com.easyerp.DeployTool.view;

import java.util.List;

import cn.com.easyerp.DeployTool.service.ABatch;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class BatchRequestModel extends FormRequestModelBase<BatchModel> {
    private List<ABatch> insert;

    public List<ABatch> getInsert() {
        return this.insert;
    }

    private List<ABatch> update;
    private List<ABatch> deleted;

    public void setInsert(List<ABatch> insert) {
        this.insert = insert;
    }

    public List<ABatch> getUpdate() {
        return this.update;
    }

    public void setUpdate(List<ABatch> update) {
        this.update = update;
    }

    public List<ABatch> getDeleted() {
        return this.deleted;
    }

    public void setDeleted(List<ABatch> deleted) {
        this.deleted = deleted;
    }
}
