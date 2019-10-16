package cn.com.easyerp.module.tree2;

import java.util.List;

import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("tree2")
public class Tree2FormModel extends TableBasedFormModel {
    private List<FieldModelBase> fields;
    private List<FieldModelBase> fieldsCondition;

    public Tree2FormModel(String parent, String tableName, List<FieldModelBase> fields,
            List<FieldModelBase> fieldsCondition) {
        super(ActionType.edit, parent, tableName);
        this.fields = fields;
        this.fieldsCondition = fieldsCondition;
    }

    public List<FieldModelBase> getFields() {
        return this.fields;
    }

    public void setFields(List<FieldModelBase> fields) {
        this.fields = fields;
    }

    public List<FieldModelBase> getFieldsCondition() {
        return this.fieldsCondition;
    }

    public void setFieldsCondition(List<FieldModelBase> fieldsCondition) {
        this.fieldsCondition = fieldsCondition;
    }
}
