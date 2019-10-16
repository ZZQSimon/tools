package cn.com.easyerp.DeployTool.view;

import java.util.List;

import cn.com.easyerp.DeployTool.service.ViewReportDetails;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class ViewReportRequestModel extends FormRequestModelBase<ViewReportModel> {
    private List<ViewReportDetails> insert;

    public List<ViewReportDetails> getInsert() {
        return this.insert;
    }

    private List<ViewReportDetails> deleted;
    private List<ViewReportDetails> update;

    public void setInsert(List<ViewReportDetails> insert) {
        this.insert = insert;
    }

    public List<ViewReportDetails> getDeleted() {
        return this.deleted;
    }

    public void setDeleted(List<ViewReportDetails> deleted) {
        this.deleted = deleted;
    }

    public List<ViewReportDetails> getUpdate() {
        return this.update;
    }

    public void setUpdate(List<ViewReportDetails> update) {
        this.update = update;
    }
}
