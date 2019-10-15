package cn.com.easyerp.auth;

import java.util.List;

import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.framework.common.Common;

@Widget("authMenu")
public class AuthMenuFormModel extends AuthGroupFormModel {
    private List<MenuModel> menu;
    private List<MenuModel> shortcut;
    private List<AuthConfigDescribe> configs;

    protected AuthMenuFormModel(List<AuthControlDescribe> controls) {
        super(controls);
    }

    public String getTitle() {
        return Common.getDataService().getMessageText("Menu", new Object[0]);
    }

    public List<MenuModel> getMenu() {
        return this.menu;
    }

    public void setMenu(List<MenuModel> menu) {
        this.menu = menu;
    }

    public List<MenuModel> getShortcut() {
        return this.shortcut;
    }

    public void setShortcut(List<MenuModel> shortcut) {
        this.shortcut = shortcut;
    }

    public List<AuthConfigDescribe> getConfigs() {
        return this.configs;
    }

    public void setConfigs(List<AuthConfigDescribe> configs) {
        this.configs = configs;
    }
}
