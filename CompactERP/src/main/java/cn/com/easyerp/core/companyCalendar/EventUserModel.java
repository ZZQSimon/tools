package cn.com.easyerp.core.companyCalendar;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("eventUser")
public class EventUserModel extends FormModelBase {
    protected EventUserModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "eventUser";
    }
}
