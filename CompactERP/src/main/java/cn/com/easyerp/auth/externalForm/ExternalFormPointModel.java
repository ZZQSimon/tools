package cn.com.easyerp.auth.externalForm;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("externalFormPoint")
public class ExternalFormPointModel extends FormModelBase {
    private String url;
    private String errorMsg;

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    protected ExternalFormPointModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "ExternalFormPoint";
    }
}
