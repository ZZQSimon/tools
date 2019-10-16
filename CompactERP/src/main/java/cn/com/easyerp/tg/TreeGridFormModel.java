package cn.com.easyerp.tg;

import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("tg")
public class TreeGridFormModel extends TableBasedFormModel {
    private FilterModel filter;
    private TreeGridController.TreeType type;
    private ViewDataMap filterData;
    private boolean load;

    protected TreeGridFormModel(String parent, String tableName, FilterModel filter, TreeGridController.TreeType type,
            boolean load) {
        super(ActionType.view, parent, tableName);
        this.filter = filter;
        this.type = type;
        this.load = load;
    }

    public FilterModel getFilter() {
        return this.filter;
    }

    public void setFilter(FilterModel filter) {
        this.filter = filter;
    }

    public TreeGridController.TreeType getType() {
        return this.type;
    }

    public void setType(TreeGridController.TreeType type) {
        this.type = type;
    }

    public ViewDataMap getFilterData() {
        return this.filterData;
    }

    public void setFilterData(ViewDataMap filterData) {
        this.filterData = filterData;
    }

    public boolean isLoad() {
        return this.load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }
}
