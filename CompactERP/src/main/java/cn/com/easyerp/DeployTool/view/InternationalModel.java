package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("international")
public class InternationalModel extends FormModelBase {
    protected InternationalModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "international";
    }
}
