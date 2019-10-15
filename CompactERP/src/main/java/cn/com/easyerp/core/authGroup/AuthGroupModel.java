package cn.com.easyerp.core.authGroup;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("authGroup")
public class AuthGroupModel extends FormModelBase {
    private List<MenuGroup> menuGroup;
    private List<MenuGroup> menuId;
    private List<MenuModel> menu;
    private List<AuthGroup> authGroupMember;

    public List<TableAction> getCheckboxAll() {
        return this.checkboxAll;
    }

    private List<MenuTree> menuTree;

    public void setCheckboxAll(List<TableAction> checkboxAll) {
        this.checkboxAll = checkboxAll;
    }

    private List<MenuTree> columnTree;
    private List<Table> table;

    public List<MenuTree> getColumnTree() {
        return this.columnTree;
    }

    private List<TableAction> checkboxAll;
    Map<String, List<AuthDataGroupDetail>> authDataGroupMap;

    public void setColumnTree(List<MenuTree> columnTree) {
        this.columnTree = columnTree;
    }

    public List<Table> getTable() {
        return this.table;
    }

    public void setTable(List<Table> table) {
        this.table = table;
    }

    public List<MenuTree> getMenuTree() {
        return this.menuTree;
    }

    public void setMenuTree(List<MenuTree> menuTree) {
        this.menuTree = menuTree;
    }

    public Map<String, List<AuthDataGroupDetail>> getAuthDataGroupMap() {
        return this.authDataGroupMap;
    }

    public void setAuthDataGroupMap(Map<String, List<AuthDataGroupDetail>> authDataGroupMap) {
        this.authDataGroupMap = authDataGroupMap;
    }

    public List<MenuGroup> getMenuGroup() {
        return this.menuGroup;
    }

    public List<MenuGroup> getMenuId() {
        return this.menuId;
    }

    public void setMenuId(List<MenuGroup> menuId) {
        this.menuId = menuId;
    }

    public void setMenuGroup(List<MenuGroup> menuGroup) {
        this.menuGroup = menuGroup;
    }

    public List<MenuModel> getMenu() {
        return this.menu;
    }

    public void setMenu(List<MenuModel> menu) {
        this.menu = menu;
    }

    public List<AuthGroup> getAuthGroupMember() {
        return this.authGroupMember;
    }

    public void setAuthGroupMember(List<AuthGroup> authGroupMember) {
        this.authGroupMember = authGroupMember;
    }

    public AuthGroupModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "AuthorityManagement";
    }
}
