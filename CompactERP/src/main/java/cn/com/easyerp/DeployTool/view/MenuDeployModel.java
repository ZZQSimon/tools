package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("menuDeploy")
public class MenuDeployModel extends FormModelBase {
    protected MenuDeployModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "menuDeploy";
    }
}
