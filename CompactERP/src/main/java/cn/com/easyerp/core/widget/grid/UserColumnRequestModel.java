package cn.com.easyerp.core.widget.grid;

import java.util.List;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class UserColumnRequestModel extends FormRequestModelBase<UserColumnModel> {
    private String tableName;
    private List<UserColumn> userColumns;

    public List<UserColumn> getUserColumns() {
        return this.userColumns;
    }

    public void setUserColumns(List<UserColumn> userColumns) {
        this.userColumns = userColumns;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
