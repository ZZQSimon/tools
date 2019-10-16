package cn.com.easyerp.DeployTool.view;

import java.util.List;

import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@SuppressWarnings("rawtypes")
@Widget("chartReport")
public class ChartReportModel extends FormModelBase {
    private List<ReportModel> chartReports;
    private int type;

    protected ChartReportModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "chartReport";
    }

    public List<ReportModel> getChartReports() {
        return this.chartReports;
    }

    public void setChartReports(List<ReportModel> chartReports) {
        this.chartReports = chartReports;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
