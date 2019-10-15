package cn.com.easyerp.batch;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;

@Widget("batch")
public class CsvImportFormModel extends BatchFormModel {
    private String tempTable;

    protected CsvImportFormModel(String parent, List<FieldModelBase> fields, BatchDescribe batch, int cols) {
        super(parent, fields, batch, cols);
    }

    @JsonIgnore
    public String getTempTable() {
        return this.tempTable;
    }

    public void setTempTable(String tempTable) {
        this.tempTable = tempTable;
    }
}
