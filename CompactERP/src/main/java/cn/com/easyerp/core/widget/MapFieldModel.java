package cn.com.easyerp.core.widget;

import java.util.Map;

public class MapFieldModel extends FieldModelBase {
    private Map<String, String> selection;
    private boolean modified = false;

    MapFieldModel() {
    }

    public MapFieldModel(String table, String column, Object value, Map<String, String> selection) {
        super(table, column, value);
        this.selection = selection;
    }

    public Map<String, String> getSelection() {
        return this.selection;
    }

    public void setSelection(Map<String, String> selection) {
        this.selection = selection;
        this.modified = true;
    }

    public boolean isModified() {
        return this.modified;
    }

    public FieldModelBase copyExtra(FieldModelBase field) {
        MapFieldModel f = (MapFieldModel) field;
        f.selection = this.selection;
        f.modified = this.modified;
        return field;
    }
}
