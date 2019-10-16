package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.DeployTool.service.DashboardService;
import cn.com.easyerp.auth.AuthConfigDescribe;
import cn.com.easyerp.batch.BatchInterceptor;
import cn.com.easyerp.core.ApplicationContextProvider;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.approve.ApproveBlockEvent;
import cn.com.easyerp.core.approve.FlowBlock;
import cn.com.easyerp.core.approve.FlowBlockEditColumn;
import cn.com.easyerp.core.approve.FlowConditionDetail;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.approve.FlowLine;
import cn.com.easyerp.core.authGroup.AuthDataGroup;
import cn.com.easyerp.core.authGroup.AuthDataGroupDetail;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.authGroup.MenuGroup;
import cn.com.easyerp.core.cache.style.ColumnMapViewStyle;
import cn.com.easyerp.core.cache.style.ColumnViewStyle;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.evaluate.FormulaService;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.ftp.FtpService;
import cn.com.easyerp.core.ftp.SftpService;
import cn.com.easyerp.core.logger.Loggable;
import cn.com.easyerp.core.mail.MailServer;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.core.widget.menu.PageModel;
import cn.com.easyerp.framework.common.Common;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service
public class CacheService {
    public static final int I18N_TYPE_TABLE = 1;
    public static final int I18N_TYPE_COLUMN = 2;
    public static final int I18N_TYPE_DICT = 3;
    public static final int I18N_TYPE_MENU = 4;
    private static final Pattern sqlVarPattern = Pattern.compile("#\\{([^}]+)}");
    public static final int I18N_TYPE_MESSAGE = 5;
    public static final int I18N_TYPE_REPORT = 6;
    public static final int I18N_TYPE_GROUP = 7;
    public static final int I18N_TYPE_BATCH = 9;
    public static final int I18N_TYPE_ACTION = 10;
    public static final int I18N_TYPE_TABLE_SHORTCUT = 11;
    public static final int I18N_TYPE_TABLE_TAB = 12;
    public static final int I18N_TYPE_AUTH_GROPU = 13;
    public static final String C_COLUMN_MAP = "c_column_map";
    public static final String C_COLUMN_MAP_KEY = "key";
    public static final String C_COLUMN_MAP_VALUE = "column_key";
    public static final int MENU_TYPE_STANDARD = 1;
    public static final int MENU_TYPE_SHORTCUT = 2;
    public static final String TAG = "CacheService";
    public static final String IS_SQL_PATTERN = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private ApplicationContextProvider applicationContext;
    @Autowired
    private DashboardService dashService;
    @Loggable
    private Logger logger;
    @Autowired
    private FormulaService formulaService;
    private Map<String, DxCache> cacheMap = new HashMap<>();
    private Map<String, CacheLoader<?>> loaders = new HashMap<>();

    public static String makeColumnCacheKey(ColumnDescribe desc) {
        return String.format("%s.%s", new Object[] { desc.getTable_id(), desc.getColumn_name() });
    }

    public static String makeI18nCacheKey(String key1, String key2) {
        return String.format("%s.%s", new Object[] { key1, key2 });
    }

    public static String makeAuthConfigKey(String id, AuthConfigDescribe.Type op) {
        if (op == AuthConfigDescribe.Type.R)
            return id;
        return String.format("%s,%s", new Object[] { id, op });
    }

    private static List<String> parseSqlVar(String sql) {
        List<String> ret = new ArrayList<String>();
        Matcher matcher = sqlVarPattern.matcher(sql);
        while (matcher.find())
            ret.add(matcher.group(1));
        return ret;
    }

    private void buildI18nCache(DxCache cache) {
        cache.tableI18nCache = new HashMap<>();
        cache.columnI18nCache = new HashMap<>();
        cache.dictCache = new HashMap<>();
        cache.menuI18nCache = new HashMap<>();
        cache.msgI18nCache = new HashMap<>();
        cache.reportI18nCache = new HashMap<>();
        cache.groupI18nCache = new HashMap<>();
        cache.batchI18nCache = new HashMap<>();
        cache.actionI18nCache = new HashMap<>();
        cache.tableShortcutI18nCache = new HashMap<>();
        cache.tableTabI18nCache = new HashMap<>();
        cache.authGroupI18nCache = new HashMap<>();
        for (I18nDescribe desc : this.systemDao.selectTableI18N()) {
            cache.tableI18nCache.put(desc.getKey1(), desc);
        }
        for (I18nDescribe desc : this.systemDao.selectColumnI18N()) {
            cache.columnI18nCache.put(makeI18nCacheKey(desc.getKey1(), desc.getKey2()), desc);
        }
        for (I18nDescribe desc : this.systemDao.selectDictI18N()) {
            Map<String, I18nDescribe> describeMap = (Map) cache.dictCache.get(desc.getKey1());
            if (describeMap == null) {
                describeMap = new LinkedHashMap<String, I18nDescribe>();
                cache.dictCache.put(desc.getKey1(), describeMap);
            }
            describeMap.put(desc.getKey2(), desc);
        }
        cache.dictNameI18NCache = new HashMap<>();
        for (I18nDescribe desc : this.systemDao.selectDictNameI18N()) {
            cache.dictNameI18NCache.put(desc.getKey1(), desc);
        }
        for (I18nDescribe desc : this.systemDao.selectMenuI18N()) {
            cache.menuI18nCache.put(desc.getKey1(), desc);
        }
        for (I18nDescribe desc : this.systemDao.selectOtherI18N()) {
            cache.reportI18nCache.put(desc.getKey1(), desc);
            cache.msgI18nCache.put(desc.getKey1(), desc);
            cache.groupI18nCache.put(makeI18nCacheKey(desc.getKey1(), desc.getKey2()), desc);
            cache.batchI18nCache.put(desc.getKey1(), desc);
            cache.actionI18nCache.put(desc.getKey1(), desc);
            cache.tableShortcutI18nCache.put(desc.getKey1(), desc);
            cache.tableTabI18nCache.put(makeI18nCacheKey(desc.getKey1(), desc.getKey2()), desc);
            cache.authGroupI18nCache.put(desc.getKey1(), desc);
        }
    }

    @PostConstruct
    public void init() {
        Map<String, CacheLoader> map = this.applicationContext.getBeansOfType(CacheLoader.class);
        for (Map.Entry<String, CacheLoader> entry : map.entrySet()) {
            this.loaders.put(((CacheLoader) entry.getValue()).getKey(), entry.getValue());
        }
    }

