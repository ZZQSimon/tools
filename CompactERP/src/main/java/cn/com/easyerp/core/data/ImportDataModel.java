package cn.com.easyerp.core.data;

import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class ImportDataModel extends FormRequestModelBase {
    private Map<String, ViewDataMap> data;

    public Map<String, ViewDataMap> getData() {
        return this.data;
    }

    public void setData(Map<String, ViewDataMap> data) {
        this.data = data;
    }
}
