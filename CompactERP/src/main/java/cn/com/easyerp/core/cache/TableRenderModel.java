package cn.com.easyerp.core.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

public class TableRenderModel {
    private static TypeReference<StyleModel> renderJsonRef = new TypeReference<StyleModel>() {

    };

    private String table_id;

    private String column;
    private int level;
    private int seq;

    public String get_selected() {
        return this._selected;
    }

    private String formula;
    private String color;
    private StyleModel style;
    private String _selected;

    public void set_selected(String _selected) {
        this._selected = _selected;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    @JsonIgnore
    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public StyleModel getStyle() {
        return this.style;
    }

    private static class StyleModel {
        String color;

        String bg;

        public String getColor() {
            return this.color;
        }

        public String getBg() {
            return this.bg;
        }
    }
}
