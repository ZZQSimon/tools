package cn.com.easyerp.module.customReport;

import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class CustomReportRequestModel extends FormRequestModelBase<CustomReportModel> {
    private String filterTable;
    private String type;
    private FilterRequestModel filter;

    public String getFilterTable() {
        return this.filterTable;
    }

    public void setFilterTable(String filterTable) {
        this.filterTable = filterTable;
    }

    public FilterRequestModel getFilter() {
        return this.filter;
    }

    public void setFilter(FilterRequestModel filter) {
        this.filter = filter;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
