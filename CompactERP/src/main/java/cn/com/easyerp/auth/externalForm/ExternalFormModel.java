package cn.com.easyerp.auth.externalForm;

import java.util.Map;

import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("externalForm")
public class ExternalFormModel extends FormModelBase {
    private String externalFormUrl;
    private Map<String, Object> externalFormData;
    private TableDescribe table;
    private Map<String, Map<String, I18nDescribe>> dicMap;
    private String encryptStr;
    private String tableId;
    private Map<String, Object> param;

    public String getTableId() {
        return this.tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public Map<String, Map<String, I18nDescribe>> getDicMap() {
        return this.dicMap;
    }

    public void setDicMap(Map<String, Map<String, I18nDescribe>> dicMap) {
        this.dicMap = dicMap;
    }

    public TableDescribe getTable() {
        return this.table;
    }

    public void setTable(TableDescribe table) {
        this.table = table;
    }

    public Map<String, Object> getExternalFormData() {
        return this.externalFormData;
    }

    public void setExternalFormData(Map<String, Object> externalFormData) {
        this.externalFormData = externalFormData;
    }

    public String getExternalFormUrl() {
        return this.externalFormUrl;
    }

    public void setExternalFormUrl(String externalFormUrl) {
        this.externalFormUrl = externalFormUrl;
    }

    protected ExternalFormModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "ExternalForm";
    }

    public String getEncryptStr() {
        return this.encryptStr;
    }

    public void setEncryptStr(String encryptStr) {
        this.encryptStr = encryptStr;
    }
}
