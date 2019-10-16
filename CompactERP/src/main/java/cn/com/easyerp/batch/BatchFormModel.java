package cn.com.easyerp.batch;

import java.util.List;

import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("batch")
public class BatchFormModel extends DetailFormModel {
    private String batchId;
    private int import_type;

    @SuppressWarnings({ "rawtypes" })
    protected BatchFormModel(String parent, List<FieldModelBase> fields, BatchDescribe batch, int cols) {
        super(ActionType.batch, parent, batch.getApi().getId(), fields, null, cols);
        this.batchId = batch.getBatch_id();
    }

    public String getBatchId() {
        return this.batchId;
    }

    public String getButtonText() {
        return "Exec";
    }

    public String getTitle() {
        return Common.getDataService().getBatchLabel(this.batchId);
    }

    protected String getView() {
        return "detail";
    }

    public int getImport_type() {
        return this.import_type;
    }

    public void setImport_type(int import_type) {
        this.import_type = import_type;
    }
}
