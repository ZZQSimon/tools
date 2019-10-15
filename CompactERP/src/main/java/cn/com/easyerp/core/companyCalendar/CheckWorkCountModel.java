package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("checkWorkCount")
public class CheckWorkCountModel extends FormModelBase {
    protected CheckWorkCountModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "checkWorkCount";
    }
}
