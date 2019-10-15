package cn.com.easyerp.chart;

import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("chart")
public class ChartFormModel extends TableBasedFormModel {
    private String report_id;
    private WidgetModelBase chart;
    private FilterModel filter;

    public ChartFormModel(String parent, String report_id, WidgetModelBase chart, String tableName) {
        super(ActionType.view, parent, tableName);
        this.report_id = report_id;
        this.chart = chart;
        this.filter = new FilterModel(tableName, "Show");
    }

    public String getReport_id() {
        return this.report_id;
    }

    public WidgetModelBase getChart() {
        return this.chart;
    }

    public FilterModel getFilter() {
        return this.filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }
}
