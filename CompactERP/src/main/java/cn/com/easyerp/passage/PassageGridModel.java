package cn.com.easyerp.passage;

import java.util.List;

import cn.com.easyerp.core.widget.WidgetModelBase;

public class PassageGridModel extends WidgetModelBase {
    private List<PassageRecordModel> records;

    public List<PassageRecordModel> getRecords() {
        return this.records;
    }

    public void setRecords(List<PassageRecordModel> records) {
        this.records = records;
    }
}
