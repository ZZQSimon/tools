package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("tableDeploy")
public class TableDeployModel extends FormModelBase {
    private String tableName;

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    protected TableDeployModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "tableDeploy";
    }
}
