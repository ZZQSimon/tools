package cn.com.easyerp.chart;

import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class ChartFormRequestModel<T extends WidgetModelBase> extends FormRequestModelBase<T> {
    private String report_id;
    private Map<String, Object> fixedData;

    public String getReport_id() {
        return this.report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }

    public Map<String, Object> getFixedData() {
        return this.fixedData;
    }

    public void setFixedData(Map<String, Object> fixedData) {
        this.fixedData = fixedData;
    }
}
