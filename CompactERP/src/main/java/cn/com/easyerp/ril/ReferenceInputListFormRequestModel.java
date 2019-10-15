package cn.com.easyerp.ril;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class ReferenceInputListFormRequestModel<T extends WidgetModelBase> extends FormRequestModelBase<T> {
    private String table;
    private String source;
    private List<String> sourceFilters;
    private Map<String, String> mapping;
    private Map<String, Object> filterData;
    private Map<String, Map<String, Object>> data;

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<String> getSourceFilters() {
        return this.sourceFilters;
    }

    public void setSourceFilters(List<String> sourceFilters) {
        this.sourceFilters = sourceFilters;
    }

    public Map<String, String> getMapping() {
        return this.mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    public Map<String, Object> getFilterData() {
        return this.filterData;
    }

    public void setFilterData(Map<String, Object> filterData) {
        this.filterData = filterData;
    }

    public Map<String, Map<String, Object>> getData() {
        return this.data;
    }

    public void setData(Map<String, Map<String, Object>> data) {
        this.data = data;
    }
}
