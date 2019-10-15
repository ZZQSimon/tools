package cn.com.easyerp.core.view;

import java.util.Set;

import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.style.TableViewStyle;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;

public class TableBasedFormModel extends FormModelBase {
    private String tableName;
    private Set<TableViewStyle.Button> hideButtons;

    protected TableBasedFormModel(ActionType action, String parent, String tableName) {
        super(action, parent);
        this.tableName = tableName;
    }

    public String getTableName() {
        return this.tableName;
    }

    public String getTitle() {
        DataService dataService = Common.getDataService();
        return dataService.getTableLabel(getTableName());
    }

    public void setHideButtons(Set<TableViewStyle.Button> hideButtons) {
        TableDescribe table = Common.getDataService().getTableDesc(this.tableName);
        if (table.getViewStyle() != null && table.getViewStyle().getHideButtons() != null)
            if (hideButtons != null) {
                hideButtons.addAll(table.getViewStyle().getHideButtons());
            } else {
                hideButtons = table.getViewStyle().getHideButtons();
            }
        this.hideButtons = hideButtons;
    }

    public boolean button(String name) {
        TableViewStyle.Button button = TableViewStyle.Button.valueOf(name);
        return (this.hideButtons == null
                || (!this.hideButtons.contains(TableViewStyle.Button.all) && !this.hideButtons.contains(button)));
    }
}