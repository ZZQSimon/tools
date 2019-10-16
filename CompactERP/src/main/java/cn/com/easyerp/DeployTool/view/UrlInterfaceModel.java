package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("UrlInterface")
public class UrlInterfaceModel extends FormModelBase {
    private String type;

    protected UrlInterfaceModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        if ("2".equals(this.type)) {
            return "urlInterface";
        }
        return "urlInterface_page";
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
