package cn.com.easyerp.core.widget.message;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("messageDetail")
public class MessageDetailModel extends FormModelBase {
    public MessageDescribe message;

    protected MessageDetailModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "messageDetail";
    }

    public MessageDescribe getMessage() {
        return this.message;
    }

    public void setMessage(MessageDescribe message) {
        this.message = message;
    }
}
