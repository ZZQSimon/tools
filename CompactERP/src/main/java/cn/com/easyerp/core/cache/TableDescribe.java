package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.core.approve.ApproveBlockEvent;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.cache.style.TableViewStyle;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;

public class TableDescribe implements Cloneable {
    public static final int TABLE_TYPE_TABLE = 0;
    public static final int TABLE_TYPE_VIEW = 1;
    public static final int TABLE_TYPE_API = 2;
    public static final int TABLE_TYPE_BIZ_PARAM = 3;
    private static TypeReference<Map<ActionType, TableActionCondition>> actionConditionMapJsonRef = new TypeReference<Map<ActionType, TableActionCondition>>() {
    };
    private static TypeReference<TableViewStyle> viewStyleJsonRef = new TypeReference<TableViewStyle>() {
    };
    private String id;
    private String id_column;
    private String parent_id_column;
    private String children_id_column;
    private String name_column;
    private String name_expression_publicity;
    private int readonly;
    private String parent_id;
    private String auto_gen_sql;
    private String group_name;
    private String block_view_style;
    private String default_group_column;
    private String order_by;
    private String check_condition;
    private boolean logable;
    private String view_style;
    private String record_read_only_cond;
    private int table_type;
    private int detail_disp_cols;
    private String valid_date_cols;
    private String valid_date_cond_disp;
    private String valid_date_group_cols;
    private String view_main_table;
    private String action_condition_json;
    private String export_sql;
    private String import_sql;
    private String is_import;
    private boolean importable;
    private int seq;
    private String international_id;
    private String memo;
    private int is_approve;
    private int is_auth;
    private int is_approve_state;
    private String key_formula;
    private String module;
    private String child_seq;
    private I18nDescribe i18n;
    private Map<String, Map<String, FlowEvent>> approveButtonEvent;
    private Map<String, Map<String, ApproveBlockEvent>> approveBlockEvent;
    private ApproveBlockEvent submitEvent;
    private List<ColumnDescribe> columns = null;
    private Map<String, ColumnDescribe> columnsCache;
    private String[] idColumns;
    private List<TableCheckRuleDescribe> checkRules;
    private Map<String, TriggerDescribe> triggers = null;
    private List<TriggerDescribe> listTriggers;
    private Map<String, List<String>> operations;
    private List<String> shortcuts = null;

    private List<TableTabDescribe> tabs;
    private Map<String, AutoGenTableDesc> autoGens;
    private Map<ActionType, TableActionCondition> action_condition;
    private List<TableRenderModel> renders;
    private List<ColumnDescribe> linkedColumns;
    private TableViewStyle viewStyle;
    private List<ColumnDescribe> complexColumns;
    private String[] rangeList;
    private List<OrderByDescribe> orderBy;
    private Map<String, List<TableApproveEventDescribe>> approveEvent;
    private String mobile_list_group;
    private int is_approve_select;

    public Map<String, List<TableApproveEventDescribe>> getApproveEvent() {
        return this.approveEvent;
    }

    public void setApproveEvent(Map<String, List<TableApproveEventDescribe>> approveEvent) {
        this.approveEvent = approveEvent;
    }

    public TableDescribe() {
        this.tabs = new ArrayList<>();
    }

