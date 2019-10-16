package cn.com.easyerp.DeployTool.view;

import java.util.Map;

import cn.com.easyerp.DeployTool.service.AddTableDeploy;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class TableDeployRequestModel extends FormRequestModelBase<TableDeployModel> {
    private Map<String, AddTableDeploy> editData;
    private Map<String, AddTableDeploy> addTables;
    private Map<String, AddTableDeploy> deleteTables;
    private String table_id;

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public Map<String, AddTableDeploy> getEditData() {
        return this.editData;
    }

    public void setEditData(Map<String, AddTableDeploy> editData) {
        this.editData = editData;
    }

    public Map<String, AddTableDeploy> getAddTables() {
        return this.addTables;
    }

    public void setAddTables(Map<String, AddTableDeploy> addTables) {
        this.addTables = addTables;
    }

    public Map<String, AddTableDeploy> getDeleteTables() {
        return this.deleteTables;
    }

    public void setDeleteTables(Map<String, AddTableDeploy> deleteTables) {
        this.deleteTables = deleteTables;
    }
}
