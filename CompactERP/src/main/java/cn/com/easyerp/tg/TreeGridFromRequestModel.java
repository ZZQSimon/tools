package cn.com.easyerp.tg;

import java.util.List;

import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.core.view.TableFormRequestModel;

public class TreeGridFromRequestModel extends TableFormRequestModel<TreeGridFormModel> {
    private FilterRequestModel filter;
    private ViewDataMap fixedData;
    private List<String> ids;
    private boolean load = false;

    public FilterRequestModel getFilter() {
        return this.filter;
    }

    public void setFilter(FilterRequestModel filter) {
        this.filter = filter;
    }

    public ViewDataMap getFixedData() {
        return this.fixedData;
    }

    public void setFixedData(ViewDataMap fixedData) {
        this.fixedData = fixedData;
    }

    public List<String> getIds() {
        return this.ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public boolean isLoad() {
        return this.load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }
}
