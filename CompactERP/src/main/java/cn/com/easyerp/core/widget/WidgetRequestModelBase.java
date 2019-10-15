package cn.com.easyerp.core.widget;

import cn.com.easyerp.core.ViewService;

public class WidgetRequestModelBase<T extends WidgetModelBase> extends Object {
    private String id;
    private T widget;

    public String getId() {
        return this.id;
    }

    @SuppressWarnings("unchecked")
    public void setId(String id) {
        this.id = id;
        this.widget = (T) ViewService.fetchWidgetModel(id);
    }

    public T getWidget() {
        return (T) this.widget;
    }
}