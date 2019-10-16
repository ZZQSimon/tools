package cn.com.easyerp.DeployTool.view;

import java.util.List;

import cn.com.easyerp.DeployTool.service.FunctionPoint;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class FunctionPointRequestModel extends FormRequestModelBase<FunctionPointModel> {
    private List<FunctionPoint> insert;
    private List<FunctionPoint> updated;
    private List<FunctionPoint> deleted;
    private String params;

    public List<FunctionPoint> getUpdated() {
        return this.updated;
    }

    public void setUpdated(List<FunctionPoint> updated) {
        this.updated = updated;
    }

    public String getParams() {
        return this.params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public List<FunctionPoint> getInsert() {
        return this.insert;
    }

    public void setInsert(List<FunctionPoint> insert) {
        this.insert = insert;
    }

    public List<FunctionPoint> getUpdate() {
        return this.updated;
    }

    public void setUpdate(List<FunctionPoint> update) {
        this.updated = update;
    }

    public List<FunctionPoint> getDeleted() {
        return this.deleted;
    }

    public void setDeleted(List<FunctionPoint> deleted) {
        this.deleted = deleted;
    }
}
