package cn.com.easyerp.core.widget.grid;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

@JsonIgnoreProperties({ "records" })
public class StdGridModel extends WidgetModelBase {
    private String table;
    private List<RecordModel> records = new ArrayList<>();

    public StdGridModel(String table) {
        this.table = table;
    }

    public List<RecordModel> getRecords() {
        return this.records;
    }

    public void setRecords(List<? extends RecordModel> records) {
        setRecords(records, true);
    }

    @SuppressWarnings("unchecked")
    protected void setRecords(List<? extends RecordModel> records, boolean cache) {
        if (this.records != null) {
            List<WidgetModelBase> list = new ArrayList<WidgetModelBase>();
            for (RecordModel record : this.records) {
                list.add(record);
                for (FieldModelBase field : record.getFields())
                    list.add(field);
            }
            ViewService.removeWidgetsFromCache(list);
        }
        this.records = (List<RecordModel>) records;
        if (this.records == null)
            this.records = new ArrayList<>();
        for (RecordModel record : this.records)
            record.setParent(getId());
        if (!cache || this.records.size() == 0 || getParent() == null)
            return;
        ViewService.cacheRequestWidgets(ViewService.fetchFormModel(getParent()));
    }

    public void reset() {
        setRecords(null);
    }

    public String getTable() {
        return this.table;
    }

    public String idPrefix() {
        return "g";
    }

    public void addRecord(RecordModel record) {
        record.setStatus(RecordModel.Status.inserted);
        this.records.add(record);
        ViewService.cacheRequestWidgets(ViewService.fetchFormModel(getParent()));
    }

    public void updateRecord(RecordModel record, List<FieldModelBase> data) {
        if (record.getStatus() == RecordModel.Status.none)
            record.setStatus(RecordModel.Status.updated);
        record.updateFields(data);
        ViewService.cacheRequestWidgets(ViewService.fetchFormModel(getParent()));
    }

    public String getWidgetType() {
        return "grid";
    }
}
