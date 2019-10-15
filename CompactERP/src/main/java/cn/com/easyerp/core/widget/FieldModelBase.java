package cn.com.easyerp.core.widget;

import cn.com.easyerp.framework.exception.ApplicationException;

public class FieldModelBase extends WidgetModelBase {
    private Object value;
    private String table;
    private String column;
    private Object orig;

    public FieldModelBase(String table, String column, Object value) {
        this.table = table;
        this.column = column;
        this.value = this.orig = value;
    }

    FieldModelBase() {
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getTable() {
        return this.table;
    }

    public String getColumn() {
        return this.column;
    }

    public FieldModelBase copy() {
        return copy(getParent());
    }

    public FieldModelBase copy(String parent) {
        FieldModelBase field;
        try {
            field = (FieldModelBase) getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
        field.table = this.table;
        field.column = this.column;
        field.value = this.value;
        field.setParent(parent);
        return copyExtra(field);
    }

    public FieldModelBase copyExtra(FieldModelBase field) {
        return field;
    }

    public String idPrefix() {
        return "w";
    }

    public Object getOrig() {
        return this.orig;
    }

    public void setOrig(Object orig) {
        this.orig = orig;
    }
}
