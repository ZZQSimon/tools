package cn.com.easyerp.core.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.framework.common.Common;

public class FilterModel extends WidgetModelBase {
    public static final String FILTER_BUTTON_LABEL = "Search";
    public static final String FILTER_BUTTON_ACTION = "exec";

    public static enum FilterMode {
        std, match;
    }

    public class ButtonDesc {
        private String action;
        private String label;

        public ButtonDesc(String action, String label) {
            this.action = action;
            this.label = label;
        }

        public String getAction() {
            return this.action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getLabel() {
            return this.label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    private Map<String, FilterDescribe> defaultFilters;
    private String table;
    private List<ButtonDesc> buttons;
    private List<ColumnDescribe> columns;
    private Map<String, FieldModelBase> functionalFields = new HashMap<>();

    private Map<String, Object> fixedData;

    private FilterMode mode;

    public FilterModel(String table) {
        this(table, "Search");
    }

    public FilterModel(String table, String buttonLabel) {
        this(table, buttonLabel, null);
    }

    public FilterModel(String table, String buttonLabel, Set<String> needed) {
        this(table, buttonLabel, needed, null);
    }

    public FilterModel(String table, String buttonLabel, Set<String> needed, Map<String, Object> fixedData) {
        this(table, buttonLabel, needed, fixedData, null);
    }

    public FilterModel(String table, String buttonLabel, Set<String> needed, Map<String, Object> fixedData,
            Map<String, FilterDescribe> defaultFilters) {
        this(table, buttonLabel, needed, fixedData, defaultFilters, FilterMode.std);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public FilterModel(String table, String buttonLabel, Set<String> needed, Map<String, Object> fixedData,
            Map<String, FilterDescribe> defaultFilters, FilterMode mode) {
        this.table = table;
        this.buttons = new ArrayList<>();
        this.buttons.add(new ButtonDesc("exec", buttonLabel));
        this.defaultFilters = defaultFilters;
        this.fixedData = fixedData;
        this.mode = mode;

        DataService dataService = Common.getDataService();
        TableDescribe tableDesc = dataService.getTableDesc(table);
        if (needed == null) {
            this.columns = tableDesc.getColumns();
        } else {
            this.columns = new ArrayList<>();
        }
        for (ColumnDescribe column : tableDesc.getColumns()) {
            if (needed != null) {
                if (!needed.contains(column.getColumn_name())) {
                    continue;
                }
                this.columns.add(column);
            }
            String columnName = column.getColumn_name();
            Object value = null;
            if (defaultFilters != null) {
                FilterDescribe desc = (FilterDescribe) defaultFilters.get(columnName);
                if (desc != null) {
                    value = desc.getValue();

                    if (column.getData_type() == 4)
                        if (desc.getType() == FilterDescribe.Type.eq) {
                            desc.setValue(ViewService.convertToDBValue(column, value));
                        } else {
                            desc.setFrom(ViewService.convertToDBValue(column, desc.getFrom()));
                            desc.setTo(ViewService.convertToDBValue(column, desc.getTo()));
                        }
                }
            }
            if (column.getRef_table_name() != null) {
                if (List.class.isInstance(value)) {
                    List<String> list = (List) value;
                    switch (list.size()) {
                    case 0:
                        value = null;
                        break;
                    case 1:
                        value = list.get(0);
                        break;
                    default:
                        value = dataService.getRefNames(tableDesc, list);
                        break;
                    }
                }
                this.functionalFields.put(columnName, new FieldWithRefModel(table, columnName, value, null));
                continue;
            }
            if (column.getFormat() != null) {
                this.functionalFields.put(columnName, new FieldModelBase(table, columnName, value));
                continue;
            }
            if (column.getDic_id() != null && mode == FilterMode.match) {
                this.functionalFields.put(columnName, new FieldModelBase(table, columnName, value));
            }
        }
    }

    public String getTable() {
        return this.table;
    }

    public FieldModelBase functionalField(String columnName) {
        return (FieldModelBase) this.functionalFields.get(columnName);
    }

    public Map<String, Object> getFixedData() {
        return this.fixedData;
    }

    public FilterDescribe getData(String column) {
        return (this.defaultFilters == null) ? null : (FilterDescribe) this.defaultFilters.get(column);
    }

    @JsonIgnore
    public List<ColumnDescribe> getColumns() {
        return this.columns;
    }

    public void setColumns(List<ColumnDescribe> columns) {
        this.columns = columns;
    }

    public FilterMode getMode() {
        return this.mode;
    }

    public void setMode(FilterMode mode) {
        this.mode = mode;
    }

    @JsonIgnore
    public String getType() {
        return "form";
    }

    public boolean hasReset() {
        return true;
    }

    public void addButton(String name, String label) {
        this.buttons.add(0, new ButtonDesc(name, label));
    }

    public List<ButtonDesc> getButtons() {
        return this.buttons;
    }

    public String idPrefix() {
        return "c";
    }

    public void setFixedData(Map<String, Object> fixedData) {
        this.fixedData = fixedData;
    }
}
