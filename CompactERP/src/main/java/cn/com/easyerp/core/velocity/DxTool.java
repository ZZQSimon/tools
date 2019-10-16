package cn.com.easyerp.core.velocity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.config.DefaultKey;

import cn.com.easyerp.DeployTool.service.DictNameDescribe;
import cn.com.easyerp.DeployTool.view.ImportDeployModel;
import cn.com.easyerp.auth.AuthControlDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.batch.BatchFormModel;
import cn.com.easyerp.core.ApplicationContextProvider;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.approve.ApproveBlockEventExec;
import cn.com.easyerp.core.approve.ApproveFlow;
import cn.com.easyerp.core.approve.ApproveFlowNode;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.OperationDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TableGroupDescribe;
import cn.com.easyerp.core.cache.TableShortcutDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.MapFieldModel;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
import cn.com.easyerp.operation.OperationFormModel;

@DefaultKey("dx")
public class DxTool {
    private DxToolService service;
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private boolean jsDebug;
    private String errorMsg;
    private SimpleDateFormat formatterMint = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void init(Object data) throws IOException {
        this.service = (DxToolService) ApplicationContextProvider.getBean("dxToolService");
        this.service.setTool(this);
        this.jsDebug = "true".equals(System.getProperty("DXDEBUG"));
    }

    public String getErrorMsg() {
        CacheService cacheService = (CacheService) ApplicationContextProvider.getBean("cacheService");
        try {
            cacheService.get();
            this.errorMsg = null;
        } catch (Exception e) {
            this.errorMsg = e.toString();
        }
        return this.errorMsg;
    }

    public String json(Object value) {
        return Common.toJson(value);
    }

    public String label(ColumnDescribe column) {
        String label = this.service.dataService.getLabel(column);
        if (label == null) {
            String column_name = column.getColumn_name();
            column_name = column_name.replace("<", "&lt;");
            column_name = column_name.replace(">", "&gt;");
            column_name = column_name.replace("\"", "&quot;");
            return column_name.replace("'", "&#39;");
        }

        label = label.replace("<", "&lt;");
        label = label.replace(">", "&gt;");
        label = label.replace("\"", "&quot;");
        return label.replace("'", "&#39;");
    }

    public String label(TableDescribe table) {
        String label = this.service.dataService.getLabel(table);
        if (label == null) {
            String tableId = table.getId();
            tableId = tableId.replace("<", "&lt;");
            tableId = tableId.replace(">", "&gt;");
            tableId = tableId.replace("\"", "&quot;");
            return tableId.replace("'", "&#39;");
        }

        label = label.replace("<", "&lt;");
        label = label.replace(">", "&gt;");
        label = label.replace("\"", "&quot;");
        return label.replace("'", "&#39;");
    }

    public String label(FieldModelBase field) {
        String label = this.service.dataService.getLabel(field);
        if (label == null)
            return "";
        return label;
    }

    public String label(GridModel grid) {
        String label = this.service.dataService.getLabel(grid);
        if (label == null)
            return "";
        return label;
    }

    public String label(String tableName) {
        String str = this.service.dataService.getTableLabel(tableName);
        if (str == null)
            return tableName;
        return str;
    }

    public String label(AuthDetails.UserMenu menu) {
        String menuLabel = this.service.dataService.getMenuLabel(menu.getInternational_id());
        if (menuLabel == null) {
            if (menu.getId() == null) {
                return "";
            }
            return menu.getId();
        }
        menuLabel = menuLabel.replace("<", "&lt;");
        menuLabel = menuLabel.replace(">", "&gt;");
        menuLabel = menuLabel.replace("\"", "&quot;");
        return menuLabel.replace("'", "&#39;");
    }

    public String label(MenuModel menu) {
        String menuLabel = this.service.dataService.getMenuLabel(menu.getId());
        if (menuLabel == null)
            return menu.getId();
        return menuLabel;
    }

    public String label(OperationDescribe action) {
        String label = this.service.dataService.getLabel(action);
        if (label == null)
            return action.getId();
        return label;
    }

    public String label(TableShortcutDescribe shortcut) {
        return this.service.dataService.getLabel(shortcut);
    }

    public String label(AuthControlDescribe control) {
        String label = this.service.dataService.getLabel(control);
        if (label == null)
            return "";
        return label;
    }

