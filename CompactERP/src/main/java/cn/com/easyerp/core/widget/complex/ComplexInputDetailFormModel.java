package cn.com.easyerp.core.widget.complex;

import java.util.List;

import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("detail")
public class ComplexInputDetailFormModel extends DetailFormModel {
    public ComplexInputDetailFormModel(ActionType action, String parent, String tableName, List<FieldModelBase> fields,
            int cols) {
        super(action, parent, tableName, fields, null, cols);
    }

    public String urlBase() {
        return "/widget/complex";
    }

    protected String getView() {
        return "complexDetail";
    }
}
