package cn.com.easyerp.DeployTool.view;

import java.util.List;

import cn.com.easyerp.DeployTool.service.Url;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class UrlRequestModel extends FormRequestModelBase<UrlModel> {
    private List<Url> insert;
    private List<Url> update;
    private List<Url> deleted;

    public List<Url> getInsert() {
        return this.insert;
    }

    public void setInsert(List<Url> insert) {
        this.insert = insert;
    }

    public List<Url> getUpdate() {
        return this.update;
    }

    public void setUpdate(List<Url> update) {
        this.update = update;
    }

    public List<Url> getDeleted() {
        return this.deleted;
    }

    public void setDeleted(List<Url> deleted) {
        this.deleted = deleted;
    }
}
