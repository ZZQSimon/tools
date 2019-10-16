package cn.com.easyerp.core.view.form.index;

import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class DesktopRequestModel<T extends WidgetModelBase> extends FormRequestModelBase<T> {
    private String itemId;

    public String getItemId() {
        return this.itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