    public void reload() {
        this.dxRoutingDataSource.reloadDataSource();
        reload(this.dxRoutingDataSource.getDomainKey());
    }

    private void error(String msg) {
        this.logger.error(msg);
        throw new ApplicationException(msg);
    }

    public DxCache reload(String domain) {
        DxCache origCache = (DxCache) this.cacheMap.get(domain);
        DxCache cache = new DxCache();
        this.cacheMap.put(domain, cache);
        String errorLog = "";

        try {
            buildI18nCache(cache);

            cache.sqlMap = new HashMap<>();
            cache.orderBy = new HashMap<>();
            List<OrderByDescribe> orderBy = this.systemDao.selectOrderBy();
            for (OrderByDescribe orderByDescribe : orderBy) {
                List<OrderByDescribe> orderByDescribes = (List) cache.orderBy.get(orderByDescribe.getTable_id());
                if (orderByDescribes != null && orderByDescribes.size() != 0) {
                    orderByDescribes.add(orderByDescribe);
                    continue;
                }
                orderByDescribes = new ArrayList<OrderByDescribe>();
                orderByDescribes.add(orderByDescribe);
                cache.orderBy.put(orderByDescribe.getTable_id(), orderByDescribes);
            }

            errorLog = "表配置信息出错";
            cache.tableDescCache = new HashMap<>();
            List<TableDescribe> tables = this.systemDao.selectTableDescribe(domain);
            for (TableDescribe table : tables) {
                cache.tableDescCache.put(table.getId(), table);
                table.setI18n((I18nDescribe) cache.tableI18nCache.get(table.getId()));
                List<OrderByDescribe> orderByDescribes = (List) cache.orderBy.get(table.getId());
                String tableOrderBy = "";
                if (orderByDescribes != null && orderByDescribes.size() != 0) {
                    table.setOrderBy(orderByDescribes);
                    for (OrderByDescribe orderByDescribe : orderByDescribes) {
                        tableOrderBy = tableOrderBy + orderByDescribe.getColumn_name() + " "
                                + ("1".equals(orderByDescribe.getOrder_rule()) ? "asc" : "desc") + ",";
                    }

                    tableOrderBy = tableOrderBy.substring(0, tableOrderBy.length() - 1);
                    table.setOrder_by("".equals(tableOrderBy) ? null : tableOrderBy);
                }
            }

            cache.tableViewStyle = new HashMap<>();
            List<TableViewStyle> tableViewStyles = this.systemDao.selectTableViewStyle();
            for (TableViewStyle TableViewStyle : tableViewStyles) {
                cache.tableViewStyle.put(TableViewStyle.getId(), TableViewStyle);
            }

            cache.complexColumns = new HashMap<>();
            for (ComplexColumnDescribe complex : this.systemDao.selectComplexColumn()) {
                cache.complexColumns.put(complex.getId(), complex);
            }

            for (ComplexColumnMapModel map : this.systemDao.selectComplexColumnMap()) {
                ((ComplexColumnDescribe) cache.complexColumns.get(map.getComplex_id())).addMap(map.getDetail_col(),
                        map.getBase_col());
            }
            cache.urlInterface = new HashMap<>();
            for (UrlInterfaceDescribe urlInterface : this.systemDao.selectUrlInterface(domain)) {
                urlInterface.setI18n((I18nDescribe) cache.reportI18nCache.get(urlInterface.getName()));
                cache.urlInterface.put(urlInterface.getId(), urlInterface);
            }

            String link_json = "";

            List<ColumnMapViewStyle> maps = new ArrayList<ColumnMapViewStyle>();
            List<ColumnDescribe> columnDescribes = this.systemDao.selectColumnsDescribe(domain);
            for (ColumnDescribe column : columnDescribes) {
                if (column.getUrl_id() != null && !"".equals(column.getUrl_id())) {
                    column.setI18n((I18nDescribe) cache.msgI18nCache.get(column.getInternational_id()));
                    UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe) cache.urlInterface
                            .get(column.getUrl_id());
                    if (urlInterfaceDescribe != null)
                        urlInterfaceDescribe.addUrlParam(column);
                    continue;
                }
                String table_id = column.getTable_id();
                TableDescribe table = (TableDescribe) cache.tableDescCache.get(table_id);

                if (table == null) {
                    throw new ApplicationException("table " + table_id + " does not exists");
                }
                if (column.getIs_id_column() == 1) {
                    table.addId_column(column.getColumn_name());
                }
            }
            for (ColumnDescribe column : columnDescribes) {

                Map<String, SqlMap> columnSql = drawSql(column);
                if (columnSql != null) {
                    cache.sqlMap.putAll(columnSql);
                }
                if (column.getTable_id() == null || "".equals(column.getTable_id()))
                    continue;
                String table_id = column.getTable_id();
                TableDescribe table = (TableDescribe) cache.tableDescCache.get(table_id);
                if (table == null) {
                    error("table \"" + table_id + "\" using by column \"" + column.getColumn_name()
                            + "\" not defined in sys_table.");
                }
                if (DataService.sysGroupColumnName.contains(column.getColumn_name()))
                    column.setGroup_name("_SYS_COLUMN");
                column.setI18n((I18nDescribe) cache.columnI18nCache.get(makeColumnCacheKey(column)));
                if (column.getRef_table_cols() != null)
                    column.setRef_mapping(Common.stringMapJson(column.getRef_table_cols()));
                if (column.getComplex_param() != null)
                    column.setComplex_mapping(Common.stringMapJson(column.getComplex_param().replaceAll("#", "\\$")));
                if (column.getData_type() == 1 && column.getData_format() != null) {
                    column.setFormat((FormatDesc) Common.fromJson(column.getData_format(), FormatDesc.jsonRef));
                }
                if (column.getRef_table_name() != null && !"".equals(column.getRef_table_name())
                        && (column.getRef_table_tree() == null || "".equals(column.getRef_table_tree()))
                        && (column.getLink_json() == null || "".equals(column.getLink_json()))
                        && column.getIs_multiple() == 0) {
                    TableDescribe refTableDescribe = (TableDescribe) cache.tableDescCache
                            .get(column.getRef_table_name());
                    if (refTableDescribe != null) {
                        String ref_table_id_column = refTableDescribe.getId_column();
                        if (ref_table_id_column == null)
                            continue;
                        if (ref_table_id_column.split(",").length == 1) {
                            link_json = "{url:'/detail/edit.view', param:'{" + ref_table_id_column + ": ${"
                                    + column.getColumn_name() + "}}',title:'test', method:'dx_default'}";
                            column.setLink_json(link_json);
                            if (Common.notBlank(link_json)) {
                                column.setLink((ColumnLink) Common.fromJson(link_json, ColumnLink.jsonRef));
                            }
                        }
                    } else {
                        cache.loadCacheErrorMsg.add(column.getTable_id() + "表" + column.getColumn_name() + "列关联的表"
                                + column.getRef_table_name() + "不存在\r\n");
                    }
                }
                table.addColumn(column);
                if (column.getRef_table_name() != null) {
                    TableDescribe referenceTable = (TableDescribe) cache.tableDescCache.get(column.getRef_table_name());
                    if (referenceTable != null) {

                        if (!referenceTable.getId().equals(table.getParent_id()) && (table.getTable_type() == 0
                                || table.getTable_type() == 4 || table.getTable_type() == 5))
                            referenceTable.addLinkedColumn(column);
                    }
                }
                if (Common.notBlank(column.getComplex_id())) {
                    table.addComplexColumn(column);
                }
                if (column.getIs_multiple() == 11111111) {
                    ColumnMapViewStyle columnMapViewStyle = new ColumnMapViewStyle();
                    columnMapViewStyle.setInventory("c_column_map");
                    columnMapViewStyle.setFilter(column.getRef_table_sql());
                    columnMapViewStyle.setKey("key");
                    TableDescribe referenceTable = (TableDescribe) cache.tableDescCache.get(column.getRef_table_name());
                    String targetId = referenceTable.getIdColumns()[referenceTable.getIdColumns().length - 1];
                    columnMapViewStyle.setValue("column_key");
                    columnMapViewStyle.setTarget(column.getRef_table_name());
                    columnMapViewStyle.setTargetId(targetId);
                    Set<String> cols = new HashSet<String>(2);
                    cols.add(targetId);
                    String name = referenceTable.getName_column();
                    if (Common.notBlank(name)) {
                        cols.add(name);
                    } else {
                        name = null;
                    }
                    columnMapViewStyle.setTargetName(name);
                    columnMapViewStyle.setCols(cols);
                    if (column.getViewStyle() == null)
                        column.setViewStyle(new ColumnViewStyle());
                    column.getViewStyle().setMap(columnMapViewStyle);
                    maps.add(columnMapViewStyle);
                }
            }
            if (tables.size() > 0) {
                cache.bizParam = new HashMap<>();
                for (TableDescribe t : tables) {

                    if (t.getTable_type() == 3) {
                        cache.bizParam.put(t.getId(), this.systemDao.selectBizParam(t));
                    }
                    String genSql = t.getAuto_gen_sql();
                    if (genSql != null) {
                        TableDescribe parent = (TableDescribe) cache.tableDescCache.get(t.getParent_id());
                        for (String columnName : parseSqlVar(genSql)) {
                            ColumnDescribe column = parent.getColumn(columnName);
                            if (column == null)
                                error(t.getId() + ".auto_gen_sql: \"" + t.getAuto_gen_sql() + "\" has a variable: \""
                                        + columnName + "\" not defined in parent table: \"" + parent

                                                .getId()
                                        + "\"");
                            column.addAutoGenChild(t.getId());
                        }
                    }
                }
            }
            errorLog = "批量操作配置信息出错";

            for (AutoGenTableDesc auto : this.systemDao.selectAutoGen()) {
                if (auto.getMode() != AutoGenTableDesc.AutoGenMode.single) {
                    TableDescribe view = (TableDescribe) cache.tableDescCache.get(auto.getRef_view());
                    if (view != null) {
                        for (ColumnDescribe column : view.getColumns()) {
                            if (column.isRo_insert()) {
                                auto.addEditable(column.getColumn_name());
                            }
                        }
                    } else {

                        cache.loadCacheErrorMsg.add(auto.getTable_id() + "批量操作视图不存在" + auto.getRef_view() + "\r\n");
                    }
                }
                TableDescribe tableDescribe = (TableDescribe) cache.tableDescCache.get(auto.getTable_id());
                if (tableDescribe != null) {
                    tableDescribe.addAutoGen(auto);
                    continue;
                }
                cache.loadCacheErrorMsg.add(auto.getTable_id() + "批量操作表不存在" + auto.getTable_id() + "\r\n");
            }

            TableDescribe table = null;
            List<TableCheckRuleDescribe> checkRules = null;

            errorLog = "表权限配置信息出错";
            List<TableCheckRuleDescribe> tableCheckRuleDescribes = this.systemDao.selectTableCheckRules();
            for (TableCheckRuleDescribe rule : tableCheckRuleDescribes) {
                Map<String, SqlMap> tableCheckSql = drawSql(rule);
                if (tableCheckSql != null) {
                    cache.sqlMap.putAll(tableCheckSql);
                }
                if (rule.getUrl_id() != null && !"".equals(rule.getUrl_id())) {
                    UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe) cache.urlInterface
                            .get(rule.getUrl_id());
                    if (urlInterfaceDescribe != null) {
                        urlInterfaceDescribe.addUrlCheck(rule);
                    }
                    if (urlInterfaceDescribe != null) {
                        table = (TableDescribe) cache.tableDescCache.get(urlInterfaceDescribe.getUrl());
                        checkRules = new ArrayList<TableCheckRuleDescribe>();
                        if (null != table)
                            table.setCheckRules(checkRules);
                        checkRules.add(rule);
                    }
                } else {
                    if (table == null || !table.getId().equals(rule.getTable_id())) {
                        table = (TableDescribe) cache.tableDescCache.get(rule.getTable_id());
                        checkRules = new ArrayList<TableCheckRuleDescribe>();
                        if (null != table)
                            table.setCheckRules(checkRules);
                    }
                    checkRules.add(rule);
                }
                rule.setMsgI18n((I18nDescribe) cache.msgI18nCache.get(rule.getError_msg_id()));
            }

