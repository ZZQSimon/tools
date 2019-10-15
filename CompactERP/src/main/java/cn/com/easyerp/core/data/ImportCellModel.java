package cn.com.easyerp.core.data;

public class ImportCellModel {
    private String table;
    private String value;

    public String getValue() {
        return this.value;
    }

    private int col;
    private boolean defalut;
    private boolean hidden;

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getDefalut() {
        return this.defalut;
    }

    public void setDefalut(boolean defalut) {
        this.defalut = defalut;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
