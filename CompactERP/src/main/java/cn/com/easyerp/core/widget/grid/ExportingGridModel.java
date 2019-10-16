package cn.com.easyerp.core.widget.grid;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class ExportingGridModel extends StdGridModel {
    public ExportingGridModel(String table) {
        super(table);
    }

    public void setRecords(List<? extends RecordModel> records) {
        setRecords(records, false);
    }
}
