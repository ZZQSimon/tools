package cn.com.easyerp.core.data;

import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class ImportDataModel<T extends WidgetModelBase> extends FormRequestModelBase<T> {
    private Map<String, ViewDataMap> data;

    public Map<String, ViewDataMap> getData() {
        return this.data;
    }

    public void setData(Map<String, ViewDataMap> data) {
        this.data = data;
    }
}
