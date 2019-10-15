package cn.com.easyerp.core.data;

import java.util.List;

import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.RecordModel;

public class ImportRecordModel extends RecordModel {
    private int errorCode;
    private String errorParam;
    private int index;

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ImportRecordModel(List<FieldModelBase> fields, int errorCode, String errorParam) {
        super(fields);
        this.errorCode = errorCode;
        this.errorParam = errorParam;
    }

    public ImportRecordModel(List<FieldModelBase> fields, int errorCode, String errorParam, int index) {
        super(fields);
        this.errorCode = errorCode;
        this.errorParam = errorParam;
        this.index = index;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorParam() {
        return this.errorParam;
    }
}
