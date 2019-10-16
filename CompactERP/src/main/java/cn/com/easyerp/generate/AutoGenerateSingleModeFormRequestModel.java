package cn.com.easyerp.generate;

import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.view.FormRequestModelBase;

public class AutoGenerateSingleModeFormRequestModel extends FormRequestModelBase<AutoGenerateSingleModeFormModel> {
    private ViewDataMap param;

    public ViewDataMap getParam() {
        return this.param;
    }

    public void setParam(ViewDataMap param) {
        this.param = param;
    }
}
