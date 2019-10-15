package cn.com.easyerp.core.data;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.StdGridModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("import")
public class ImportFormModel extends TableBasedFormModel {
    private StdGridModel grid;
    private String tableName;
    private Map<String, String> msgMap;
    private List<DatabaseDataMap> data;
    private String tip;
    private boolean noImportSql;
    private int import_type;

    public ImportFormModel(String parent, String tableName, StdGridModel grid, Map<String, String> msgMap,
            List<DatabaseDataMap> data, String tip, boolean noImportSql) {
        super(ActionType.view, parent, tableName);
        this.grid = grid;
        this.tableName = tableName;
        this.msgMap = msgMap;
        this.data = data;
        this.tip = tip;
        this.noImportSql = noImportSql;
    }

    public String getTableName() {
        return this.tableName;
    }

    public StdGridModel getGrid() {
        return this.grid;
    }

    public Map<String, String> getMsgMap() {
        return this.msgMap;
    }

    @JsonIgnore
    public List<DatabaseDataMap> getData() {
        return this.data;
    }

    public int total() {
        if (this.data == null) {
            return this.grid.getRecords().size();
        }
        return this.data.size();
    }

    public String getTip() {
        return this.tip;
    }

    public boolean isNoImportSql() {
        return this.noImportSql;
    }

    public int getImport_type() {
        return this.import_type;
    }

    public void setImport_type(int import_type) {
        this.import_type = import_type;
    }
}
