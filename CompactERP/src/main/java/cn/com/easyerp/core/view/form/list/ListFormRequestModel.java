package cn.com.easyerp.core.view.form.list;

import java.util.Map;

import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.view.TableFormRequestModel;
import cn.com.easyerp.framework.enums.ActionType;

public class ListFormRequestModel extends TableFormRequestModel<ListFormModel> {
    private ActionType action = ActionType.view;

    private Map<String, Object> fixedData;

    private Map<String, FilterDescribe> defaultFilters;
    private Map<String, FilterDescribe> leftSelectFilters;
    private boolean leftSelect = false;
    private boolean load = true;
    private boolean control = true;
    private String tabIcon;

    public ActionType getAction() {
        return this.action;
    }

    public void setAction(ActionType action) {
        this.action = action;
    }

    public Map<String, Object> getFixedData() {
        return this.fixedData;
    }

    public void setFixedData(Map<String, Object> fixedData) {
        this.fixedData = fixedData;
    }

    public Map<String, FilterDescribe> getDefaultFilters() {
        return this.defaultFilters;
    }

    public void setDefaultFilters(Map<String, FilterDescribe> defaultFilters) {
        this.defaultFilters = defaultFilters;
    }

    public boolean isLoad() {
        return this.load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public boolean isControl() {
        return this.control;
    }

    public void setControl(boolean control) {
        this.control = control;
    }

    public boolean isLeftSelect() {
        return this.leftSelect;
    }

    public void setLeftSelect(boolean leftSelect) {
        this.leftSelect = leftSelect;
    }

    public Map<String, FilterDescribe> getLeftSelectFilters() {
        return this.leftSelectFilters;
    }

    public void setLeftSelectFilters(Map<String, FilterDescribe> leftSelectFilters) {
        this.leftSelectFilters = leftSelectFilters;
    }

    public String getTabIcon() {
        return this.tabIcon;
    }

    public void setTabIcon(String tabIcon) {
        this.tabIcon = tabIcon;
    }
}
