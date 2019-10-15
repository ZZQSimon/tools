package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.List;

public class TableGroupDescribe {
    private String id;
    private List<ColumnDescribe> columns;

    public TableGroupDescribe(String id) {
        this.id = id;
        this.columns = new ArrayList<>();
    }

    public void addColumn(ColumnDescribe column) {
        this.columns.add(column);
    }

    public String getId() {
        return this.id;
    }

    public List<ColumnDescribe> getColumns() {
        return this.columns;
    }
}
