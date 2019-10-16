package cn.com.easyerp.core.widget.message;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("message")
public class MessageModel extends FormModelBase {
    protected MessageModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "message";
    }
}
