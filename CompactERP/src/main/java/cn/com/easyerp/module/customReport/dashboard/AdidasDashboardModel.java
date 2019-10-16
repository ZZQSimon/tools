package cn.com.easyerp.module.customReport.dashboard;

import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.form.FormWithFilterModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("adidasDashboard")
public class AdidasDashboardModel extends FormWithFilterModel {
    private String type;

    protected AdidasDashboardModel(String parent, String tableName, FilterModel filter) {
        super(ActionType.view, parent, tableName, filter);
    }

    public String getTitle() {
        return "adidasDashboard";
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
