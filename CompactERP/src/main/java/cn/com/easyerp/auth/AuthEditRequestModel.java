package cn.com.easyerp.auth;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class AuthEditRequestModel<T extends WidgetModelBase> extends FormRequestModelBase<T> {
    private List<AuthControlDescribe> controls;
    private Map<String, List<String>> configMap;

    public List<AuthControlDescribe> getControls() {
        return this.controls;
    }

    public void setControls(List<AuthControlDescribe> controls) {
        this.controls = controls;
    }

    public Map<String, List<String>> getConfigMap() {
        return this.configMap;
    }

    public void setConfigMap(Map<String, List<String>> configMap) {
        this.configMap = configMap;
    }
}