            errorLog = "表状态，状态迁移配置信息出错";
            cache.operations = new HashMap<>();
            for (OperationDescribe op : this.systemDao.selectTableOperations()) {
                table = (TableDescribe) cache.tableDescCache.get(op.getTable_id());
                String oid = op.getId();
                if (op.getStatement() != null)
                    op.setApi((ApiDescribe) ApiDescribe.parseStatement(op.getStatement()).get(0));
                if (table != null) {
                    table.addOperation(op.getColumn_name(), oid);
                } else {

                    cache.loadCacheErrorMsg.add(op.getColumn_name() + "状态迁移表不存在" + op.getTable_id() + "\r\n");
                }
                op.setI18n(getActionI18n(op.getId()));
                cache.operations.put(oid, op);
            }

            errorLog = "表状态，状态迁移权限配置信息出错";
            for (OperationRuleModel rule : this.systemDao.selectTableOperationRules()) {
                ((OperationDescribe) cache.operations.get(rule.getAction_id())).addRules(rule);
            }

            errorLog = "表行颜色配置信息出错";
            for (TableRenderModel render : this.systemDao.selectTableRender()) {
                Map<String, SqlMap> tableApproveSql = drawSql(render);
                if (tableApproveSql != null) {
                    cache.sqlMap.putAll(tableApproveSql);
                }
                ((TableDescribe) cache.tableDescCache.get(render.getTable_id())).addRender(render);
            }

