package cn.com.easyerp.core.widget;

public class FieldWithRepresentationModel extends FieldModelBase {
    private String text;

    public FieldWithRepresentationModel(String table, String column, Object value, String text) {
        super(table, column, value);
        this.text = text;
    }

    FieldWithRepresentationModel() {
    }

    public FieldModelBase copy() {
        FieldWithRepresentationModel field = (FieldWithRepresentationModel) super.copy();
        field.text = this.text;
        return field;
    }

    public String getText() {
        return this.text;
    }
}
