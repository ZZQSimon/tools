package cn.com.easyerp.core.data;

import cn.com.easyerp.core.cache.ColumnDescribe;

public class ExportColumn {
    private ColumnDescribe column;
    private String value;

    public ExportColumn(ColumnDescribe column, String value) {
        this.column = column;
        this.value = value;
    }

    public ColumnDescribe getColumn() {
        return this.column;
    }

    public void setColumn(ColumnDescribe column) {
        this.column = column;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
