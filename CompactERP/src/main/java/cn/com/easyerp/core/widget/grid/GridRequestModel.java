package cn.com.easyerp.core.widget.grid;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;
import cn.com.easyerp.core.widget.grid.dt.DataTablePagingParameterModel;

public class GridRequestModel<T extends WidgetModelBase> extends WidgetRequestModelBase<T> {
    private List<String> ids;
    private Map<String, Object> param;
    private FilterRequestModel filter;
    private DataTablePagingParameterModel paging;
    private Map<String, Integer> reorder;
    private TriggerDescribe triggerRequestParams;
    private Map<String, TriggerDescribe> childTriggerRequestParams;
    private int isApproveSelect;
    private Map<String, Object> approveSelectParam;
    private boolean isRefChild;

    public List<String> getIds() {
        return this.ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public FilterRequestModel getFilter() {
        return this.filter;
    }

    public void setFilter(FilterRequestModel filter) {
        this.filter = filter;
    }

    public DataTablePagingParameterModel getPaging() {
        return this.paging;
    }

    public void setPaging(DataTablePagingParameterModel paging) {
        this.paging = paging;
    }

    public Map<String, Integer> getReorder() {
        return this.reorder;
    }

    public void setReorder(Map<String, Integer> reorder) {
        this.reorder = reorder;
    }

    public TriggerDescribe getTriggerRequestParams() {
        return this.triggerRequestParams;
    }

    public void setTriggerRequestParams(TriggerDescribe triggerRequestParams) {
        this.triggerRequestParams = triggerRequestParams;
    }

    public int getIsApproveSelect() {
        return this.isApproveSelect;
    }

    public void setIsApproveSelect(int isApproveSelect) {
        this.isApproveSelect = isApproveSelect;
    }

    public Map<String, TriggerDescribe> getChildTriggerRequestParams() {
        return this.childTriggerRequestParams;
    }

    public void setChildTriggerRequestParams(Map<String, TriggerDescribe> childTriggerRequestParams) {
        this.childTriggerRequestParams = childTriggerRequestParams;
    }

    public boolean isRefChild() {
        return this.isRefChild;
    }

    public void setRefChild(boolean isRefChild) {
        this.isRefChild = isRefChild;
    }

    public Map<String, Object> getApproveSelectParam() {
        return this.approveSelectParam;
    }

    public void setApproveSelectParam(Map<String, Object> approveSelectParam) {
        this.approveSelectParam = approveSelectParam;
    }
}
