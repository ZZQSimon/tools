package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("checkWork")
public class CheckWorkModel extends FormModelBase {
    protected CheckWorkModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "checkWork";
    }
}
