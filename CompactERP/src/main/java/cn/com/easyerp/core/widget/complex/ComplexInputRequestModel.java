package cn.com.easyerp.core.widget.complex;

import cn.com.easyerp.core.view.form.detail.DetailRequestModel;
import cn.com.easyerp.framework.enums.ActionType;

public class ComplexInputRequestModel extends DetailRequestModel {
    private ActionType action;
    private String sql;
    private boolean reload;

    public ActionType getAction() {
        return this.action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public boolean isReload() {
        return this.reload;
    }

    public void setReload(boolean reload) {
        this.reload = reload;
    }
}
