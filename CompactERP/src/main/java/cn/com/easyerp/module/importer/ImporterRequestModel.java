package cn.com.easyerp.module.importer;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.TableFormRequestModel;
import cn.com.easyerp.framework.enums.ActionType;

public class ImporterRequestModel<T extends FormModelBase> extends TableFormRequestModel<T> {
    private ActionType action = ActionType.create;

    private String fileId;

    private String customerId;
    private int typeId;

    public ActionType getAction() {
        return this.action;
    }

    public String getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFileId() {
        return this.fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public int getTypeId() {
        return this.typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
