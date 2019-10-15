package cn.com.easyerp.core.widget;

import java.util.Map;

public class FieldWithRefModel extends FieldModelBase {
    private Map<String, Object> ref;

    public FieldWithRefModel(String table, String column, Object value, Map<String, Object> ref) {
        super(table, column, value);
        this.ref = ref;
    }

    FieldWithRefModel() {
    }

    public FieldModelBase copy() {
        FieldWithRefModel field = (FieldWithRefModel) super.copy();
        field.ref = this.ref;
        return field;
    }

    public Map<String, Object> getRef() {
        return this.ref;
    }

    public void setRef(Map<String, Object> ref) {
        this.ref = ref;
    }
}
