package cn.com.easyerp.core.view;

import java.util.Set;

import cn.com.easyerp.core.cache.style.TableViewStyle;

public class TableFormRequestModel<T extends FormModelBase> extends FormRequestModelBase<T> {
    private String table;
    private Set<TableViewStyle.Button> hideButtons;

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public void setHideButtons(Set<TableViewStyle.Button> hideButtons) {
        this.hideButtons = hideButtons;
    }

    public Set<TableViewStyle.Button> getHideButtons() {
        return this.hideButtons;
    }
}
