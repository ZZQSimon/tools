package cn.com.easyerp.core.api;

import java.util.Map;

import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ViewDataMap;

public class ApiRequestParam {
    private ViewDataMap files;
    private Map<String, ViewDataMap> data;
    DatabaseDataMap filesParams = null;

    public ViewDataMap getFiles() {
        return this.files;
    }

    public void setFiles(ViewDataMap files) {
        this.files = files;
    }

    public Map<String, ViewDataMap> getData() {
        return this.data;
    }

    public void setData(Map<String, ViewDataMap> data) {
        this.data = data;
    }
}
