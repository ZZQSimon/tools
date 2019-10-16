package cn.com.easyerp.core.view.form.detail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthConfigDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.companyCalendar.CalendarEvent;
import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.evaluate.CacheModelService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.core.widget.complex.ComplexInputService;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
import cn.com.easyerp.framework.exception.ApplicationException;

@Service
public class DetailFormService {
    @Autowired
    private AuthService authService;
    @Autowired
    private DataService dataService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private GridService gridService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private ComplexInputService complexInputService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private CalendarDao calendarDao;
    // @Autowired
    // private ApproveService approveService;
    @Autowired
    private AuthDao authDao;
    @Autowired
    private CacheModelService cacheModelService;

    public DetailFormModel createView(String tableName, String parent, ViewDataMap fixedData, AuthDetails user) {
        return createView(tableName, parent, fixedData, user, this);
    }

    public DetailFormModel createView(String tableName, String parent, ViewDataMap fixedData, AuthDetails user,
            DetailFormService builder) {
        TableDescribe table = this.dataService.getTableDesc(tableName);

        this.authService.assertAuth(user, table.getId(),
                table.getTrigger(TriggerDescribe.TriggerType.add).getAction_id());

        List<GridModel> children = childView(tableName, null);
        int cols = table.getDetail_disp_cols();
        DetailFormModel form = builder.build(ActionType.create, parent, tableName,
                this.dataService.buildEmptyDataRecord(tableName, null), children, cols, fixedData);
        form.setHasNext(true);
        form.setNoAuthColumns(this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.R));
        form.setReadonlyColumns(this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.W));
        return form;
    }

    public ActionResult create(DetailFormModel form, Map<String, Object> param, Map<String, Object> calendarColorParam,
            TableDescribe table, AuthDetails user, TriggerDescribe requestTrigger,
            Map<String, TriggerDescribe> childTriggerRequestParams) {
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(param);

        if (form.getParent() != null) {
            WidgetModelBase widget = ViewService.fetchWidgetModel(form.getParent());
            if (GridModel.class.isInstance(widget)) {
                GridModel grid = (GridModel) widget;
                if (grid.isInMemory()) {
                    RecordModel record = this.gridService.insert(grid, param, true);

                    if (record.getFieldMap() != null) {
                        for (Map.Entry<String, FieldModelBase> field : record.getFieldMap().entrySet()) {
                            if (field.getValue() instanceof FieldWithRefModel
                                    && ((FieldWithRefModel) field.getValue()).getRef() == null
                                    && ((FieldModelBase) field.getValue()).getValue() != null
                                    && !"".equals(((FieldModelBase) field.getValue()).getValue())) {
                                TableDescribe refTable = this.cacheService
                                        .getTableDesc(((FieldModelBase) field.getValue()).getTable());
                                Map<String, Object> ref = this.dataService.getRef(
                                        refTable.getColumn(((FieldModelBase) field.getValue()).getColumn()),
                                        ((FieldModelBase) field.getValue()).getValue().toString(), null);
                                TableDescribe tableDesc = this.cacheService.getTableDesc(
                                        refTable.getColumn(((FieldModelBase) field.getValue()).getColumn())
                                                .getRef_table_name());
                                ref.put("ref____name_Expression", this.dataService.buildNameExpression(tableDesc, ref));
                                ((FieldWithRefModel) field.getValue()).setRef(ref);
                            }
                        }
                    }

                    this.cacheModelService.checkSubmit(form, databaseDataMap);

                    this.cacheModelService.changeModify(grid.getParent(), grid.getId(), record);

                    record.setCalendarEvents(this.viewService.calendarColorData(this.viewService.mapDataToDB(param),
                            table, calendarColorParam, true));

                    if (table.hasComplexColumn())
                        this.complexInputService.saveToRecord(record, param.keySet());
                    return Common.ActionOk;
                }
            }
        }

        List<CalendarEvent> calendarEvents = this.viewService.calendarColorData(databaseDataMap, table,
                calendarColorParam, false);
        if (calendarEvents != null && calendarEvents.size() != 0) {
            for (CalendarEvent calendarEvent : calendarEvents) {
                this.calendarDao.addCalendarEvents(calendarEvent);
            }
        }

        this.authService.assertAuth(user, table.getId(),
                table.getTrigger(TriggerDescribe.TriggerType.add).getAction_id());

        this.cacheModelService.checkSubmit(form, databaseDataMap);
        if (table.hasComplexColumn()) {
            this.complexInputService.insert(form.getFields(), (String) databaseDataMap.get(table.getIdColumns()[0]));
        }
        ApiActionResult result = this.apiService.insertRecordWithTrigger(table, databaseDataMap, requestTrigger, null);

        this.gridService.save(table, form.getFieldMap(), "inserted", requestTrigger);

        for (GridModel grid : form.getChildren()) {
            if (childTriggerRequestParams == null) {
                this.gridService.save(grid, databaseDataMap, null, null);
                continue;
            }
            this.gridService.save(grid, databaseDataMap,
                    (TriggerDescribe) childTriggerRequestParams.get(grid.getTable()), Integer.valueOf(1));
        }

        result.setData(buildKeyData(table, databaseDataMap));
        return result;
    }

    public DetailFormModel editView(DetailRequestModel request, AuthDetails user) {
        return editView(request, user, this);
    }

    @SuppressWarnings({ "rawtypes" })
    public DetailFormModel editView(DetailRequestModel request, AuthDetails user, DetailFormService builder) {
        String tableName = request.getTable();
        TableDescribe table = this.dataService.getTableDesc(tableName);

        Map<String, Object> record = this.dataService.selectRecordByKey(tableName,
                this.viewService.convertToDBValues(table, request.getParam()));

        if (request.isReadonly()) {
        }

        int cols = table.getDetail_disp_cols();

        ActionType action = request.isReadonly() ? ActionType.view : ActionType.edit;

        if (request.getParent() != null) {
            WidgetModelBase widget = ViewService.fetchWidgetModel(request.getParent());
            if (RecordModel.class.isInstance(widget)) {
                RecordModel recordModel = (RecordModel) widget;
                widget = ViewService.fetchWidgetModel(recordModel.getParent());
                if (GridModel.class.isInstance(widget) && ((GridModel) widget).isInMemory()) {
                    List<GridModel> children = childView(tableName, recordModel.getValueMap());
                    List<FieldModelBase> recordClone = new ArrayList<FieldModelBase>();
                    for (FieldModelBase field : recordModel.getFields())
                        recordClone.add(field.copy());
                    DetailFormModel form = builder.build(action, request.getParent(), tableName, recordClone, children,
                            cols, request.getFixedData());
                    form.setCalendarEvents(recordModel.getCalendarEvents());
                    return form;
                }
            }
        }

        if (!request.isReadonly()) {
            if (request.getIsOutPage() != 1 && table.getTriggers() != null) {
                this.authService.assertAuth(user, table.getId(),
                        table.getTrigger(TriggerDescribe.TriggerType.edit).getAction_id(),
                        (String) this.authService.getOwner(record));
            }
        }

        List<GridModel> children = childView(tableName, record);
        DetailFormModel form = builder.build(action, request.getParent(), tableName,
                this.dataService.buildModel(table, record), children, cols, request.getFixedData());
        form.setHasNext(request.isHasNext());
        if (request.getIsOutPage() != 1) {
            form.setNoAuthColumns(this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.R));
            form.setReadonlyColumns(this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.W));
        }

        List<Map<String, String>> refChildTableNames = this.systemDao.selectRefChildTable(tableName);
        List<GridModel> refChildTables = new ArrayList<GridModel>();
        if (refChildTableNames != null)
            for (int i = 0; i < refChildTableNames.size(); i++) {
                TableDescribe refChildTable = this.cacheService
                        .getTableDesc((String) ((Map) refChildTableNames.get(i)).get("table_id"));
                GridModel refTableGridModel = new GridModel((String) ((Map) refChildTableNames.get(i)).get("table_id"),
                        record, false, true, (String) ((Map) refChildTableNames.get(i)).get("column_name"));
                refTableGridModel.setNoAuthColumns(this.authService.noAuthColumns(refChildTable, user, null));
                refTableGridModel.setParent(form.getId());
                refChildTables.add(refTableGridModel);
            }
        form.setRefChildTables(refChildTables);

        String refTableData = this.viewService.buildRefTableData(table, record);
        if (refTableData != null) {
            List<CalendarEvent> calendarEvents = this.calendarDao.selectCalendarEventByTable("0000000001", tableName,
                    refTableData);
            form.setCalendarEvents(calendarEvents);
        }
        return form;
    }

    public ActionResult edit(DetailFormModel form, Map<String, Object> param, Map<String, Object> calendarColorParam,
            Map<String, Object> calendarColorStatus, TableDescribe table, AuthDetails user,
            TriggerDescribe requestTriggerr, Map<String, TriggerDescribe> childTriggerRequestParams,
            HttpServletRequest httpRequest) {
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(param);
        httpRequest.setAttribute("data", Common.toJson(databaseDataMap));

        if (form.getParent() != null) {
            WidgetModelBase widget = ViewService.fetchWidgetModel(form.getParent());
            if (widget != null) {
                DatabaseDataMap valueMap = ((RecordModel) widget).getValueMap();
                httpRequest.setAttribute("data", Common.toJson(valueMap) + " -> " + httpRequest.getAttribute("data"));
            }
            if (RecordModel.class.isInstance(widget)) {
                RecordModel record = (RecordModel) widget;
                GridModel grid = (GridModel) ViewService.fetchWidgetModel(record.getParent());
                if (grid.isInMemory()) {
                    this.gridService.update(grid, record, param, true);

                    this.cacheModelService.checkSubmit(form, databaseDataMap);

                    this.cacheModelService.changeModify(grid.getParent(), grid.getId(), record);

                    record.setCalendarEvents(this.viewService.calendarColorData(this.viewService.mapDataToDB(param),
                            table, calendarColorParam, calendarColorStatus, true));

                    if (table.hasComplexColumn())
                        this.complexInputService.saveToRecord(record, param.keySet());
                    return Common.ActionOk;
                }
            }
        }

        List<CalendarEvent> calendarEvents = this.viewService.calendarColorData(databaseDataMap, table,
                calendarColorParam, calendarColorStatus, false);
        if (calendarEvents != null && calendarEvents.size() != 0) {
            for (CalendarEvent calendarEvent : calendarEvents) {
                if ("add".equals(calendarEvent.getStatus())) {
                    this.calendarDao.addCalendarEvents(calendarEvent);
                    continue;
                }
                this.calendarDao.updateCalendarEventsByTable(calendarEvent);
            }
        }

        Map<String, Object> ids = new HashMap<String, Object>();
        for (int i = 0; i < table.getIdColumns().length; i++) {
            ids.put(table.getIdColumns()[i], databaseDataMap.get(table.getIdColumns()[i]));
        }
        if (!this.authService.optionAuth(user, table.getId(),
                table.getTrigger(TriggerDescribe.TriggerType.edit).getAction_id(),
                this.authService.getOwner(databaseDataMap), ids)) {
            throw new ApplicationException("access is denied");
        }

        if (table.hasComplexColumn()) {
            this.complexInputService.update(form.getFields(), (String) databaseDataMap.get(table.getIdColumns()[0]));
        }

        this.cacheModelService.checkSubmit(form, databaseDataMap);
        ApiActionResult result = this.apiService.updateRecordWithTrigger(table, databaseDataMap, form.getFields(),
                requestTriggerr);

        this.gridService.save(table, form.getFieldMap(), "updated", requestTriggerr);

        for (GridModel grid : form.getChildren()) {
            this.gridService.save(grid, databaseDataMap,
                    (TriggerDescribe) childTriggerRequestParams.get(grid.getTable()), Integer.valueOf(1));
        }
        if (table.hasComplexColumn()) {
            this.complexInputService.removeOld(table, (String) databaseDataMap.get(table.getIdColumns()[0]));
        }

        result.setData(buildKeyData(table, databaseDataMap));
        return result;
    }

    private Map<String, Object> buildKeyData(TableDescribe table, Map<String, Object> data) {
        String[] idColumns = table.getIdColumns();
        Map<String, Object> keyData = new HashMap<String, Object>(idColumns.length);
        for (String column : idColumns)
            keyData.put(column, data.get(column));
        return keyData;
    }

    private List<GridModel> childView(String tableName, Map<String, Object> record) {
        List<String> tableNames = this.dataService.getChildrenTable(tableName);
        List<GridModel> grids = new ArrayList<GridModel>(tableNames.size());
        for (String childTableName : tableNames) {
            GridModel grid = this.gridService.buildModel(childTableName, record, true);
            grid.setAutoLoad(true);
            grids.add(grid);
            TableDescribe childTable = this.dataService.getTableDesc(childTableName);
            boolean hasSum = false;
            for (ColumnDescribe column : childTable.getColumns()) {
                if (column.getSum_flag() != null) {
                    hasSum = true;
                    break;
                }
            }
            grid.setHasSum(hasSum);
        }
        return grids;
    }

    public DetailFormModel build(ActionType action, String parent, String tableName, List<FieldModelBase> fields,
            List<GridModel> children, int cols, ViewDataMap fixedData) {
        DetailFormModel form = new DetailFormModel(action, parent, tableName, fields, children, cols);
        form.setFixedData(fixedData);

        if (fixedData != null)
            for (Map.Entry<String, Object> entry : fixedData.entrySet())
                form.getField((String) entry.getKey()).setValue(entry.getValue());
        return form;
    }

    public boolean editAuthCheck(AuthDetails user, TableDescribe table, String actionId, Object owner,
            Map<String, FieldModelBase> fieldMap) {
        String[] idColumns = table.getIdColumns();
        Map<String, Object> ids = new HashMap<String, Object>();
        for (int i = 0; i < idColumns.length; i++) {
            ids.put(idColumns[i], ((FieldModelBase) fieldMap.get(idColumns[i])).getValue());
        }

        if (table.getIs_approve_select() == 1) {
            List<Integer> isInApprove = this.authDao.selectApproveData(table.getId(), user.getId(),
                    ((FieldModelBase) fieldMap.get(idColumns[0])).getValue());
            if (isInApprove != null && isInApprove.size() != 0) {
                for (int i = 0; i < isInApprove.size(); i++) {
                    if (((Integer) isInApprove.get(i)).intValue() != 0) {
                        return true;
                    }
                }
            }
        }
        return this.authService.optionAuth(user, table.getId(), actionId, owner, ids);
    }
}
