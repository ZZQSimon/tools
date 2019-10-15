 package cn.com.easyerp.DeployTool.service;
 import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.approve.ApproveBlockEvent;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.OrderByDescribe;
import cn.com.easyerp.core.cache.TableApproveEventDescribe;
import cn.com.easyerp.core.cache.TableCheckRuleDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TableRenderModel;
import cn.com.easyerp.core.cache.TableTabDescribe;
import cn.com.easyerp.core.cache.style.TableViewStyle;
import cn.com.easyerp.framework.enums.ActionType;
 
 public class AddTableDeploy {
   private int updateType;
   private int orderRuleChange;
   private int checkRuleChange;
   private int rendersChange;
   private int autoGenChange;
   private Map<String, TriggerDeploy> triggers = null; private Map<String, Map<String, FlowEvent>> approveBlockEvent; private ApproveBlockEvent submitEvent; private Map<String, ColumnDeploy> cRUDColumns; private Map<String, TriggerDeploy> deleteTrigger; private List<ColumnDeploy> deleteColumns;
   private List<TriggerDeploy> listTriggers;
   private List<ColumnDeploy> columns = null;
   
   private Map<String, ColumnDeploy> columnMap;
   private Map<String, AutoGenDeploy> autoGens;
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
   private boolean importable;
   private int seq;
   private String international_id;
   private String memo;
   private String is_import;
   private int is_approve;
   private int is_auth;
   private int is_approve_state;
   private String key_formula;
   private String mb_block_view_style;
   private String pc_block_view_style;
   private String level;
   private String type;
   private String module;
   private String child_seq;
   private I18nDescribe i18n;
   private String[] idColumns;
   private List<TableCheckRuleDescribe> checkRules;
   private Map<String, List<String>> operations;
   private List<String> shortcuts = null;
   
   private List<TableTabDescribe> tabs;
   
   private Map<ActionType, TableDescribe.TableActionCondition> action_condition;
   
   private List<TableRenderModel> renders;
   private List<ColumnDescribe> linkedColumns;
   private TableViewStyle viewStyle;
   private List<ColumnDescribe> complexColumns;
   private String[] rangeList;
   private List<OrderByDescribe> orderBy;
   private Map<String, List<TableApproveEventDescribe>> approveEvent;
   private String mobile_list_group;
   private Map<String, Map<String, FlowEvent>> approveButtonEvent;
   private int is_approve_select;
   private List<CustomLayout> customLayout;
   
   public List<CustomLayout> getCustomLayout() { return this.customLayout; }
 
 
   
   public void setCustomLayout(List<CustomLayout> customLayout) { this.customLayout = customLayout; }
 
 
   
   public Map<String, Map<String, FlowEvent>> getApproveButtonEvent() { return this.approveButtonEvent; }
 
 
   
   public void setApproveButtonEvent(Map<String, Map<String, FlowEvent>> approveButtonEvent) { this.approveButtonEvent = approveButtonEvent; }
 
 
   
   public Map<String, List<TableApproveEventDescribe>> getApproveEvent() { return this.approveEvent; }
 
 
   
   public void setApproveEvent(Map<String, List<TableApproveEventDescribe>> approveEvent) { this.approveEvent = approveEvent; }
 
 
   
   public ApproveBlockEvent getSubmitEvent() { return this.submitEvent; }
 
 
   
   public void setSubmitEvent(ApproveBlockEvent submitEvent) { this.submitEvent = submitEvent; }
 
 
   
   public List<ColumnDeploy> getDeleteColumns() { return this.deleteColumns; }
 
 
   
   public void setDeleteColumns(List<ColumnDeploy> deleteColumns) { this.deleteColumns = deleteColumns; }
 
 
   
   public int getUpdateType() { return this.updateType; }
 
 
   
   public void setUpdateType(int updateType) { this.updateType = updateType; }
 
 
   
   public int getOrderRuleChange() { return this.orderRuleChange; }
 
 
   
   public void setOrderRuleChange(int orderRuleChange) { this.orderRuleChange = orderRuleChange; }
 
 
   
   public int getCheckRuleChange() { return this.checkRuleChange; }
 
 
   
   public void setCheckRuleChange(int checkRuleChange) { this.checkRuleChange = checkRuleChange; }
 
 
   
   public int getRendersChange() { return this.rendersChange; }
 
 
   
   public void setRendersChange(int rendersChange) { this.rendersChange = rendersChange; }
 
 
   
   public int getAutoGenChange() { return this.autoGenChange; }
 
 
   
   public void setAutoGenChange(int autoGenChange) { this.autoGenChange = autoGenChange; }
 
 
   
   public Map<String, TriggerDeploy> getDeleteTrigger() { return this.deleteTrigger; }
 
 
   
   public void setDeleteTrigger(Map<String, TriggerDeploy> deleteTrigger) { this.deleteTrigger = deleteTrigger; }
 
 
   
   public Map<String, TriggerDeploy> getTriggers() { return this.triggers; }
 
 
   
   public void setTriggers(Map<String, TriggerDeploy> triggers) { this.triggers = triggers; }
 
 
   
   public List<ColumnDeploy> getColumns() { return this.columns; }
 
 
   
   public void setColumns(List<ColumnDeploy> columns) { this.columns = columns; }
 
 
   
   public Map<String, ColumnDeploy> getColumnMap() { return this.columnMap; }
 
 
   
   public void setColumnMap(Map<String, ColumnDeploy> columnMap) { this.columnMap = columnMap; }
 
 
 
 
   
   public Map<String, AutoGenDeploy> getAutoGens() { return this.autoGens; }
 
 
   
   public void setAutoGens(Map<String, AutoGenDeploy> autoGens) { this.autoGens = autoGens; }
 
 
   
   public String getId() { return this.id; }
 
 
   
   public void setId(String id) { this.id = id; }
 
 
   
   public String getId_column() { return this.id_column; }
 
 
   
   public void setId_column(String id_column) { this.id_column = id_column; }
 
 
   
   public String getParent_id_column() { return this.parent_id_column; }
 
 
   
   public void setParent_id_column(String parent_id_column) { this.parent_id_column = parent_id_column; }
 
 
   
   public String getChildren_id_column() { return this.children_id_column; }
 
 
   
   public void setChildren_id_column(String children_id_column) { this.children_id_column = children_id_column; }
 
 
   
   public String getName_column() { return this.name_column; }
 
 
   
   public void setName_column(String name_column) { this.name_column = name_column; }
 
 
   
   public String getName_expression_publicity() { return this.name_expression_publicity; }
 
 
   
   public void setName_expression_publicity(String name_expression_publicity) { this.name_expression_publicity = name_expression_publicity; }
 
 
   
   public int getReadonly() { return this.readonly; }
 
 
   
   public void setReadonly(int readonly) { this.readonly = readonly; }
 
 
   
   public String getParent_id() { return this.parent_id; }
 
 
   
   public void setParent_id(String parent_id) { this.parent_id = parent_id; }
 
 
   
   public String getAuto_gen_sql() { return this.auto_gen_sql; }
 
 
   
   public void setAuto_gen_sql(String auto_gen_sql) { this.auto_gen_sql = auto_gen_sql; }
 
 
   
   public String getGroup_name() { return this.group_name; }
 
 
   
   public void setGroup_name(String group_name) { this.group_name = group_name; }
 
 
   
   public String getBlock_view_style() { return this.block_view_style; }
 
 
   
   public void setBlock_view_style(String block_view_style) { this.block_view_style = block_view_style; }
 
 
   
   public String getDefault_group_column() { return this.default_group_column; }
 
 
   
   public void setDefault_group_column(String default_group_column) { this.default_group_column = default_group_column; }
 
 
   
   public String getOrder_by() { return this.order_by; }
 
 
   
   public void setOrder_by(String order_by) { this.order_by = order_by; }
 
 
   
   public String getCheck_condition() { return this.check_condition; }
 
 
   
   public void setCheck_condition(String check_condition) { this.check_condition = check_condition; }
 
 
   
   public boolean isLogable() { return this.logable; }
 
 
   
   public void setLogable(boolean logable) { this.logable = logable; }
 
 
   
   public String getView_style() { return this.view_style; }
 
 
   
   public void setView_style(String view_style) { this.view_style = view_style; }
 
 
   
   public String getRecord_read_only_cond() { return this.record_read_only_cond; }
 
 
   
   public void setRecord_read_only_cond(String record_read_only_cond) { this.record_read_only_cond = record_read_only_cond; }
 
 
   
   public int getTable_type() { return this.table_type; }
 
 
   
   public void setTable_type(int table_type) { this.table_type = table_type; }
 
 
   
   public int getDetail_disp_cols() { return this.detail_disp_cols; }
 
 
   
   public void setDetail_disp_cols(int detail_disp_cols) { this.detail_disp_cols = detail_disp_cols; }
 
 
   
   public String getValid_date_cols() { return this.valid_date_cols; }
 
 
   
   public void setValid_date_cols(String valid_date_cols) { this.valid_date_cols = valid_date_cols; }
 
 
   
   public String getValid_date_cond_disp() { return this.valid_date_cond_disp; }
 
 
   
   public void setValid_date_cond_disp(String valid_date_cond_disp) { this.valid_date_cond_disp = valid_date_cond_disp; }
 
 
   
   public String getValid_date_group_cols() { return this.valid_date_group_cols; }
 
 
   
   public void setValid_date_group_cols(String valid_date_group_cols) { this.valid_date_group_cols = valid_date_group_cols; }
 
 
   
   public String getView_main_table() { return this.view_main_table; }
 
 
   
   public void setView_main_table(String view_main_table) { this.view_main_table = view_main_table; }
 
 
   
   public String getAction_condition_json() { return this.action_condition_json; }
 
 
   
   public void setAction_condition_json(String action_condition_json) { this.action_condition_json = action_condition_json; }
 
 
   
   public String getExport_sql() { return this.export_sql; }
 
 
   
   public void setExport_sql(String export_sql) { this.export_sql = export_sql; }
 
 
   
   public String getImport_sql() { return this.import_sql; }
 
 
   
   public void setImport_sql(String import_sql) { this.import_sql = import_sql; }
 
 
   
   public boolean isImportable() { return this.importable; }
 
 
   
   public void setImportable(boolean importable) { this.importable = importable; }
 
 
   
   public int getSeq() { return this.seq; }
 
 
   
   public void setSeq(int seq) { this.seq = seq; }
 
 
   
   public String getInternational_id() { return this.international_id; }
 
 
   
   public void setInternational_id(String international_id) { this.international_id = international_id; }
 
 
   
   public String getMemo() { return this.memo; }
 
 
   
   public void setMemo(String memo) { this.memo = memo; }
 
 
   
   public int getIs_approve() { return this.is_approve; }
 
 
   
   public void setIs_approve(int is_approve) { this.is_approve = is_approve; }
 
 
   
   public int getIs_auth() { return this.is_auth; }
 
 
   
   public void setIs_auth(int is_auth) { this.is_auth = is_auth; }
 
 
   
   public int getIs_approve_state() { return this.is_approve_state; }
 
 
   
   public void setIs_approve_state(int is_approve_state) { this.is_approve_state = is_approve_state; }
 
 
   
   public String getKey_formula() { return this.key_formula; }
 
 
   
   public void setKey_formula(String key_formula) { this.key_formula = key_formula; }
 
 
   
   public String getMb_block_view_style() { return this.mb_block_view_style; }
 
 
   
   public void setMb_block_view_style(String mb_block_view_style) { this.mb_block_view_style = mb_block_view_style; }
 
 
   
   public String getPc_block_view_style() { return this.pc_block_view_style; }
 
 
   
   public void setPc_block_view_style(String pc_block_view_style) { this.pc_block_view_style = pc_block_view_style; }
 
 
   
   public String getLevel() { return this.level; }
 
 
   
   public void setLevel(String level) { this.level = level; }
 
 
   
   public String getType() { return this.type; }
 
 
   
   public void setType(String type) { this.type = type; }
 
 
   
   public String getModule() { return this.module; }
 
 
   
   public void setModule(String module) { this.module = module; }
 
 
   
   public String getChild_seq() { return this.child_seq; }
 
 
   
   public void setChild_seq(String child_seq) { this.child_seq = child_seq; }
 
 
   
   public String getIs_import() { return this.is_import; }
 
 
   
   public void setIs_import(String is_import) { this.is_import = is_import; }
 
 
   
   public I18nDescribe getI18n() { return this.i18n; }
 
 
   
   public void setI18n(I18nDescribe i18n) { this.i18n = i18n; }
 
 
   
   public String[] getIdColumns() { return this.idColumns; }
 
 
   
   public void setIdColumns(String[] idColumns) { this.idColumns = idColumns; }
 
 
   
   public List<TableCheckRuleDescribe> getCheckRules() { return this.checkRules; }
 
 
   
   public void setCheckRules(List<TableCheckRuleDescribe> checkRules) { this.checkRules = checkRules; }
 
 
   
   public Map<String, List<String>> getOperations() { return this.operations; }
 
 
   
   public void setOperations(Map<String, List<String>> operations) { this.operations = operations; }
 
 
   
   public List<String> getShortcuts() { return this.shortcuts; }
 
 
   
   public void setShortcuts(List<String> shortcuts) { this.shortcuts = shortcuts; }
 
 
   
   public List<TableTabDescribe> getTabs() { return this.tabs; }
 
 
   
   public void setTabs(List<TableTabDescribe> tabs) { this.tabs = tabs; }
 
 
   
   public Map<ActionType, TableDescribe.TableActionCondition> getAction_condition() { return this.action_condition; }
 
 
   
   public void setAction_condition(Map<ActionType, TableDescribe.TableActionCondition> action_condition) { this.action_condition = action_condition; }
 
 
   
   public List<TableRenderModel> getRenders() { return this.renders; }
 
 
   
   public void setRenders(List<TableRenderModel> renders) { this.renders = renders; }
 
 
   
   public List<ColumnDescribe> getLinkedColumns() { return this.linkedColumns; }
 
 
   
   public void setLinkedColumns(List<ColumnDescribe> linkedColumns) { this.linkedColumns = linkedColumns; }
 
 
   
   public TableViewStyle getViewStyle() { return this.viewStyle; }
 
 
   
   public void setViewStyle(TableViewStyle viewStyle) { this.viewStyle = viewStyle; }
 
 
   
   public List<ColumnDescribe> getComplexColumns() { return this.complexColumns; }
 
 
   
   public void setComplexColumns(List<ColumnDescribe> complexColumns) { this.complexColumns = complexColumns; }
 
 
   
   public String[] getRangeList() { return this.rangeList; }
 
 
   
   public void setRangeList(String[] rangeList) { this.rangeList = rangeList; }
 
 
   
   public List<OrderByDescribe> getOrderBy() { return this.orderBy; }
 
 
   
   public void setOrderBy(List<OrderByDescribe> orderBy) { this.orderBy = orderBy; }
 
 
   
   public Map<String, ColumnDeploy> getcRUDColumns() { return this.cRUDColumns; }
 
 
   
   public void setcRUDColumns(Map<String, ColumnDeploy> cRUDColumns) { this.cRUDColumns = cRUDColumns; }
 
 
   
   public List<TriggerDeploy> getListTriggers() { return this.listTriggers; }
 
 
   
   public void setListTriggers(List<TriggerDeploy> listTriggers) { this.listTriggers = listTriggers; }
 
 
 
   
   public Map<String, Map<String, FlowEvent>> getApproveBlockEvent() { return this.approveBlockEvent; }
 
 
   
   public void setApproveBlockEvent(Map<String, Map<String, FlowEvent>> approveBlockEvent) { this.approveBlockEvent = approveBlockEvent; }
 
 
   
   public String getMobile_list_group() { return this.mobile_list_group; }
 
 
   
   public void setMobile_list_group(String mobile_list_group) { this.mobile_list_group = mobile_list_group; }
 
 
   
   public int getIs_approve_select() { return this.is_approve_select; }
 
 
   
   public void setIs_approve_select(int is_approve_select) { this.is_approve_select = is_approve_select; }
 }


