package cn.com.easyerp.tree;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.TableFormRequestModel;

public class TreeFormRequestModel extends TableFormRequestModel<TreeFormModel> {
    private Map<String, Object> param;
    private String conditions;
    private int searchMode = 1;

    private boolean extend = false;

    private List<String> ids;

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public String getConditions() {
        return this.conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public int getSearchMode() {
        return this.searchMode;
    }

    public void setSearchMode(int searchMode) {
        this.searchMode = searchMode;
    }

    public boolean isExtend() {
        return this.extend;
    }

    public void setExtend(boolean extend) {
        this.extend = extend;
    }

    public List<String> getIds() {
        return this.ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
