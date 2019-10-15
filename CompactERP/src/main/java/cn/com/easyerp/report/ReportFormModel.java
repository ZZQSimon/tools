package cn.com.easyerp.report;

import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("report")
public class ReportFormModel extends FormModelBase {
    private String report_id;
    private FilterModel filter;
    private String tableName;
    private String title;

    public ReportFormModel(String parent, String report_id, String tableName, String title) {
        super(ActionType.view, parent);
        this.report_id = report_id;
        this.tableName = tableName;
        this.filter = new FilterModel(tableName, "Print");
        this.title = title;
    }

    public String getReport_id() {
        return this.report_id;
    }

    public FilterModel getFilter() {
        return this.filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