            cache.pages = new HashMap<>();
            for (PageModel page : this.authDao.selectPages()) {
                cache.pages.put(page.getId(), page);
                Map<String, SqlMap> pageSql = drawSql(page);
                if (pageSql != null) {
                    cache.sqlMap.putAll(pageSql);
                }
            }

            cache.actionEvent = new HashMap<>();
            for (ActionEventDescribe actionEventDescribe : this.systemDao.selectActionEvent()) {
                Map<String, SqlMap> tableActionSql = drawSql(actionEventDescribe);
                if (tableActionSql != null) {
                    cache.sqlMap.putAll(tableActionSql);
                }
                if (cache.actionEvent.get(actionEventDescribe.getTable_action_id()) == null) {
                    cache.actionEvent.put(actionEventDescribe.getTable_action_id(), new ArrayList<>());
                }
                ((List) cache.actionEvent.get(actionEventDescribe.getTable_action_id())).add(actionEventDescribe);
                cache.actionEventByEventId.put(actionEventDescribe.getEvent_id(), actionEventDescribe);
            }
            cache.triggers = new HashMap<>();
            for (TriggerDescribe trigger : this.systemDao.selectTableTriggers()) {
                I18nDescribe i18nDescribe = (I18nDescribe) cache.actionI18nCache
                        .get(trigger.getAction_name_international());
                trigger.setAction_name_I18N(i18nDescribe);
                cache.triggers.put(trigger.getAction_id(), trigger);
                trigger.setApi(ApiDescribe.parseStatement(cache.urlInterface,
                        (List) cache.actionEvent.get(trigger.getAction_id())));

                table = (TableDescribe) cache.tableDescCache.get(trigger.getTable_id());
                if (table != null)
                    table.addTrigger(trigger);
            }
            Map<String, List<ActionPrerequistieDescribe>> actionPrerequistieMap = new HashMap<String, List<ActionPrerequistieDescribe>>();
            for (ActionPrerequistieDescribe actionPrerequistieDescribe : this.systemDao.selectActionPrerequistie()) {
                Map<String, SqlMap> tablePrerequistieSql = drawSql(actionPrerequistieDescribe);
                if (tablePrerequistieSql != null) {
                    cache.sqlMap.putAll(tablePrerequistieSql);
                }
                if (actionPrerequistieMap.get(actionPrerequistieDescribe.getTable_action_id()) == null) {
                    actionPrerequistieMap.put(actionPrerequistieDescribe.getTable_action_id(), new ArrayList<>());
                }
                ((List) actionPrerequistieMap.get(actionPrerequistieDescribe.getTable_action_id()))
                        .add(actionPrerequistieDescribe);

                String table_action_id = actionPrerequistieDescribe.getTable_action_id();
                TriggerDescribe triggerDescribe = (TriggerDescribe) cache.triggers.get(table_action_id);
                if (triggerDescribe == null)
                    continue;
                actionPrerequistieDescribe.setViolate_msg_I18N((I18nDescribe) cache.msgI18nCache
                        .get(actionPrerequistieDescribe.getViolate_msg_international_id()));
                List<ActionPrerequistieDescribe> condition = null;
                if (triggerDescribe != null) {
                    condition = triggerDescribe.getCondition();
                    if (condition == null)
                        ((TriggerDescribe) cache.triggers.get(table_action_id)).setCondition(new ArrayList<>());
                    ((TriggerDescribe) cache.triggers.get(table_action_id)).getCondition()
                            .add(actionPrerequistieDescribe);
                }
            }

            for (FlowEvent flowEvent : this.approveDao.selectApproveButtonEvent()) {
                cache.flowEventMap.put(flowEvent.getFlow_event_id(), flowEvent);

                String table_id = flowEvent.getTable_id();
                if (table_id == null || "".equals(table_id)) {
                    continue;
                }

                List<ActionEventDescribe> actionEventDescribes = (List) cache.actionEvent
                        .get(flowEvent.getFlow_event_id());
                if (actionEventDescribes != null) {
                    for (ActionEventDescribe event : actionEventDescribes) {
                        flowEvent.addEvent(event);
                    }
                }
                flowEvent.setCondition((List) actionPrerequistieMap.get(flowEvent.getFlow_event_id()));

                TableDescribe tableDescribe = (TableDescribe) cache.tableDescCache.get(table_id);
                if (tableDescribe == null)
                    continue;
                if (null != flowEvent.getInternational_id())
                    flowEvent.setI18n(
                            (I18nDescribe) cache.msgI18nCache.get(flowEvent.getInternational_id().toLowerCase()));
                tableDescribe.addButtonEvent(flowEvent);
            }

