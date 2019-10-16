package cn.com.easyerp.core.widget;

public class CheckBoxField extends FieldModelBase {
    public CheckBoxField(String table, String column, boolean checked) {
        super(table, column, Boolean.valueOf(checked));
    }

    public CheckBoxField() {
    }

    public boolean isChecked() {
        if (getValue() instanceof Integer) {
            if (((Integer) getValue()).intValue() == 1) {
                return true;
            }
            return false;
        }
        return ((Boolean) getValue()).booleanValue();
    }
}
