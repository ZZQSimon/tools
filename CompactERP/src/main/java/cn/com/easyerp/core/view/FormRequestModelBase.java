package cn.com.easyerp.core.view;

import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;

public class FormRequestModelBase<T extends WidgetModelBase> extends WidgetRequestModelBase<T> {
    private String parent;
    private String menuId;
    private int import_type;
    private String tabIcon;

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public int getImport_type() {
        return this.import_type;
    }

    public void setImport_type(int import_type) {
        this.import_type = import_type;
    }

    public String getTabIcon() {
        return this.tabIcon;
    }

    public void setTabIcon(String tabIcon) {
        this.tabIcon = tabIcon;
    }
}
