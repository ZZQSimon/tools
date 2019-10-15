package cn.com.easyerp.core.cache.style;

public class ColumnViewStyle {
    private ColumnRenderStyle ag;
    private ColumnRefRenderStyle ref;
    private ColumnMapViewStyle map;

    public ColumnRenderStyle getAg() {
        return this.ag;
    }

    public void setAg(ColumnRenderStyle ag) {
        this.ag = ag;
    }

    public ColumnRefRenderStyle getRef() {
        return this.ref;
    }

    public void setRef(ColumnRefRenderStyle ref) {
        this.ref = ref;
    }

    public ColumnMapViewStyle getMap() {
        return this.map;
    }

    public void setMap(ColumnMapViewStyle map) {
        this.map = map;
    }
}