            for (ApproveBlockEvent blockEvent : this.approveDao.selectApproveBlockEvent()) {
                String table_id = blockEvent.getTable_id();
                TableDescribe tableDescribe = (TableDescribe) cache.tableDescCache.get(table_id);
                if (tableDescribe == null) {
                    continue;
                }
                List<ActionEventDescribe> actionEventDescribes = (List) cache.actionEvent
                        .get(blockEvent.getApprove_event_id());
                if (actionEventDescribes != null)
                    for (ActionEventDescribe event : actionEventDescribes) {
                        blockEvent.addEvent(event);
                    }
                if (ApproveBlockEvent.EventType.SUBMIT_EVENT.equals(blockEvent.getEvent_type())) {
                    tableDescribe.setSubmitEvent(blockEvent);
                }
                tableDescribe.addApproveBlockEvent(blockEvent);
            }

            List<TableApproveEventDescribe> approveEvent = this.systemDao.selectAllApproveEvent();
            for (int i = 0; i < approveEvent.size(); i++) {
                Map<String, SqlMap> tableApproveSql = drawSql((TableApproveEventDescribe) approveEvent.get(i));
                if (tableApproveSql != null) {
                    cache.sqlMap.putAll(tableApproveSql);
                }
                TableDescribe tableDescribe = (TableDescribe) cache.tableDescCache
                        .get(((TableApproveEventDescribe) approveEvent.get(i)).getTable_id());
                if (tableDescribe != null) {
                    tableDescribe.addTableApproveEventDescribe((TableApproveEventDescribe) approveEvent.get(i));
                }
            }
            List<FlowBlock> block = this.approveDao.selectBlocks(null);
            cache.flowBlocks = new HashMap<>();
            cache.tableFlowBlock = new HashMap<>();
            if (block != null) {
                for (FlowBlock flowBlock : block) {
                    if (null == cache.flowBlocks.get(flowBlock.getTable_id()))
                        cache.flowBlocks.put(flowBlock.getTable_id(), new HashMap<>());
                    ((Map) cache.flowBlocks.get(flowBlock.getTable_id())).put(flowBlock.getBlock_id(), flowBlock);
                    if (cache.tableFlowBlock.get(flowBlock.getTable_id()) == null) {
                        cache.tableFlowBlock.put(flowBlock.getTable_id(), new ArrayList<>());
                    }
                    ((List) cache.tableFlowBlock.get(flowBlock.getTable_id())).add(flowBlock);
                }
            }

            List<FlowLine> lines = this.approveDao.selectLines(null);
            cache.tableFlowLines = new HashMap<>();
            List<FlowConditionDetail> conditions = this.approveDao.selectCondition(null);
            cache.tableFlowConditionDetails = new HashMap<>();
            if (lines != null) {
                for (FlowLine line : lines) {
                    if (cache.tableFlowLines.get(line.getTable_id()) == null) {
                        cache.tableFlowLines.put(line.getTable_id(), new ArrayList<>());
                    }
                    ((List) cache.tableFlowLines.get(line.getTable_id())).add(line);
                }
            }
            if (conditions != null) {
                for (FlowConditionDetail condition : conditions) {
                    if (cache.tableFlowConditionDetails.get(condition.getTable_id()) == null) {
                        cache.tableFlowConditionDetails.put(condition.getTable_id(), new ArrayList<>());
                    }
                    ((List) cache.tableFlowConditionDetails.get(condition.getTable_id())).add(condition);
                }
            }

            List<FlowBlockEditColumn> flowBlockEditColumns = this.approveDao.selectBlockEditColumn();
            if (null != flowBlockEditColumns) {
                for (FlowBlockEditColumn editColumn : flowBlockEditColumns) {
                    Map<String, FlowBlock> flowBlockMap = (Map) cache.flowBlocks.get(editColumn.getTable());
                    if (null == flowBlockMap)
                        continue;
                    FlowBlock flowBlock = (FlowBlock) flowBlockMap.get(editColumn.getBlock());
                    if (null == flowBlock)
                        continue;
                    flowBlock.addFlowBlockEditColumns(editColumn);
                }
            }
            cache.calendarEventConfigs = new HashMap<>();

            // this.dxRoutingDataSource;

            // this.dxRoutingDataSource;
            cache.domainLanguage = this.systemDao.selectDomainLanguage(DxRoutingDataSource.DX_DEFAULT_DATASOURCE,
                    domain);
            if (cache.domainLanguage == null || "".equals(cache.domainLanguage)) {
                cache.domainLanguage = "cn";
            }
            errorLog = "批处理配置信息出错";
            cache.batches = new HashMap<>();
            for (BatchDescribe batch : this.systemDao.selectBatchDesc()) {
                if (batch.getBatch_id() == null)
                    continue;
                cache.batches.put(batch.getBatch_id(), batch);
                String interceptor_service = batch.getInterceptor_service();
                if (interceptor_service != null) {
                    BatchInterceptor interceptor = (BatchInterceptor) ApplicationContextProvider
                            .getBean(interceptor_service);
                    interceptor.init(batch);
                    batch.setInterceptor(interceptor);
                }
                List<ApiDescribe> apiDescribes = ApiDescribe.parseStatement(batch.getStatement());
                if (apiDescribes != null) {
                    batch.setApi((ApiDescribe) apiDescribes.get(0));
                }
                apiDescribes = ApiDescribe.parseStatement(batch.getUpdate_statement());
                if (apiDescribes != null) {
                    batch.setUpdate_api((ApiDescribe) apiDescribes.get(0));
                }
            }

            cache.systemParam = this.systemDao.selectSystemParam();

            cache

                    .mailServer = new MailServer(cache.systemParam.getMail_host(), cache.systemParam.getMail_user(),
                            cache.systemParam.getMail_addr(), cache.systemParam.getMail_pwd());

            cache

                    .ftpServer = new FtpService(cache.systemParam.getFtp_host(), cache.systemParam.getFtp_port(),
                            cache.systemParam.getFtp_user(), cache.systemParam.getFtp_pwd());

            cache

                    .sftpServer = new SftpService(cache.systemParam.getFtp_user(), cache.systemParam.getFtp_pwd(),
                            cache.systemParam.getFtp_host(), cache.systemParam.getFtp_port());

            cache.tableShortcuts = new HashMap<>();

