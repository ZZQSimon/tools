package cn.com.easyerp.core.data;

import cn.com.easyerp.core.view.TableFormRequestModel;

public class ImportRequestModel extends TableFormRequestModel {
    private String formId;
    private String fileId;
    private int import_type;

    public String getFormId() {
        return this.formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getImport_type() {
        return this.import_type;
    }

    public void setImport_type(int import_type) {
        this.import_type = import_type;
    }
}
