package cn.com.easyerp.core.widget;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.view.FormModelBase;

public class WidgetModelBase {
    private String id;
    private String parent = null;

    private FormModelBase form;

    public WidgetModelBase() {
        this.id = ViewService.getNextTagId(idPrefix());
        if (cacheNeeded()) {
            ViewService.cacheWidgetToRequest(this);
        }
    }

    protected WidgetModelBase(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String getWidgetName() {
        if (getClass().isAnnotationPresent(Widget.class)) {
            return ((Widget) getClass().getAnnotation(Widget.class)).value();
        }
        String[] strs = getClass().getName().split("\\.");
        String name = strs[strs.length - 1];
        if (name.endsWith("Model"))
            name = name.substring(0, name.length() - 5);
        return name;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @JsonIgnore
    public FormModelBase getForm() {
        return this.form;
    }

    public void setForm(FormModelBase form) {
        this.form = form;
    }

    public boolean cacheNeeded() {
        return true;
    }

    public String idPrefix() {
        return "w";
    }

    public String getWidgetType() {
        return "widget";
    }
}
