package cn.com.easyerp.generate;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.cache.AutoGenTableDesc;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("agSingle")
public class AutoGenerateSingleModeFormModel extends DetailFormModel {
    private AutoGenTableDesc ag;

    public AutoGenerateSingleModeFormModel(String parent, String tableName, List<FieldModelBase> fields,
            AutoGenTableDesc ag) {
        super(ActionType.create, parent, tableName, fields, null, 1);
        this.ag = ag;
    }

    @JsonIgnore
    public AutoGenTableDesc getAg() {
        return this.ag;
    }
}
