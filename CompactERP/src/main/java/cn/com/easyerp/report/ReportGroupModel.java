package cn.com.easyerp.report;

public class ReportGroupModel {
    private String value;
    private int col;

    public String getValue() {
        return this.value;
    }

    private int startrow;
    private int endrow;

    public void setValue(String value) {
        this.value = value;
    }

    public int getCol() {
        return this.col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getStartrow() {
        return this.startrow;
    }

    public void setStartrow(int startrow) {
        this.startrow = startrow;
    }

    public int getEndrow() {
        return this.endrow;
    }

    public void setEndrow(int endrow) {
        this.endrow = endrow;
    }
}