    public TableDescribe(String id, String id_column, List<ColumnDescribe> columns) {
        this.id = id;
        this.id_column = id_column;
        this.columns = columns;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId_column() {
        return this.id_column;
    }

    public void addId_column(String id_column) {
        if (this.id_column != null && !"".equals(this.id_column)) {
            for (int i = 0; i < this.idColumns.length; i++) {
                if (this.idColumns[i].equals(id_column))
                    return;
            }
            this.id_column += "," + id_column;
            this.idColumns = this.id_column.split(",");
        } else {
            this.id_column = id_column;
            this.idColumns = new String[1];
            this.idColumns[0] = id_column;
        }
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getParent_id_column() {
        return this.parent_id_column;
    }

    public void setParent_id_column(String parent_id_column) {
        this.parent_id_column = parent_id_column;
    }

    public String getChildren_id_column() {
        return this.children_id_column;
    }

    public void setChildren_id_column(String children_id_column) {
        this.children_id_column = children_id_column;
    }

    public String getName_column() {
        return this.name_column;
    }

    public void setName_column(String name_column) {
        this.name_column = name_column;
    }

    public String getName_expression_publicity() {
        return this.name_expression_publicity;
    }

    public void setName_expression_publicity(String name_expression_publicity) {
        this.name_expression_publicity = name_expression_publicity;
    }

    public int getReadonly() {
        return this.readonly;
    }

    public void setReadonly(int readonly) {
        this.readonly = readonly;
    }

    public String getParent_id() {
        return this.parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getAuto_gen_sql() {
        return this.auto_gen_sql;
    }

    public void setAuto_gen_sql(String auto_gen_sql) {
        this.auto_gen_sql = auto_gen_sql;
    }

    public String getGroup_name() {
        return this.group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getOrder_by() {
        return this.order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public String getCheck_condition() {
        return this.check_condition;
    }

    public void setCheck_condition(String check_condition) {
        this.check_condition = check_condition;
    }

    public boolean getLogable() {
        return this.logable;
    }

    public void setLogable(boolean logable) {
        this.logable = logable;
    }

    public String getView_style() {
        return this.view_style;
    }

    public void setView_style(String view_style) {
        this.view_style = view_style;
        if (view_style != null) {
            this.viewStyle = (TableViewStyle) Common.fromJson(view_style, viewStyleJsonRef);
        }
    }

    public int getIs_approve() {
        return this.is_approve;
    }

    public void setIs_approve(int is_approve) {
        this.is_approve = is_approve;
    }

    public int getIs_auth() {
        return this.is_auth;
    }

    public void setIs_auth(int is_auth) {
        this.is_auth = is_auth;
    }

    public int getIs_approve_state() {
        return this.is_approve_state;
    }

    public void setIs_approve_state(int is_approve_state) {
        this.is_approve_state = is_approve_state;
    }

    public String getKey_formula() {
        return this.key_formula;
    }

    public void setKey_formula(String key_formula) {
        this.key_formula = key_formula;
    }

    public String getRecord_read_only_cond() {
        return this.record_read_only_cond;
    }

    public void setRecord_read_only_cond(String record_read_only_cond) {
        this.record_read_only_cond = record_read_only_cond;
    }

    public int getTable_type() {
        return this.table_type;
    }

    public void setTable_type(int table_type) {
        this.table_type = table_type;
    }

    public int getDetail_disp_cols() {
        return this.detail_disp_cols;
    }

    public void setDetail_disp_cols(int detail_disp_cols) {
        this.detail_disp_cols = detail_disp_cols;
    }

    public String getValid_date_cols() {
        return this.valid_date_cols;
    }

    public void setValid_date_cols(String valid_date_cols) {
        this.valid_date_cols = valid_date_cols;
        if (Common.notBlank(valid_date_cols)) {
            this.rangeList = Common.split(valid_date_cols, ",");
        }
    }

    @JsonIgnore
    public String[] getRangeList() {
        return this.rangeList;
    }

    public String getValid_date_cond_disp() {
        return this.valid_date_cond_disp;
    }

    public void setValid_date_cond_disp(String valid_date_cond_disp) {
        this.valid_date_cond_disp = valid_date_cond_disp;
    }

    public String getValid_date_group_cols() {
        return this.valid_date_group_cols;
    }

    public void setValid_date_group_cols(String valid_date_group_cols) {
        this.valid_date_group_cols = valid_date_group_cols;
    }

    public String getView_main_table() {
        return this.view_main_table;
    }

    public void setView_main_table(String view_main_table) {
        this.view_main_table = view_main_table;
    }

    public String getExport_sql() {
        return this.export_sql;
    }

    public void setExport_sql(String export_sql) {
        this.export_sql = Common.isBlank(export_sql) ? null : export_sql;
    }

    public String getImport_sql() {
        return this.import_sql;
    }

    public void setImport_sql(String import_sql) {
        this.import_sql = import_sql;
    }

    public boolean isImportable() {
        return this.importable;
    }

    public void setImportable(boolean importable) {
        this.importable = importable;
    }

    public int getSeq() {
        return this.seq;
    }

    public String getIs_import() {
        return this.is_import;
    }

    public void setIs_import(String is_import) {
        this.is_import = is_import;
    }

    public String getChild_seq() {
        return this.child_seq;
    }

    public void setChild_seq(String child_seq) {
        this.child_seq = child_seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDefault_group_column() {
        return this.default_group_column;
    }

    public void setDefault_group_column(String default_group_column) {
        this.default_group_column = default_group_column;
    }

    public String getBlock_view_style() {
        return this.block_view_style;
    }

    public void setBlock_view_style(String block_view_style) {
        this.block_view_style = block_view_style;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }

    public List<ColumnDescribe> getColumns() {
        return this.columns;
    }

    public void setColumns(List<ColumnDescribe> columns) {
        this.columns = columns;
    }

    public void addColumn(ColumnDescribe column) {
        if (this.columns == null) {
            this.columns = new ArrayList<>();
            this.columnsCache = new HashMap<>();
        }
        this.columns.add(column);
        this.columnsCache.put(column.getColumn_name(), column);

        String tab_name = column.getTab_name();
        if (tab_name == null)
            tab_name = "";
        TableTabDescribe tab = null;
        for (TableTabDescribe item : this.tabs) {
            if (item.getId().equals(tab_name)) {
                tab = item;
                break;
            }
        }
        if (tab == null) {
            tab = new TableTabDescribe(tab_name);
            this.tabs.add(tab);
        }
        String group_name = column.getGroup_name();
        if (group_name == null)
            group_name = "";
        TableGroupDescribe group = null;
        for (TableGroupDescribe item : tab.getGroups()) {
            if (item.getId().equals(group_name)) {
                group = item;
                break;
            }
        }
        if (group == null) {
            group = new TableGroupDescribe(group_name);
            tab.addGroup(group);
        }
        group.addColumn(column);
    }

    @JsonIgnore
    public ColumnDescribe getColumn(String columnName) {
        return (ColumnDescribe) this.columnsCache.get(columnName);
    }

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public boolean hasColumn(String columnName) {
        return this.columnsCache.containsKey(columnName);
    }

    public String[] getIdColumns() {
        return this.idColumns;
    }

    public List<TableCheckRuleDescribe> getCheckRules() {
        return this.checkRules;
    }

    public void setCheckRules(List<TableCheckRuleDescribe> checkRules) {
        this.checkRules = checkRules;
    }

    public Map<String, TriggerDescribe> getTriggers() {
        return this.triggers;
    }

    public void addTrigger(TriggerDescribe trigger) {
        if (this.triggers == null)
            this.triggers = new HashMap<>();
        this.triggers.put(trigger.getAction_id(), trigger);
        if (this.listTriggers == null)
            this.listTriggers = new ArrayList<>();
        this.listTriggers.add(trigger);
    }

    public TriggerDescribe getTrigger(TriggerDescribe.TriggerType type) {
        if (this.triggers == null) {
            return null;
        }
        for (Map.Entry<String, TriggerDescribe> entry : this.triggers.entrySet()) {
            TriggerDescribe trigger = (TriggerDescribe) entry.getValue();
            if (trigger.getSystem_type().equals(type.name())) {
                return trigger;
            }
        }
        return null;
    }

    public Map<String, List<String>> getOperations() {
        return this.operations;
    }

    public List<String> getShortcuts() {
        return this.shortcuts;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addOperation(String column, String op) {
        if (this.operations == null)
            this.operations = new HashMap<>();
        List<String> list = (List) this.operations.get(column);
        if (list == null) {
            list = new ArrayList<String>();
            this.operations.put(column, list);
        }
        list.add(op);
    }

    public void addShortcut(String ts) {
        if (this.shortcuts == null)
            this.shortcuts = new ArrayList<>();
        this.shortcuts.add(ts);
    }

    @JsonIgnore
    public List<TableTabDescribe> getTabs() {
        return this.tabs;
    }

    public boolean asView() {
        return "true".equals(this.record_read_only_cond);
    }

    public void addAutoGen(AutoGenTableDesc ag) {
        if (this.autoGens == null)
            this.autoGens = new HashMap<>();
        this.autoGens.put(ag.getId(), ag);
    }

    public Map<String, AutoGenTableDesc> getAutoGens() {
        return this.autoGens;
    }

    public AutoGenTableDesc getAutoGen(String id) {
        return (AutoGenTableDesc) this.autoGens.get(id);
    }

    @JsonIgnore
    public String getAction_condition_json() {
        return this.action_condition_json;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setAction_condition_json(String action_condition_json) {
        this.action_condition_json = action_condition_json;
        this.action_condition = (Map) Common.fromJson(action_condition_json, actionConditionMapJsonRef);
    }

    public Map<ActionType, TableActionCondition> getAction_condition() {
        return this.action_condition;
    }

    @JsonIgnore
    public Collection<String> getColumnNames() {
        return this.columnsCache.keySet();
    }

    public void addRender(TableRenderModel render) {
        if (this.renders == null)
            this.renders = new ArrayList<>();
        this.renders.add(render);
    }

    public List<TableRenderModel> getRenders() {
        return this.renders;
    }

    public List<ColumnDescribe> getLinkedColumns() {
        return this.linkedColumns;
    }

    public void addLinkedColumn(ColumnDescribe column) {
        if (this.linkedColumns == null)
            this.linkedColumns = new ArrayList<>();
        this.linkedColumns.add(column);
    }

    public TableViewStyle getViewStyle() {
        return this.viewStyle;
    }

    public void addComplexColumn(ColumnDescribe column) {
        if (this.complexColumns == null)
            this.complexColumns = new ArrayList<>();
        this.complexColumns.add(column);
    }

    public List<OrderByDescribe> getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(List<OrderByDescribe> orderBy) {
        this.orderBy = orderBy;
    }

    public List<ColumnDescribe> getComplexColumns() {
        return this.complexColumns;
    }

    public boolean hasComplexColumn() {
        return (this.complexColumns != null);
    }

    public List<TriggerDescribe> getListTriggers() {
        return this.listTriggers;
    }

    public void setListTriggers(List<TriggerDescribe> listTriggers) {
        this.listTriggers = listTriggers;
    }

    public Map<String, Map<String, FlowEvent>> getApproveButtonEvent() {
        return this.approveButtonEvent;
    }

    public void setApproveButtonEvent(Map<String, Map<String, FlowEvent>> approveButtonEvent) {
        this.approveButtonEvent = approveButtonEvent;
    }

    public String getMobile_list_group() {
        return this.mobile_list_group;
    }

    public void setMobile_list_group(String mobile_list_group) {
        this.mobile_list_group = mobile_list_group;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addTableApproveEventDescribe(TableApproveEventDescribe event) {
        if (event == null)
            return;
        try {
            event.setPaseParam(Common.paseEventParam(event.getEvent_param()));
        } catch (Exception e) {
        }

        if (event.getApprove_event_type() == 0) {
            if (this.approveEvent == null) {
                this.approveEvent = new HashMap<>();
            }
            if (this.approveEvent.get("pass") == null) {
                this.approveEvent.put("pass", new ArrayList<>());
            }
            ((List) this.approveEvent.get("pass")).add(event);
        } else if (event.getApprove_event_type() == 1) {
            if (this.approveEvent == null) {
                this.approveEvent = new HashMap<>();
            }
            if (this.approveEvent.get("reject") == null) {
                this.approveEvent.put("reject", new ArrayList<>());
            }
            ((List) this.approveEvent.get("reject")).add(event);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addButtonEvent(FlowEvent flowEvent) {
        if (flowEvent == null)
            return;
        if (flowEvent.getBlock_id() == null || flowEvent.getFlow_event_id() == null)
            return;
        if (this.approveButtonEvent == null)
            this.approveButtonEvent = new HashMap<>();
        if (this.approveButtonEvent.get(flowEvent.getBlock_id()) == null) {
            this.approveButtonEvent.put(flowEvent.getBlock_id(), new HashMap<>());
        }
        FlowEvent events = (FlowEvent) ((Map) this.approveButtonEvent.get(flowEvent.getBlock_id()))
                .get(flowEvent.getFlow_event_id());
        if (events == null)
            ((Map) this.approveButtonEvent.get(flowEvent.getBlock_id())).put(flowEvent.getFlow_event_id(), flowEvent);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void addApproveBlockEvent(ApproveBlockEvent blockEvent) {
        if (null == this.approveBlockEvent)
            this.approveBlockEvent = new HashMap<>();
        if (null == this.approveBlockEvent.get(blockEvent.getBlock_id()))
            this.approveBlockEvent.put(blockEvent.getBlock_id(), new HashMap<>());
        ((Map) this.approveBlockEvent.get(blockEvent.getBlock_id())).put(blockEvent.getEvent_type(), blockEvent);
    }

    public static class TableActionCondition {
        private String formula;
        private String message;

        public String getFormula() {
            return this.formula;
        }

        public void setFormula(String formula) {
            this.formula = formula;
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public Map<String, Map<String, ApproveBlockEvent>> getApproveBlockEvent() {
        return this.approveBlockEvent;
    }

    public void setApproveBlockEvent(Map<String, Map<String, ApproveBlockEvent>> approveBlockEvent) {
        this.approveBlockEvent = approveBlockEvent;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public ApproveBlockEvent getSubmitEvent() {
        return this.submitEvent;
    }

    public void setSubmitEvent(ApproveBlockEvent submitEvent) {
        this.submitEvent = submitEvent;
    }

    public int getIs_approve_select() {
        return this.is_approve_select;
    }

    public void setIs_approve_select(int is_approve_select) {
        this.is_approve_select = is_approve_select;
    }
}
