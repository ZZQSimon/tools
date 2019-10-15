package cn.com.easyerp.core.data;

import java.util.List;

public class exportXlsModel {
    private List<String> rids;
    private String formId;

    public List<String> getRids() {
        return this.rids;
    }

    public void setRids(List<String> rids) {
        this.rids = rids;
    }

    public String getFormId() {
        return this.formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }
}
