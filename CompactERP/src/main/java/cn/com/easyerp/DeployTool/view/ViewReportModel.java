package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("viewReport")
public class ViewReportModel extends FormModelBase {
    protected ViewReportModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "viewReport";
    }
}
