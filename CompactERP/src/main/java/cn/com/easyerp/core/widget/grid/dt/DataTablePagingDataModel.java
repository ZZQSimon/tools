package cn.com.easyerp.core.widget.grid.dt;

import java.util.List;

import cn.com.easyerp.core.widget.grid.RecordModel;

public class DataTablePagingDataModel {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<RecordModel> records;
    private String error;

    public DataTablePagingDataModel(DataTablePagingParameterModel paging) {
        this.draw = paging.getDraw();
    }

    public int getDraw() {
        return this.draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getRecordsTotal() {
        return this.recordsTotal;
    }

    public void setRecordsTotal(int recordsTotal) {
        this.recordsTotal = recordsTotal;
    }

    public int getRecordsFiltered() {
        return this.recordsFiltered;
    }

    public void setRecordsFiltered(int recordsFiltered) {
        this.recordsFiltered = recordsFiltered;
    }

    public List<RecordModel> getRecords() {
        return this.records;
    }

    public void setRecords(List<RecordModel> records) {
        this.records = records;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
