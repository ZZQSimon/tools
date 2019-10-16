package cn.com.easyerp.module.orion;

import java.util.List;

import cn.com.easyerp.batch.BatchFormModel;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;

@SuppressWarnings({ "rawtypes" })
@Widget("batch")
public class OrionImportFormModel extends BatchFormModel {
    protected OrionImportFormModel(String parent, List<FieldModelBase> fields, BatchDescribe batch, int cols) {
        super(parent, fields, batch, cols);
    }

    public String getButtonText() {
        return "Import";
    }
}
