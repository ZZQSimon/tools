package cn.com.easyerp.core.widget.grid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.companyCalendar.CalendarEvent;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class RecordModel extends WidgetModelBase {
    public static enum Status {
        inserted, updated, deleted, none;
    }

    private List<FieldModelBase> fields;
    private Map<String, FieldModelBase> fieldMap;
    private DatabaseDataMap valueMap;
    private Status status;
    private List<CalendarEvent> calendarEvents;
    private String rowColor;
    private String columnColor;
    private String ref____name_Expression;

    public RecordModel(List<FieldModelBase> fields) {
        this(fields, Status.none);
    }

    public RecordModel(List<FieldModelBase> fields, Status status) {
        setFields(fields);
        this.status = status;
    }

    public List<FieldModelBase> getFields() {
        return this.fields;
    }

    public void setFields(List<FieldModelBase> fields) {
        this.fields = fields;
        this.fieldMap = new HashMap<>();
        this.valueMap = new DatabaseDataMap();
        for (FieldModelBase field : fields) {
            field.setParent(getId());
            this.fieldMap.put(field.getColumn(), field);
            this.valueMap.put(field.getColumn(), field.getValue());
        }
    }

    public void updateFields(List<FieldModelBase> fields) {
        List<FieldModelBase> list = this.fields;
        setFields(fields);
        for (FieldModelBase field : list) {
            ((FieldModelBase) this.fieldMap.get(field.getColumn())).setOrig(field.getOrig());
        }
    }

    @JsonIgnore
    public DatabaseDataMap getValueMap() {
        return this.valueMap;
    }

    @JsonIgnore
    public Map<String, FieldModelBase> getFieldMap() {
        return this.fieldMap;
    }

    public String idPrefix() {
        return "r";
    }

    @JsonIgnore
    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getWidgetType() {
        return "record";
    }

    public List<CalendarEvent> getCalendarEvents() {
        return this.calendarEvents;
    }

    public void setCalendarEvents(List<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }

    public String getRowColor() {
        return this.rowColor;
    }

    public void setRowColor(String rowColor) {
        this.rowColor = rowColor;
    }

    public String getColumnColor() {
        return this.columnColor;
    }

    public void setColumnColor(String columnColor) {
        this.columnColor = columnColor;
    }

    public String getRef____name_Expression() {
        return this.ref____name_Expression;
    }

    public void setRef____name_Expression(String ref____name_Expression) {
        this.ref____name_Expression = ref____name_Expression;
    }
}