            for (TableShortcutDescribe ts : this.systemDao.selectTableShortcuts()) {
                ts.setI18n((I18nDescribe) cache.tableShortcutI18nCache.get(ts.getId()));
                cache.tableShortcuts.put(ts.getId(), ts);
                getTableDesc(ts.getTable_id()).addShortcut(ts.getId());
            }
            cache.dashboards = new HashMap<>();
            List<Map<String, Object>> dashlist = this.dashService.getSubscribes();
            if (dashlist != null && dashlist.size() != 0) {
                for (int i = 0; i < dashlist.size(); i++) {
                    Map<String, SqlMap> dashboardSql = drawSql((Map) dashlist.get(i));
                    if (dashboardSql != null) {
                        cache.sqlMap.putAll(dashboardSql);
                    }
                    cache.dashboards.put(((Map) dashlist.get(i)).get("dashboard_id").toString(), dashlist.get(i));
                }
            }

            errorLog = "菜单配置信息出错";
            Map<String, List<MenuModel>> menus = new HashMap<String, List<MenuModel>>();
            Map<String, List<MenuModel>> shortcuts = new HashMap<String, List<MenuModel>>();
            List<MenuModel> menuModels = this.authDao.selectMenu();
            cache.menuMap = new HashMap<>();
            for (MenuModel menu : menuModels) {
                cache.menuMap.put(menu.getId(), menu);
                Map<String, List<MenuModel>> map = (menu.getType() == 1) ? menus : shortcuts;
                String parentId = menu.getParent_id();
                List<MenuModel> list = (List) map.get(parentId);
                if (list == null) {
                    list = new ArrayList<MenuModel>();
                    map.put(parentId, list);
                }
                list.add(menu);
            }
            cache.menu = generateMenu("", menus);
            cache.shortcut = generateMenu("", shortcuts);
            reloadAuth(cache);
            cache.data = new HashMap<>();
            for (Map.Entry<String, CacheLoader<?>> entry : this.loaders.entrySet()) {
                CacheLoader loader = (CacheLoader) entry.getValue();
                cache.data.put(entry.getKey(), loader.reload());
            }
            cache.decryptKey = domain;
            return cache;
        } catch (Exception e) {
            e.printStackTrace();
            this.logger.error("CacheService", e);

            if (origCache != null) {
                this.cacheMap.put(domain, origCache);
            } else {
                this.cacheMap.remove(domain);
            }
            throw new ApplicationException(new Exception(e.toString() + errorLog));
        }
    }

    public void reloadAuth() {
        reloadAuth(get());
    }

    private void reloadAuth(DxCache cache) {
        cache.menuAuth = new ArrayList<>();
        cache.optionAuth = new HashMap<>();
        List<AuthGroup> authGroups = this.systemDao.selectAuthGroup();

        Map<String, List<MenuGroup>> menuGroups = new HashMap<String, List<MenuGroup>>();

        Map<String, List<AuthGroup>> columnGroups = new HashMap<String, List<AuthGroup>>();
        cache.columnAuth = columnGroups;

        Map<String, List<AuthGroup>> optionGroups = new HashMap<String, List<AuthGroup>>();

        Map<String, Map<String, List<AuthGroup>>> approveBlockUsers = new HashMap<String, Map<String, List<AuthGroup>>>();
        for (MenuGroup menuGroup : this.systemDao.selectMenuGroup()) {
            if (menuGroups.get(menuGroup.getMenu_group_id()) == null) {
                menuGroups.put(menuGroup.getMenu_group_id(), new ArrayList<>());
            }
            ((List) menuGroups.get(menuGroup.getMenu_group_id())).add(menuGroup);
        }
        for (AuthGroup authGroup : authGroups) {
            if (authGroup.getType() == 1) {
                authGroup.setMenus((List) menuGroups.get(authGroup.getMenu()));
                cache.menuAuth.add(authGroup);
                continue;
            }
            if (authGroup.getType() == 3) {
                String key = authGroup.getTable() + "." + authGroup.getColumn();
                if (columnGroups.get(key) == null)
                    columnGroups.put(key, new ArrayList<>());
                ((List) columnGroups.get(key)).add(authGroup);
                continue;
            }
            if (authGroup.getType() == 2) {
                String key = authGroup.getTable();
                if (optionGroups.get(key) == null)
                    optionGroups.put(key, new ArrayList<>());
                ((List) optionGroups.get(key)).add(authGroup);
                continue;
            }
            if (authGroup.getType() == 4) {
                if (approveBlockUsers.get(authGroup.getTable()) == null) {
                    approveBlockUsers.put(authGroup.getTable(), new HashMap<>());
                }
                if (((Map) approveBlockUsers.get(authGroup.getTable())).get(authGroup.getBlock()) == null) {
                    ((Map) approveBlockUsers.get(authGroup.getTable())).put(authGroup.getBlock(), new ArrayList<>());
                }
                ((List) ((Map) approveBlockUsers.get(authGroup.getTable())).get(authGroup.getBlock())).add(authGroup);
            }
        }
        cache.approveBlockUsers = approveBlockUsers;
        cache.optionAuth = optionGroups;

        List<AuthDataGroup> authDataGroups = this.systemDao.selectAuthDataGroup();
        cache.authDataGroup = new HashMap<>();
        if (authDataGroups != null) {
            for (AuthDataGroup dataGroup : authDataGroups) {
                dataGroup.setI18n((I18nDescribe) cache.msgI18nCache.get(dataGroup.getInternational_id()));
                cache.authDataGroup.put(dataGroup.getGroup_id(), dataGroup);
            }
        }

        if (cache.authDataGroup != null) {
            List<AuthDataGroupDetail> authDataGroupDetails = this.systemDao.selectAuthDataGroupDetail();
            if (authDataGroupDetails != null) {
                for (AuthDataGroupDetail detail : authDataGroupDetails) {
                    AuthDataGroup dataGroup = (AuthDataGroup) cache.authDataGroup.get(detail.getGroup_id());
                    if (dataGroup == null)
                        continue;
                    dataGroup.addDetail(detail);
                }
            }
        }
    }

    public DxCache get() {
        String currentDomainKey = this.dxRoutingDataSource.getDomainKey();
        DxCache cache = (DxCache) this.cacheMap.get(currentDomainKey);
        if (cache == null) {
            String origKey = this.dxRoutingDataSource.getActiveDomainKey();
            this.dxRoutingDataSource.setActiveDomainKey(currentDomainKey);
            cache = reload(currentDomainKey);
            this.dxRoutingDataSource.setActiveDomainKey(origKey);
        }
        return cache;
    }

    private List<MenuModel> generateMenu(String parent, Map<String, List<MenuModel>> menus) {
        List<MenuModel> m = (List) menus.get(parent);
        if (m == null)
            return null;
        for (MenuModel item : m) {
            List<MenuModel> sub = generateMenu(item.getId(), menus);
            if (sub != null)
                item.setSub(sub);
        }
        return m;
    }

    public Map<String, TableDescribe> getTableDescCache() {
        return (get()).tableDescCache;
    }

    public Map<String, TableViewStyle> getTableViewStyle() {
        return (get()).tableViewStyle;
    }

    public TableDescribe getTableDesc(String key) {
        return (TableDescribe) (get()).tableDescCache.get(key);
    }

    public Map<String, TableDescribe> getTablesDesc() {
        return (get()).tableDescCache;
    }

    public Map<String, Map<String, I18nDescribe>> getDictCache() {
        return (get()).dictCache;
    }

    public Map<String, I18nDescribe> getDictNameI18NCache() {
        return (get()).dictNameI18NCache;
    }

    public Map<String, I18nDescribe> getDict(String key) {
        return (Map) (get()).dictCache.get(key);
    }

    public Map<String, Map<String, I18nDescribe>> getDicts() {
        return (get()).dictCache;
    }

    public I18nDescribe getDictNameI18N(String key) {
        return (I18nDescribe) (get()).dictNameI18NCache.get(key);
    }

    public I18nDescribe getTableI18n(String key) {
        return (I18nDescribe) (get()).tableI18nCache.get(key);
    }

    public I18nDescribe getTableTabI18n(String key) {
        return (I18nDescribe) (get()).tableTabI18nCache.get(key);
    }

    public I18nDescribe getColumnI18n(String key) {
        return (I18nDescribe) (get()).columnI18nCache.get(key);
    }

    public Map<String, I18nDescribe> getMsgI18nCache() {
        return (get()).msgI18nCache;
    }

    public I18nDescribe getMsgI18n(String key) {
        return (I18nDescribe) (get()).msgI18nCache.get(key);
    }

    public I18nDescribe getGroupI18n(String key) {
        return (I18nDescribe) (get()).groupI18nCache.get(key);
    }

    public Map<String, I18nDescribe> getReportI18nCache() {
        return (get()).reportI18nCache;
    }

    public I18nDescribe getReportI18n(String key) {
        return (I18nDescribe) (get()).reportI18nCache.get(key);
    }

    public I18nDescribe getMenuI18n(String key) {
        return (I18nDescribe) (get()).menuI18nCache.get(key);
    }

    public I18nDescribe getBatchI18n(String id) {
        return (I18nDescribe) (get()).batchI18nCache.get(id);
    }

    public I18nDescribe getActionI18n(String id) {
        return (I18nDescribe) (get()).actionI18nCache.get(id);
    }

    public I18nDescribe getAuthGroupI18n(String id) {
        return (I18nDescribe) (get()).authGroupI18nCache.get(id);
    }

    public Map<String, OperationDescribe> getOperations() {
        return (get()).operations;
    }

    public Map<String, TableShortcutDescribe> getTableShortcuts() {
        return (get()).tableShortcuts;
    }

    public SystemParameter getSystemParam() {
        return (get()).systemParam;
    }

    public Map<String, PageModel> getPages() {
        return (get()).pages;
    }

    public List<MenuModel> getMenu() {
        return (get()).menu;
    }

    public MenuModel getMenu(String id) {
        return (MenuModel) (get()).menuMap.get(id);
    }

    public String getDomainLanguage() {
        return (get()).domainLanguage;
    }

    public List<MenuModel> getShortcut() {
        return (get()).shortcut;
    }

    public BatchDescribe getBatch(String name) {
        return (BatchDescribe) (get()).batches.get(name);
    }

    public Map<String, TriggerDescribe> getTriggers() {
        return (get()).triggers;
    }

    public Map<String, BatchDescribe> getBatches() {
        return (get()).batches;
    }

    public Map<String, AuthConfigDescribe> getAuthControl(AuthConfigDescribe.Target target) {
        return (Map) (get()).authConfigs.get(target);
    }

    public Map<String, UrlInterfaceDescribe> getUrlInterface() {
        return (get()).urlInterface;
    }

    public UrlInterfaceDescribe getUrlInterface(String urlId) {
        return (UrlInterfaceDescribe) (get()).urlInterface.get(urlId);
    }

    public Map<String, ComplexColumnDescribe> getComplexColumns() {
        return (get()).complexColumns;
    }

    public FlowEvent getFlowEvent(String flow_event_id) {
        return (FlowEvent) (get()).flowEventMap.get(flow_event_id);
    }

    public Map<String, Map<String, Object>> getBizParam() {
        return (get()).bizParam;
    }

    public ActionEventDescribe getActionEvent(String eventId) {
        return (ActionEventDescribe) (get()).actionEventByEventId.get(eventId);
    }

    public MailServer getMailServer() {
        return (get()).mailServer;
    }

    public FtpService getFtpServer() {
        return (get()).ftpServer;
    }

    public SftpService getSftpServer() {
        return (get()).sftpServer;
    }

    public List<AuthGroup> getMenuAuthGroup() {
        return (get()).menuAuth;
    }

    public List<AuthGroup> getColumnAuth(String key) {
        return (List) (get()).columnAuth.get(key);
    }

    public List<AuthGroup> getOptionAuth(String key) {
        return (List) (get()).optionAuth.get(key);
    }

    public AuthDataGroup getAuthDataGroup(String key) {
        return (AuthDataGroup) (get()).authDataGroup.get(key);
    }

    public FlowBlock getBlock(String tableName, String blockId) {
        return (FlowBlock) ((Map) (get()).flowBlocks.get(tableName)).get(blockId);
    }

    public Map<String, Map<String, FlowBlock>> getFlowBlock() {
        return (get()).flowBlocks;
    }

    public Map<String, Map<String, Object>> getDashboards() {
        return (get()).dashboards;
    }

    public Map<String, Object> getDashboard(String dashId) {
        return (Map) (get()).dashboards.get(dashId);
    }

    public Map<String, SqlMap> getSqlMaps() {
        return (get()).sqlMap;
    }

    public SqlMap getSqlMap(String sqlId) {
        return (SqlMap) (get()).sqlMap.get(sqlId);
    }

    private <T> Map<String, T> getModuleCache(DxCache cache, CacheLoader<T> loader) {
        String key = loader.getKey();
        Map<String, T> data = (Map) cache.data.get(key);
        if (data == null) {
            data = loader.reload();
            cache.data.put(key, data);
        }
        return data;
    }

    public String getDecryptKey() {
        String domainKey = this.dxRoutingDataSource.getDomainKey();
        if (domainKey == null || "".equals(domainKey)) {
            return DxRoutingDataSource.DX_DEFAULT_DATASOURCE;
        }
        return domainKey;
    }

    public String getDecryptKey(String domain) {
        return domain;
    }

    public <T> T getModuleCacheEntry(CacheLoader<T> loader, String id) {
        return (T) getModuleCache(get(), loader).get(id);
    }

    public Object exportModuleCache(String module) {
        CacheLoader<?> loader = (CacheLoader) this.loaders.get(module);
        Map data = getModuleCache(get(), loader);

        return loader.export(data);
    }

    public <T> Map<String, T> getRequestCache(Class<T> clazz) {
        String key = "CACHE_" + clazz.getName();
        Map<String, T> ret = (Map) Common.getRequestObject(key);
        if (ret == null)
            Common.setRequestObject(key, ret = new HashMap<String, T>());
        return ret;
    }

    public Map<String, DxCache> getDxCache() {
        return this.cacheMap;
    }

    private Map<String, SqlMap> drawSql(ColumnDescribe column) {
        if (column == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = column.getFormula();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                column.setFormula(formula);
            }
        }
        String default_value = column.getDefault_value();
        if (default_value != null) {
            Matcher matcher = sql.matcher(default_value);
            while (matcher.find()) {
                String g = matcher.group();
                int i = default_value.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(default_value.substring(i, default_value.length()),
                        "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                default_value = default_value.replace(funVal, sqlId);
                column.setDefault_value(default_value);
            }
        }
        String read_only_condition = column.getRead_only_condition();
        if (read_only_condition != null) {
            Matcher matcher = sql.matcher(read_only_condition);
            while (matcher.find()) {
                String g = matcher.group();
                int i = read_only_condition.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService
                        .getFunStr(read_only_condition.substring(i, read_only_condition.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                read_only_condition = read_only_condition.replace(funVal, sqlId);
                column.setRead_only_condition(read_only_condition);
            }
        }
        return (sqlMap.size() == 0) ? null : sqlMap;
    }

    private Map<String, SqlMap> drawSql(TableCheckRuleDescribe rule) {
        if (rule == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = rule.getFormula();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                rule.setFormula(formula);
            }
        }
        return sqlMap;
    }

    private Map<String, SqlMap> drawSql(ActionEventDescribe action) {
        if (action == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = action.getEvent_param();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                action.setEvent_param(formula);
            }
        }
        return sqlMap;
    }

    private Map<String, SqlMap> drawSql(ActionPrerequistieDescribe actionPrerequistie) {
        if (actionPrerequistie == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = actionPrerequistie.getCheck_condition();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                actionPrerequistie.setCheck_condition(formula);
            }
        }
        return sqlMap;
    }

    private Map<String, SqlMap> drawSql(TableApproveEventDescribe approveEvent) {
        if (approveEvent == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = approveEvent.getEvent_param();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                approveEvent.setEvent_param(formula);
            }
        }
        return sqlMap;
    }

    private Map<String, SqlMap> drawSql(TableRenderModel render) {
        if (render == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = render.getFormula();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                render.setFormula(formula);
            }
        }
        return sqlMap;
    }

    private Map<String, SqlMap> drawSql(PageModel page) {
        if (page == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = page.getParam();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                page.setParam(formula);
            }
        }
        return sqlMap;
    }

    private Map<String, SqlMap> drawSql(Map<String, Object> data) {
        if (data == null)
            return null;
        Map<String, SqlMap> sqlMap = new HashMap<String, SqlMap>();
        String reg = "dxf.sql\\([']?(.*?)[']?\\)|dxf.sql\\([\"](.*?)[\"]\\)|dxf.sql\\([\\\\]['](.*?)[\\\\][']\\)|dxf.sql\\([\\\\][\"](.*?)[\\\\][\"]\\)";
        Pattern sql = Pattern.compile(reg);
        String formula = (data.get("dashboard_param") == null) ? null : data.get("dashboard_param").toString();
        if (formula != null) {
            Matcher matcher = sql.matcher(formula);
            while (matcher.find()) {
                String g = matcher.group();
                int i = formula.indexOf(g);
                String sqlId = ViewService.getNextTagId("");
                String funStr = this.formulaService.getFunStr(formula.substring(i, formula.length()), "sql");
                String funVal = this.formulaService.getFunVal(funStr, "sql");
                sqlMap.put(sqlId, sqlParam(funVal));
                formula = formula.replace(funVal, sqlId);
                data.put("dashboard_param", formula);
            }
        }
        return sqlMap;
    }

    private SqlMap sqlParam(String sql) {
        if (sql == null)
            return null;
        SqlMap sqlMap = new SqlMap();
        sqlMap.setSqlId(sql);
        sqlMap.setSql(sql);
        List<String> param = new ArrayList<String>();
        String reg = "\\$\\{(.*?)\\}|\\#\\{(.*?)\\}";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(sql);
        while (m.find()) {
            String group1 = m.group(1);
            String group2 = m.group(2);
            if (group1 != null)
                param.add(group1);
            if (group2 != null)
                param.add(group2);
        }
        sqlMap.setParam(param);
        return sqlMap;
    }

    public String getDomainKey() {
        return this.dxRoutingDataSource.getDomainKey();
    }

    public Map<String, List<AuthGroup>> getApproveUser(String tableName) {
        return (Map) (get()).approveBlockUsers.get(tableName);
    }

    public List<AuthGroup> getApproveUser(String tableName, String block) {
        Map<String, List<AuthGroup>> approveUser = getApproveUser(tableName);
        if (approveUser == null) {
            return null;
        }
        return (List) approveUser.get(block);
    }

    public List<FlowBlock> getFlowBlock(String tableName) {
        return (List) (get()).tableFlowBlock.get(tableName);
    }

    public List<FlowLine> getFlowLine(String tableName) {
        return (List) (get()).tableFlowLines.get(tableName);
    }

    public List<FlowConditionDetail> getFlowCondition(String tableName) {
        return (List) (get()).tableFlowConditionDetails.get(tableName);
    }
}