    public String tableLabel(String tableName) {
        String tableLabel = this.service.dataService.getTableLabel(tableName);
        if (tableLabel == null) {
            tableName = tableName.replace("<", "&lt;");
            tableName = tableName.replace(">", "&gt;");
            tableName = tableName.replace("\"", "&quot;");
            return tableName.replace("'", "&#39;");
        }

        tableLabel = tableLabel.replace("<", "&lt;");
        tableLabel = tableLabel.replace(">", "&gt;");
        tableLabel = tableLabel.replace("\"", "&quot;");
        return tableLabel.replace("'", "&#39;");
    }

    public String columnLabel(String tableName, String columnName) {
        String columnLabel = this.service.dataService.getColumnLabel(tableName, columnName);
        if (columnLabel == null)
            return "";
        columnLabel = columnLabel.replace("<", "&lt;");
        columnLabel = columnLabel.replace(">", "&gt;");
        columnLabel = columnLabel.replace("\"", "&quot;");
        return columnLabel.replace("'", "&#39;");
    }

    public String opLabel(String id) {
        String operationLabel = this.service.dataService.getOperationLabel(id);
        if (operationLabel == null) {
            return "";
        }
        operationLabel = operationLabel.replace("<", "&lt;");
        operationLabel = operationLabel.replace(">", "&gt;");
        operationLabel = operationLabel.replace("\"", "&quot;");
        return operationLabel.replace("'", "&#39;");
    }

    public String tab(String table, String tab) {
        String ret = this.service.dataService.getTabText(table, tab);
        if (ret == null) {
            String error = String.format("table \"%s\" has no tab with name \"%s\"", new Object[] { table, tab });
            this.service.logger.error(error);
            throw new ApplicationException(error);
        }
        ret = ret.replace("<", "&lt;");
        ret = ret.replace(">", "&gt;");
        ret = ret.replace("\"", "&quot;");
        return ret.replace("'", "&#39;");
    }

    public String group(String table, String group) {
        String ret = this.service.dataService.getGroupText(table, group);
        String str = (ret == null) ? group : ret;
        str = str.replace("<", "&lt;");
        str = str.replace(">", "&gt;");
        str = str.replace("\"", "&quot;");
        return str.replace("'", "&#39;");
    }

    public Map<String, I18nDescribe> dict(String key1) {
        return this.service.dataService.getDict(key1);
    }

    public List<DictNameDescribe> getDicts() {
        Map<String, Map<String, I18nDescribe>> dicts = this.service.dataService.getDicts();
        List<DictNameDescribe> dictList = new ArrayList<DictNameDescribe>();
        for (Map.Entry<String, Map<String, I18nDescribe>> entry : dicts.entrySet()) {
            String dictKey = (String) entry.getKey();
            DictNameDescribe dict = new DictNameDescribe();
            dict.setDictKey(dictKey);
            dict.setDictText(this.service.dataService.i18nString(this.service.dataService.getDictNameI18N(dictKey)));
            dictList.add(dict);
        }
        return dictList;
    }

    public List<DictNameDescribe> getDict(String key) {
        Map<String, I18nDescribe> dict = dict(key);
        List<DictNameDescribe> dictList = new ArrayList<DictNameDescribe>();
        if (dict != null)
            for (Map.Entry<String, I18nDescribe> entry : dict.entrySet()) {
                String dictKey = (String) entry.getKey();
                DictNameDescribe oneDict = new DictNameDescribe();
                oneDict.setDictKey(dictKey);
                oneDict.setDictText(this.service.dataService.i18nString((I18nDescribe) entry.getValue()));
                dictList.add(oneDict);
            }
        return dictList;
    }

    public List<DictNameDescribe> getReports() {
        List<DictNameDescribe> dictList = new ArrayList<DictNameDescribe>();
        Map<String, I18nDescribe> reports = this.service.dataService.getReports();
        for (Map.Entry<String, I18nDescribe> entry : reports.entrySet()) {
            DictNameDescribe oneDict = new DictNameDescribe();
            oneDict.setDictKey((String) entry.getKey());
            oneDict.setDictText((this.service.dataService.i18nString((I18nDescribe) entry.getValue()) == null) ? ""
                    : this.service.dataService.i18nString((I18nDescribe) entry.getValue()));
            dictList.add(oneDict);
        }
        DictNameDescribe oneDict = new DictNameDescribe();
        oneDict.setDictKey("");
        oneDict.setDictText("");
        dictList.add(oneDict);
        return dictList;
    }

