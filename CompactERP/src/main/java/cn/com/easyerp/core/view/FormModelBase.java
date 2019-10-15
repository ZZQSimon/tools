package cn.com.easyerp.core.view;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.framework.enums.ActionType;

public abstract class FormModelBase extends WidgetModelBase {

    public static final String FORM_KEY = "form";
    private List<WidgetModelBase> widgets;
    private ActionType action;
    private String tabIcon;

    protected FormModelBase(ActionType action, String parent) {
        setParent(parent);
        this.action = action;
    }

    public List<WidgetModelBase> getWidgets() {
        return this.widgets;
    }

    public void setWidgets(List<WidgetModelBase> widgets) {
        this.widgets = widgets;
    }

    public ActionType getAction() {
        return this.action;
    }

    protected void setAction(ActionType action) {
        this.action = action;
    }

    @JsonIgnore
    protected String getView() {
        return getWidgetName();
    }

    public abstract String getTitle();

    public String getIcon() {
        return getWidgetName();
    }

    public String getTabIcon() {
        return this.tabIcon;
    }

    public void setTabIcon(String tabIcon) {
        this.tabIcon = tabIcon;
    }

    public String idPrefix() {
        return "f";
    }

    public boolean cacheNeeded() {
        return false;
    }

    public String getWidgetType() {
        return "form";
    }
}