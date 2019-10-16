package cn.com.easyerp.range;

import java.util.List;

import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("range")
public class RangeFormModel extends DetailFormModel {
    private String mode;
    private String groupCols;
    private String tableName;
    private List<FieldModelBase> filter;
    private List<FieldModelBase> record;

    public RangeFormModel(ActionType action, String parent, String mode, String tableName, String groupCols,
            List<FieldModelBase> filter, List<FieldModelBase> record, int cols, List<GridModel> grid) {
        super(action, parent, tableName, record, grid, cols);
        this.mode = mode;
        this.groupCols = groupCols;
        this.tableName = tableName;
        this.filter = filter;
        this.record = record;
    }

    public String getMode() {
        return this.mode;
    }

    public String getGroupCols() {
        return this.groupCols;
    }

    public String getTableName() {
        return this.tableName;
    }

    public List<FieldModelBase> getFilter() {
        return this.filter;
    }

    public List<FieldModelBase> getRecord() {
        return this.record;
    }
}