    public List<DictNameDescribe> getTables() {
        Map<String, TableDescribe> tablesDesc = this.service.dataService.getTablesDesc();
        List<DictNameDescribe> dictList = new ArrayList<DictNameDescribe>();
        for (Map.Entry<String, TableDescribe> entry : tablesDesc.entrySet()) {
            String dictKey = (String) entry.getKey();
            DictNameDescribe dict = new DictNameDescribe();
            dict.setDictKey(dictKey);
            String tableI18NName = this.service.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n());
            dict.setDictText((tableI18NName == null) ? "" : tableI18NName);
            dictList.add(dict);
        }
        return dictList;
    }

    public List<DictNameDescribe> getTables(boolean isBusinessTable) {
        if (isBusinessTable) {
            Map<String, TableDescribe> tablesDesc = this.service.dataService.getTablesDesc();
            List<DictNameDescribe> dictList = new ArrayList<DictNameDescribe>();
            for (Map.Entry<String, TableDescribe> entry : tablesDesc.entrySet()) {
                TableDescribe table = (TableDescribe) entry.getValue();
                if (table.getTable_type() == 4) {
                    String dictKey = (String) entry.getKey();
                    DictNameDescribe dict = new DictNameDescribe();
                    dict.setDictKey(dictKey);
                    String tableI18NName = this.service.dataService
                            .i18nString(((TableDescribe) entry.getValue()).getI18n());
                    dict.setDictText((tableI18NName == null) ? "" : tableI18NName);
                    dictList.add(dict);
                }
            }
            return dictList;
        }
        return null;
    }

    public String text(FieldWithRefModel field) {
        String name = this.service.viewService.name(field);
        if (name == null)
            return "";
        name = name.replace("<", "&lt;");
        name = name.replace(">", "&gt;");
        name = name.replace("\"", "&quot;");
        return name.replace("'", "&#39;");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String text(MapFieldModel field) {
        Map<String, String> selection = field.getSelection();
        if (selection.size() == 0)
            return msg("w.map.noneSelected", new String[0]);
        Map.Entry<String, String> entry = (Map.Entry) selection.entrySet().iterator().next();

        return msg("w.map.selected", new String[] { String.valueOf(selection.size()),
                Common.notBlank((String) entry.getValue()) ? (String) entry.getValue() : (String) entry.getKey() });
    }

    public AuthDetails getUser() {
        return AuthService.getCurrentUser();
    }

    public String dictText(String key1, String key2) {
        String text = this.service.dataService.getDictText(key1, key2);
        String str = (text == null) ? key2 : text;
        str = str.replace("<", "&lt;");
        str = str.replace(">", "&gt;");
        str = str.replace("\"", "&quot;");
        return str.replace("'", "&#39;");
    }

    public String dictText(String key1, int key2) {
        String s = String.valueOf(key2);
        String text = this.service.dataService.getDictText(key1, s);
        String str = (text == null) ? s : text;
        str = str.replace("<", "&lt;");
        str = str.replace(">", "&gt;");
        str = str.replace("\"", "&quot;");
        return str.replace("'", "&#39;");
    }

    public String dictText(FieldModelBase field) {
        if (field.getValue() == null)
            return "";
        ColumnDescribe desc = this.service.dataService.getColumnDesc(field);
        return dictText(desc.getDic_id(), field.getValue().toString());
    }

    public String msg(String code, String... param) {
        try {
            String text = this.service.dataService.getMessageText(code, (Object[]) param);
            text = text.replace("<", "&lt;");
            text = text.replace(">", "&gt;");
            text = text.replace("\"", "&quot;");
            text = text.replace("'", "&#39;");
            assert text != null;
            return text;
        } catch (Exception e) {

            return "";
        }
    }

    public String i18n(I18nDescribe desc) {
        String text = this.service.dataService.i18nString(desc);
        if (text == null) {
            return "";
        }
        return text;
    }

    public List<ColumnDescribe> columns(String tableName) {
        return this.service.dataService.getTableDesc(tableName).getColumns();
    }

    public String max(ColumnDescribe desc) {
        if (desc.getMax_len() == null) {
            return null;
        }

        return StringUtils.leftPad("", desc.getMax_len().intValue(), "9");
    }

    public String value(FieldModelBase field) {
        String text = ViewService.text(desc(field), field.getValue());
        ColumnDescribe column = desc(field);
        if (column != null && column.getData_type() == 15) {
            text = text.replace("&lt;", "<");
            text = text.replace("&gt;", ">");
            text = text.replace("&quot;", "\"");
            return text.replace("&#39;", "'");
        }

        return text;
    }

    public String filterTo(ColumnDescribe desc, FilterModel filters) {
        FilterDescribe filter = filters.getData(desc.getColumn_name());
        if (filter == null)
            return "";
        return ViewService.text(desc, filter.getTo());
    }

    public String filterFrom(ColumnDescribe desc, FilterModel filters) {
        FilterDescribe filter = filters.getData(desc.getColumn_name());
        if (filter == null)
            return "";
        return ViewService.text(desc, filter.getFrom());
    }

    public TableDescribe table(String tableName) {
        return this.service.dataService.getTableDesc(tableName);
    }

    public ColumnDescribe desc(FieldModelBase field) {
        return this.service.dataService.getColumnDesc(field);
    }

    public boolean hasCalendarEvent(FieldModelBase field) {
        ColumnDescribe columnDesc = this.service.dataService.getColumnDesc(field);
        if (columnDesc == null)
            return false;
        if (columnDesc.getIsCalendarEvent() == 1)
            return true;
        return false;
    }

    public ColumnDescribe desc(String table, String column) {
        return this.service.dataService.getColumnDesc(table, column);
    }

    public boolean visible(TableGroupDescribe group) {
        return (!Common.isBlank(group.getId()) && !DataService.isSystemGroup(group.getId()));
    }

    public boolean visible(FormModelBase form, ColumnDescribe desc) {
        if (desc.isHidden()) {
            return false;
        }
        String columnName = desc.getColumn_name();
        if (DetailFormModel.class.isInstance(form)) {
            DetailFormModel detailForm = (DetailFormModel) form;
            Set<String> noAuthColumns = detailForm.getNoAuthColumns();
            if (noAuthColumns != null && noAuthColumns.contains(columnName)) {
                return false;
            }
        }
        if (DataService.isSystemGroup(desc.getGroup_name())) {
            return false;
        }
        TableDescribe table = this.service.dataService.getTableDesc(desc.getTable_id());
        ApiDescribe api = null;
        if (OperationFormModel.class.isInstance(form)) {
            OperationFormModel opForm = (OperationFormModel) form;
            api = (ApiDescribe) this.service.dataService.getTrigger(opForm.getOperationId()).getApi()
                    .get(opForm.getSeq());
        } else if (BatchFormModel.class.isInstance(form)) {
            api = this.service.dataService.getBatch(((BatchFormModel) form).getBatchId()).getApi();
        }
        if (api != null) {
            return !api.defined(columnName);
        }
        String lastKey = table.getIdColumns()[table.getIdColumns().length - 1];
        if (table.getViewStyle() != null && table.getViewStyle().getSeq() != null
                && table.getViewStyle().getSeq().equals(columnName)) {
            return false;
        }

        if (columnName.equals(lastKey)) {

            if (desc.getData_type() == 13 && form.getAction() == ActionType.create) {
                return false;
            }
        } else if (table.getParent_id() != null && table.getId_column().contains("" + columnName + "")) {
            return false;
        }
        return true;
    }

    public List<String> ds() {
        return this.service.dxRoutingDataSource.getList();
    }

    @SuppressWarnings({ "rawtypes" })
    public boolean empty(Object data) {
        if (data == null)
            return true;
        if (String.class.isInstance(data))
            return Common.isBlank((String) data);
        if (List.class.isInstance(data))
            return ((List) data).isEmpty();
        if (Map.class.isInstance(data))
            return ((Map) data).isEmpty();
        return false;
    }

    public List<String> children(TableDescribe table) {
        return this.service.dataService.getChildrenTable(table.getId());
    }

    public OperationDescribe operation(String oid) {
        return this.service.dataService.getOperation(oid);
    }

    public TableShortcutDescribe ts(String sid) {
        return this.service.dataService.getTableShortcut(sid);
    }

    public String[] split(String str, String splitter) {
        return Common.split(str, splitter);
    }

    public Set<String> getJs() throws IOException {
        return this.service.jsList;
    }

    public Set<String> getCss() throws IOException {
        return this.service.cssList;
    }

    public boolean auth(AuthDetails user, TableShortcutDescribe ts) {
        return this.service.authService.newAuth(user, ts.getId());
    }

    public boolean auth(AuthDetails user, TableDescribe table) {
        return true;
    }

    public boolean exportAuth(AuthDetails user, String table, boolean isSelected) {
        return this.service.authService.exportAuth(user, table, null, isSelected);
    }

    public boolean importAuth(AuthDetails user, String table, boolean isInsert) {
        return this.service.authService.importAuth(user, table, null, isInsert);
    }

    public boolean addAuth(AuthDetails user, TableDescribe table, String actionId) {
        Map<String, TriggerDescribe> triggers = table.getTriggers();
        if (table.getIs_auth() != 1) {
            return true;
        }
        if (triggers == null || triggers.get(actionId) == null
                || ((TriggerDescribe) triggers.get(actionId)).getIs_using() != 1)
            return false;
        try {
            List<AuthGroup> optionAuth = getAddAuth(table.getId(), actionId);
            if (optionAuth == null || optionAuth.size() == 0) {
                return false;
            }
            for (int i = 0; i < optionAuth.size(); i++) {
                String department_relation = ((AuthGroup) optionAuth.get(i)).getDepartment_relation();
                if (department_relation != null && !"".equals(department_relation)) {
                    return true;
                }
                String user_relation = ((AuthGroup) optionAuth.get(i)).getUser_relation();
                if (user_relation != null && !"".equals(user_relation)) {
                    return true;
                }
            }
            this.service.authService.assertAuth(user, table.getId(), actionId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean deleteAuth(AuthDetails user, TableDescribe table, String actionId) {
        if (table.getIs_auth() != 1) {
            return true;
        }
        Map<String, TriggerDescribe> triggers = table.getTriggers();
        if (triggers == null || triggers.get(actionId) == null
                || ((TriggerDescribe) triggers.get(actionId)).getIs_using() != 1)
            return false;
        try {
            List<AuthGroup> optionAuth = getDeleteAuth(table.getId(), actionId);
            if (optionAuth == null || optionAuth.size() == 0) {
                return false;
            }
            for (int i = 0; i < optionAuth.size(); i++) {
                String department_relation = ((AuthGroup) optionAuth.get(i)).getDepartment_relation();
                if (department_relation != null && !"".equals(department_relation)) {
                    return true;
                }
                String user_relation = ((AuthGroup) optionAuth.get(i)).getUser_relation();
                if (user_relation != null && !"".equals(user_relation)) {
                    return true;
                }
            }
            this.service.authService.assertAuth(user, table.getId(), actionId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean operateAuth(AuthDetails user, TableDescribe table, String actionId) {
        Map<String, TriggerDescribe> triggers = table.getTriggers();
        if (triggers == null || triggers.get(actionId) == null
                || ((TriggerDescribe) triggers.get(actionId)).getIs_using() != 1)
            return false;
        try {
            List<AuthGroup> optionAuth = getOperationAuth(table.getId(), actionId);
            if (optionAuth == null || optionAuth.size() == 0) {
                return true;
            }
            for (int i = 0; i < optionAuth.size(); i++) {
                String department_relation = ((AuthGroup) optionAuth.get(i)).getDepartment_relation();
                if (department_relation != null && !"".equals(department_relation)) {
                    return true;
                }
                String user_relation = ((AuthGroup) optionAuth.get(i)).getUser_relation();
                if (user_relation != null && !"".equals(user_relation)) {
                    return true;
                }
            }
            this.service.authService.assertAuth(user, table.getId(), actionId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private List<AuthGroup> getOperationAuth(String tableName, String actionId) {
        List<AuthGroup> optionAuth = this.service.cacheService.getOptionAuth(tableName);
        if (optionAuth == null || optionAuth.size() == 0) {
            return null;
        }
        List<AuthGroup> resultOptionAuth = new ArrayList<AuthGroup>();
        for (int i = 0; i < optionAuth.size(); i++) {
            String operate = ((AuthGroup) optionAuth.get(i)).getOperate();
            if (operate != null) {

                int i1 = operate.indexOf(actionId);
                if (i1 != -1)
                    resultOptionAuth.add(optionAuth.get(i));
            }
        }
        return resultOptionAuth;
    }

    private List<AuthGroup> getAddAuth(String tableName, String actionId) {
        List<AuthGroup> optionAuth = this.service.cacheService.getOptionAuth(tableName);
        if (optionAuth == null || optionAuth.size() == 0) {
            return null;
        }
        List<AuthGroup> resultOptionAuth = new ArrayList<AuthGroup>();
        for (int i = 0; i < optionAuth.size(); i++) {
            String add = ((AuthGroup) optionAuth.get(i)).getCreate();
            if (add != null) {
                if (add.equals(actionId))
                    resultOptionAuth.add(optionAuth.get(i));
            }
        }
        return resultOptionAuth;
    }

    private List<AuthGroup> getDeleteAuth(String tableName, String actionId) {
        List<AuthGroup> optionAuth = this.service.cacheService.getOptionAuth(tableName);
        if (optionAuth == null || optionAuth.size() == 0) {
            return null;
        }
        List<AuthGroup> resultOptionAuth = new ArrayList<AuthGroup>();
        for (int i = 0; i < optionAuth.size(); i++) {
            String delete = ((AuthGroup) optionAuth.get(i)).getDelete();
            if (delete != null) {
                if (delete.equals(actionId))
                    resultOptionAuth.add(optionAuth.get(i));
            }
        }
        return resultOptionAuth;
    }

    public boolean editAuth(AuthDetails user, TableDescribe table, Map<String, FieldModelBase> fieldMap) {
        if (table.getTriggers() == null || null == table.getTrigger(TriggerDescribe.TriggerType.edit))
            return true;
        if (table.getTrigger(TriggerDescribe.TriggerType.edit).getIs_using() != 1)
            return false;
        try {
            Object owner = null;
            if (null != fieldMap.get("owner")) {
                owner = ((FieldModelBase) fieldMap.get("owner")).getValue();
            } else if (null != fieldMap.get("cre_user")) {
                owner = ((FieldModelBase) fieldMap.get("cre_user")).getValue();
            }
            this.service.authService.assertAuth(user, table.getId(),
                    table.getTrigger(TriggerDescribe.TriggerType.edit).getAction_id(), owner);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean operationButtonAuth(Map<String, FieldModelBase> fieldMap, AuthDetails user, TableDescribe table,
            String operation) {
        String owner = "";
        if (fieldMap.get("owner") == null) {
            if (fieldMap.get("cre_user") == null) {
                owner = null;
            } else if (null == ((FieldModelBase) fieldMap.get("cre_user")).getValue()) {
                owner = null;
            } else {
                owner = ((FieldModelBase) fieldMap.get("cre_user")).getValue().toString();
            }
        } else if (null == ((FieldModelBase) fieldMap.get("owner")).getValue()) {
            owner = null;
        } else {
            owner = ((FieldModelBase) fieldMap.get("owner")).getValue().toString();
        }

        if (owner == null)
            return false;
        if (!this.service.authService.optionAuth(user, table.getId(), operation, owner)) {
            return false;
        }
        return true;
    }

    public FieldModelBase field(String id) {
        return (FieldModelBase) ViewService.fetchWidgetModel(id);
    }

    public boolean importable(TableDescribe table) {
        for (ColumnDescribe column : table.getColumns()) {
            if (!Common.isBlank(column.getComplex_id()))
                return false;
        }
        return table.isImportable();
    }

    public boolean hasName(FieldWithRefModel field) {
        ColumnDescribe column = this.service.dataService.getColumnDesc(field);
        TableDescribe table = this.service.dataService.getTableDesc(column.getRef_table_name());
        return (table.getName_column() != null);
    }

    public boolean required(ColumnDescribe column) {
        boolean flag = (column.getMin_len() == null || column.getMin_len().intValue() > 0);
        return flag;
    }

    public boolean isJsDebug() {
        return this.jsDebug;
    }

    public void cacheFormModel(FormModelBase form) {
        this.service.cacheFormModel(form);
    }

    public boolean filter(TableDescribe table, ColumnDescribe desc) {
        return (!desc.isVirtual() && desc.getIs_condition().intValue() >= 0 && desc.getData_type() != 6
                && (table.getRangeList() == null || !table.getRangeList()[1].equals(desc.getColumn_name())));
    }

    public boolean hasFilter(TableDescribe table) {
        List<ColumnDescribe> columns = table.getColumns();
        for (ColumnDescribe column : columns) {
            if (!column.isVirtual() && column.getIs_condition().intValue() == 1 && column.getData_type() != 6
                    && (table.getRangeList() == null || !table.getRangeList()[1].equals(column.getColumn_name())))
                return true;
        }
        return false;
    }

    public boolean hasLeftGroup(TableDescribe table) {
        if (table == null || table.getColumns() == null || table.getColumns().size() == 0)
            return false;
        for (ColumnDescribe column : table.getColumns()) {
            if (column.getDic_id() != null && !"".equals(column.getDic_id()))
                return true;
            if (column.getRef_table_name() != null && !"".equals(column.getRef_table_name())) {
                return true;
            }
        }
        return false;
    }

    public boolean isMap(Object o) {
        return Map.class.isInstance(o);
    }

    public boolean isMapColumn(FieldModelBase field) {
        return this.service.dataService.isMapColumn(desc(field));
    }

    public Object formatEmpty(Object value) {
        if (value == null) {
            return "";
        }
        String textValue = value.toString();
        textValue = textValue.replace("<", "&lt;");
        textValue = textValue.replace(">", "&gt;");
        textValue = textValue.replace("\"", "&quot;");
        return textValue.replace("'", "&#39;");
    }

    public String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return this.formatter.format(date);
    }

    public String formatDateHasMin(Date date) {
        if (date == null) {
            return "";
        }
        return this.formatterMint.format(date);
    }

    public boolean selectIsAddApprove(String approve_id, String block_id) {
        return this.service.dataService.selectIsAddApprove(approve_id, block_id);
    }

    public String substringAfterLast(String filePath) {
        if (filePath == null)
            return "";
        String fileName = StringUtils.substringAfterLast(filePath, "/");
        if (fileName == null)
            return "";
        return fileName;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<FlowEvent> approveButton(String tableName, String blockId, String belongBlock) {
        Map<String, FlowEvent> flowEventMap;
        TableDescribe tableDesc = this.service.dataService.getTableDesc(tableName);
        if (null == tableDesc || null == tableDesc.getApproveButtonEvent()) {
            return null;
        }
        if (null != belongBlock && !"".equals(belongBlock)) {
            flowEventMap = (Map) tableDesc.getApproveButtonEvent().get(belongBlock);
        } else {
            flowEventMap = (Map) tableDesc.getApproveButtonEvent().get(blockId);
        }
        List<FlowEvent> flowEvents = new ArrayList<FlowEvent>();
        if (null != flowEventMap) {
            flowEvents = new ArrayList<FlowEvent>(flowEventMap.values());
        }
        Collections.sort(flowEvents);
        return flowEvents;
    }

    public List<FlowEvent> approveButton(String tableName, ApproveFlowNode node) {
        return approveButton(tableName, node.getBlock_id(), node.getBelong_block());
    }

    @SuppressWarnings({ "rawtypes" })
    public boolean approveBlockIsExec(ApproveFlow approveFlow, String block_id, String flow_event_id) {
        if (null == approveFlow.getBlockEventIsExec() || null == approveFlow.getBlockEventIsExec().get(block_id))
            return false;
        ApproveBlockEventExec approveBlockEventExec = (ApproveBlockEventExec) ((Map) approveFlow.getBlockEventIsExec()
                .get(block_id)).get(flow_event_id);
        if (null == approveBlockEventExec)
            return false;
        if (approveBlockEventExec.getIs_exec() == 1)
            return true;
        return false;
    }

    public String approveState(String flow_event_id) {
        FlowEvent flowEvent = this.service.dataService.getFlowEvent(flow_event_id);
        if (null == flowEvent)
            return "";
        String i18n = i18n(flowEvent.getI18n());
        if (null == i18n)
            return "";
        return i18n;
    }

    public String approveStateColor(String flow_event_id) {
        FlowEvent flowEvent = this.service.dataService.getFlowEvent(flow_event_id);
        if (null == flowEvent)
            return "";
        String color = flowEvent.getColor();
        if (null == color)
            return "";
        return color;
    }

    public boolean ifAllowUndo(String approveId) {
        return (this.service.ifAllow(approveId) == 0);
    }

    public boolean isImportInsert(String table) {
        ImportDeployModel idm = this.service.getImportDeploy(table);
        return (idm.getIs_insert() == 1);
    }

    public boolean isImportUpdate(String table) {
        ImportDeployModel idm = this.service.getImportDeploy(table);
        return (idm.getIs_update() == 1);
    }

    public int tableKeySize(String tableName) {
        TableDescribe tableDesc = this.service.getCacheService().getTableDesc(tableName);
        String[] idColumns = tableDesc.getIdColumns();
        return (idColumns == null) ? 0 : idColumns.length;
    }
}
