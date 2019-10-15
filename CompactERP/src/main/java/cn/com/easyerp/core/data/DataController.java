package cn.com.easyerp.core.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.service.ImportDeployService;
import cn.com.easyerp.DeployTool.view.ImportDeployModel;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.auth.externalForm.ExternalFormRequestModel;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.SqlMap;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.evaluate.CacheModelService;
import cn.com.easyerp.core.evaluate.FormulaService;
import cn.com.easyerp.core.evaluate.DataModel.EvalResponseModel;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.view.form.index.OpRecordModel;
import cn.com.easyerp.core.view.form.list.ListFormModel;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.core.widget.grid.ExportingGridModel;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.GridRequestModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.core.widget.grid.StdGridModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.storage.StorageService;
import cn.com.easyerp.tg.TreeGridFormModel;

@Controller
@RequestMapping({ "/data" })
public class DataController extends FormViewControllerBase {
    public static final String SPACE = "";
    public static final String SQL_MAP_KEY = "sql_map_key";
    @Autowired
    private DataService dataService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AuthService authService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private GridService gridService;
    @Autowired
    private AutoKeyService autoKeyService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ExportService exportService;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private ImportDeployService importDeployService;
    @Autowired
    private FormulaService formulaService;
    @Autowired
    private CacheModelService cacheModelService;
    private DataFormatter cellFormatter;

    public DataController() {
        this.cellFormatter = new DataFormatter();
    }

    @RequestMapping({ "/initCache.do" })
    @ResponseBody
    public ActionResult initJsCache(final HttpServletRequest req, final HttpSession session) {
        this.dataService.setRootPath(req.getServletContext().getRealPath("/"));
        this.dataService.setProjectUrl(req);
        final Map<String, Object> cache = this.dataService.initCache();
        session.setAttribute("sql_map_key", cache.get("sqlMap"));
        return new ActionResult(true, (Object) cache);
    }

    @RequestMapping({ "/initSqlMapCache.do" })
    @ResponseBody
    public ActionResult initSqlMapCache(final HttpServletRequest req) {
        final Map<String, SqlMap> sqlMaps = (Map<String, SqlMap>) this.cacheService.getSqlMaps();
        return new ActionResult(true, (Object) sqlMaps);
    }

    @RequestMapping({ "/getDomainKey.do" })
    @ResponseBody
    public ActionResult getDomainKey(final HttpServletRequest req) {
        this.dataService.setRootPath(req.getServletContext().getRealPath("/"));
        final String domainKey = this.dxRoutingDataSource.getDomainKey();
        return new ActionResult(true, (Object) domainKey);
    }

    @RequestMapping({ "/cache/{module}/load.do" })
    @ResponseBody
    public ActionResult exportModuleCache(@PathVariable("module") final String module) {
        return new ActionResult(true, this.cacheService.exportModuleCache(module));
    }

    private Object getSingleMapValue(final Map<String, Object> map) {
        final Object[] array = map.values().toArray();
        if (array.length > 0) {
            return array[0];
        }
        return null;
    }

    @RequestMapping({ "/changeModify.do" })
    @ResponseBody
    public ActionResult changeModify(@RequestBody final ChangeModifyRequestModel request) {
        this.cacheModelService.changeModify(request.getId(), request.getCurrentBlock(), request.getColumn(),
                request.getValue());
        final Map<String, EvalResponseModel> responseModelMap = (Map<String, EvalResponseModel>) this.cacheModelService
                .evaluateForm(request);
        Map<String, Boolean> readonly = null;
        if (request.isEvalReadonly()) {
            readonly = (Map<String, Boolean>) this.cacheModelService.evaluateReadonly(request.getId());
        }
        final Map<String, Object> evalValueAndReadonly = new HashMap<String, Object>();
        evalValueAndReadonly.put("evalValue", responseModelMap);
        evalValueAndReadonly.put("readonly", readonly);
        return new ActionResult(true, (Object) evalValueAndReadonly);
    }

    @RequestMapping({ "/formula.do" })
    @ResponseBody
    public ActionResult formula(@RequestBody final FormulaRequestModel request) {
        final Object value = this.formulaService.evaluate(request.getId(), request.getDataId(), request.getColumn(),
                request.getFormula(), (Map) request.getWithNoIdData());
        return new ActionResult(true, value);
    }

    @RequestMapping({ "/checkFormula.do" })
    @ResponseBody
    public ActionResult checkFormula(@RequestBody final FormulaRequestModel request) {
        final Map<String, Object> data = this.dataService.checkFormulaData(request.getTable());
        final Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            final Object evaluate = this.formulaService.evaluate(request.getFormula(), (Object) data);
        } catch (Exception e) {
            resultMap.put("status", false);
            resultMap.put("msg", e.getMessage());
            return new ActionResult(true, (Object) resultMap);
        }
        resultMap.put("status", true);
        resultMap.put("msg", "success");
        return new ActionResult(true, (Object) resultMap);
    }

    @RequestMapping({ "/sql.do" })
    @ResponseBody
    public ActionResult sql(@RequestBody final SqlRequestModel request, final HttpSession session) {
        final String formId = request.getFormId();
        final WidgetModelBase widget = (formId != null) ? ViewService.fetchWidgetModel(formId) : null;
        final SqlMap sqlMap = (SqlMap) ((Map) session.getAttribute("sql_map_key")).get(request.getSql());
        String sql = sqlMap.getSql();
        TableDescribe mainTable = null;
        if (DetailFormModel.class.isInstance(widget)) {
            mainTable = this.dataService.getTableDesc(((DetailFormModel) widget).getTableName());
        } else if (OpRecordModel.class.isInstance(widget)) {
            mainTable = this.dataService.getTableDesc(((OpRecordModel) widget).getTable_id());
        } else if (RecordModel.class.isInstance(widget)) {
            final RecordModel record = (RecordModel) widget;
            final WidgetModelBase parentWidget = ViewService.fetchWidgetModel(record.getParent());
            if (StdGridModel.class.isInstance(parentWidget)) {
                final StdGridModel grid = (StdGridModel) parentWidget;
                mainTable = this.dataService.getTableDesc(grid.getTable());
            } else if (TreeGridFormModel.class.isInstance(parentWidget)) {
                final TreeGridFormModel form = (TreeGridFormModel) parentWidget;
                mainTable = this.dataService.getTableDesc(form.getTableName());
            }
        }
        Map<String, Object> param;
        if (mainTable != null) {
            param = new HashMap<String, Object>();
            for (final Map.Entry<String, Object> p : request.getParam().entrySet()) {
                String name = p.getKey();
                final String[] names = name.split("\\.");
                Object val;
                if (!name.startsWith("__")) {
                    TableDescribe table = mainTable;
                    for (int i = 0; i < names.length - 1; ++i) {
                        if ("_parent".equals(names[i])) {
                            table = this.dataService.getTableDesc(table.getParent_id());
                        } else {
                            table = this.dataService.getTableDesc(table.getColumn(names[i]).getRef_table_name());
                        }
                    }
                    val = ViewService.convertToDBValue(table.getColumn(names[names.length - 1]), p.getValue());
                } else {
                    val = p.getValue();
                }
                if (names.length > 1) {
                    name = name.replace(".", "_");
                    sql = sql.replace(p.getKey(), name);
                }
                param.put(name, val);
            }
        } else {
            param = request.getParam();
        }
        Object ret;
        if (request.isList()) {
            ret = this.dataService.dynamicQueryList(sql, param);
        } else {
            ret = this.dataService.dynamicQuery(sql, param);
        }
        if (ret != null && !request.isMap()) {
            if (request.isList()) {
                final List<Map<String, Object>> data = this.dataService.dynamicQueryList(sql, param);
                final List<Object> list = new ArrayList<Object>(data.size());
                for (final Map<String, Object> record2 : data) {
                    list.add(this.getSingleMapValue(record2));
                }
                ret = list;
            } else {
                ret = this.getSingleMapValue((Map<String, Object>) ret);
            }
        }
        return new ActionResult(true, ret);
    }

    @ResponseBody
    @RequestMapping({ "/getSql.do" })
    public ActionResult getSql(@RequestBody final SqlRequestModel request, final HttpSession session) {
        final SqlMap sqlMap = (SqlMap) ((Map) session.getAttribute("sql_map_key")).get(request.getSql());
        if (sqlMap == null) {
            return new ActionResult(true, (Object) null);
        }
        return new ActionResult(true, (Object) sqlMap.getSql());
    }

    @RequestMapping({ "/seq.do" })
    @ResponseBody
    public ActionResult seq(@RequestBody final SequenceRequestModel request) {
        final TableDescribe table = this.dataService.getTableDesc(request.getTable());
        return new ActionResult(true, (Object) this.autoKeyService.update(table, request.getKeys()));
    }

    @RequestMapping({ "/cache/reload.do" })
    @ResponseBody
    public ActionResult reloadCache(final HttpServletRequest req) {
        this.dataService.setRootPath(req.getServletContext().getRealPath("/"));
        this.cacheService.reload();
        return Common.ActionOk;
    }

    @RequestMapping({ "/import.view" })
    public ModelAndView importExcel(@RequestBody final ImportRequestModel request, final AuthDetails user)
            throws IOException {
        final ListFormModel listform = (ListFormModel) ViewService.fetchFormModel(request.getFormId());
        final String tableName = listform.getTableName();
        final HSSFWorkbook workBook = new HSSFWorkbook(
                this.storageService.getUploadFile(request.getFileId()).getInputStream());
        final HSSFSheet sheet = workBook.getSheetAt(0);
        final int rowSize = sheet.getLastRowNum();
        boolean flag = false;
        int index = 0;
        final Map<String, Integer> columnMap = new HashMap<String, Integer>();
        for (int i = 0; i < rowSize; ++i) {
            final HSSFRow row = sheet.getRow(i);
            for (int cellNum = row.getLastCellNum(), j = 0; j < cellNum; ++j) {
                final HSSFCell cell = row.getCell(j);
                if (cell != null && CellType.STRING.equals(cell.getCellType())) {
                    if ("start".equals(cell.getStringCellValue())) {
                        flag = true;
                        index = i;
                    } else if (flag) {
                        columnMap.put(cell.getStringCellValue(), j);
                    }
                }
            }
            if (flag) {
                this.keyCheck(tableName, columnMap);
                break;
            }
        }
        final TableDescribe table = this.dataService.getTableDesc(tableName);
        List<RecordModel> records = new ArrayList<RecordModel>();
        final String keyColumnId = table.getIdColumns()[table.getIdColumns().length - 1];
        final ColumnDescribe keyColumn = table.getColumn(keyColumnId);
        int keyIndex = 0;
        if (keyColumn.getData_type() == 13) {
            keyIndex = this.autoKeyService.getIndex(table, null);
        }
        final Map<String, String> msgMap = new HashMap<String, String>();
        final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        for (int k = index + 1; k <= rowSize; ++k) {
            final HSSFRow row2 = sheet.getRow(k);
            final Map<String, Object> map = new DatabaseDataMap();
            boolean empty = true;
            for (final String column : columnMap.keySet()) {
                final ColumnDescribe columnDescribe = table.getColumn(column);
                final String id = columnDescribe.getTable_id() + "." + columnDescribe.getColumn_name();
                if (this.authService.newAuthColumn(user, id)) {
                    final HSSFCell cell2 = row2.getCell((int) columnMap.get(column));
                    if (cell2 != null) {
                        map.put(column, this.getValue(cell2, columnDescribe.getData_type()));
                        if (!Common.notBlank(this.cellFormatter.formatCellValue((Cell) cell2))) {
                            continue;
                        }
                        empty = false;
                    } else {
                        map.put(column, null);
                    }
                } else {
                    map.put(column, null);
                }
            }
            if (!empty) {
                data.add(map);
            }
        }
        final String importSql = table.getImport_sql();
        List<DatabaseDataMap> resultData = null;
        String tip;
        if (Common.isBlank(importSql)) {
            for (final Map<String, Object> map2 : data) {
                for (final ColumnDescribe columnDesc : table.getColumns()) {
                    if (!columnMap.containsKey(columnDesc.getColumn_name()) && !columnDesc.isVirtual()) {
                        map2.put(columnDesc.getColumn_name(), null);
                    }
                }
                if (keyColumn.getData_type() == 13) {
                    final AutoKeyDaoModel keyData = this.autoKeyService.getIndexData(table, null, keyIndex);
                    map2.put(keyColumnId, keyData.getNext_id());
                    keyIndex = keyData.getNext_no();
                }
                this.checkColumn(tableName, map2);
                final RecordModel record = new RecordModel((List) this.dataService.buildModel(tableName, map2));
                records.add(record);
            }
            tip = this.dataService.getMessageText("total", records.size());
        } else {
            resultData = this.dataService.selectImportSqlData(table, data, user.getId());
            records = this.dataService.dumpFailedImportData(table, resultData);
            tip = this.dataService.getMessageText("total_and_error", resultData.size(), records.size());
        }
        final ExportingGridModel grid = new ExportingGridModel(tableName);
        grid.setRecords((List) records);
        final ImportFormModel form = new ImportFormModel(request.getParent(), tableName, (StdGridModel) grid, msgMap,
                resultData, tip, Common.isBlank(importSql));
        form.setImport_type(request.getImport_type());
        return this.buildModelAndView((FormModelBase) form);
    }

    private void checkColumn(final String tableName, final Map<String, Object> valueMap) {
        final TableDescribe table = this.dataService.getTableDesc(tableName);
        for (final String key : table.getIdColumns()) {
            final ColumnDescribe col = table.getColumn(key);
            if (col.getData_type() != 13 && (valueMap.get(key) == null || "".equals(valueMap.get(key)))) {
                throw new ApplicationException(this.dataService.getMessageText("null_invalid", key));
            }
        }
    }

    @ResponseBody
    @RequestMapping({ "/codeStr.do" })
    public ActionResult codeStr(@RequestBody final ExternalFormRequestModel request) {
        return new ActionResult(true, (Object) this.dataService.setCodeStr(request.getStr()));
    }

    @ResponseBody
    @RequestMapping({ "/codeStrOut.do" })
    public ActionResult codeStrOut(final String str, final String domain) {
        this.dxRoutingDataSource.setActiveDomainKey(domain);
        return new ActionResult(true, (Object) this.dataService.setCodeStr(str));
    }

    @ResponseBody
    @RequestMapping({ "/decodeStrOut.do" })
    public ActionResult decodeStrOut(final String str, final String domain) {
        this.dxRoutingDataSource.setActiveDomainKey(domain);
        return new ActionResult(true, (Object) this.dataService.getCodeStr(str));
    }

    private void keyCheck(final String tableName, final Map<String, Integer> columnMap) {
        final TableDescribe table = this.dataService.getTableDesc(tableName);
        for (final String tabKey : table.getIdColumns()) {
            final ColumnDescribe col = table.getColumn(tabKey);
            if (!columnMap.containsKey(tabKey) && col.getData_type() != 13) {
                throw new ApplicationException(this.dataService.getMessageText("key_not_exist", tabKey));
            }
        }
        for (final String key : columnMap.keySet()) {
            if (table.getColumn(key) == null) {
                throw new ApplicationException(this.dataService.getMessageText("name_invalid", key));
            }
        }
    }

    private Object getValue(final HSSFCell cell, final int data_type) {
        switch (data_type) {
        case 1:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 13: {
            switch (cell.getCellType()) {
            case NUMERIC: {
                return this.cellFormatter.formatCellValue((Cell) cell);
            }
            default: {
                return cell.getStringCellValue();
            }
            }
        }
        case 3: {
            return Double.parseDouble(this.cellFormatter.formatCellValue((Cell) cell));
        }
        case 2:
        case 5: {
            return Integer.parseInt(this.cellFormatter.formatCellValue((Cell) cell));
        }
        case 4:
        case 11:
        case 12: {
            final Date val = cell.getDateCellValue();
            return (val == null) ? null : Common.convertToSqlDate(cell.getDateCellValue());
        }
        default: {
            throw new ApplicationException(String.format("not defined data type: '%d'", data_type));
        }
        }
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/import.do" })
    public ActionResult importData(@RequestBody final ImportDataModel request) {
        final String id = request.getId();
        final ImportFormModel form = (ImportFormModel) ViewService.fetchFormModel(id);
        final TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        final String keyColumnId = table.getIdColumns()[table.getIdColumns().length - 1];
        final ColumnDescribe keyColumn = table.getColumn(keyColumnId);
        final ImportDeployModel idm = this.importDeployService.getImportDeploy(table.getId());
        final List<String> msgs = new ArrayList<String>();
        if (form.isNoImportSql()) {
            for (final Map.Entry<String, ViewDataMap> entry : request.getData().entrySet()) {
                final ApiActionResult result;
                if ((result = this.insertImportData(table, keyColumn, keyColumnId, msgs,
                        this.viewService.convertToDBValues(table, (ViewDataMap) entry.getValue()))) != null) {
                    return (ActionResult) result;
                }
            }
        } else if (request.getImport_type() == 1) {
            for (final DatabaseDataMap data : form.getData()) {
                final ApiActionResult result;
                if ((result = this.insertImportData(table, keyColumn, keyColumnId, msgs, data)) != null) {
                    return (ActionResult) result;
                }
            }
        } else if (request.getImport_type() == 2) {
            Set<String> keyColumns = new HashSet<String>();
            for (final String key : idm.getUpdate_keywords().split(",")) {
                keyColumns.add(key);
            }
            keyColumns = ((keyColumns.size() == 0) ? null : keyColumns);
            final Map<String, Object> mapper = (Map<String, Object>) Common.paseEventParam(idm.getColumn_mapper());
            final Set<String> updateColumns = ((Map<String, Object>) mapper.get("mapper")).keySet();
            if (updateColumns.size() == 0 && updateColumns == null) {
                throw new ApplicationException(
                        this.dataService.getMessageText("import not allowed to be empty", new Object[0]));
            }
            for (final DatabaseDataMap data2 : form.getData()) {
                final ApiActionResult result;
                if ((result = this.updateImportData(table, msgs, data2, updateColumns, keyColumns)) != null) {
                    return (ActionResult) result;
                }
            }
        }
        return (ActionResult) new ApiActionResult(0,
                this.dataService.getMessageText("import_success_msg", new Object[0]), (List) msgs);
    }

    private ApiActionResult insertImportData(final TableDescribe table, final ColumnDescribe keyColumn,
            final String keyColumnId, final List<String> msgs, final DatabaseDataMap data) {
        if (keyColumn.getData_type() == 13) {
            data.put(keyColumnId, this.autoKeyService.update(table, data));
        }
        final ApiActionResult result = this.apiService.insertImportRecordWithTrigger(table, (Map) data, false);
        msgs.addAll(result.getDetails());
        if (!result.isSuccess()) {
            Common.rollback();
            return new ApiActionResult(result.getResult(),
                    this.dataService.getMessageText("import_faild_msg", new Object[0]), (List) msgs);
        }
        return null;
    }

    private ApiActionResult updateImportData(final TableDescribe table, final List<String> msgs,
            final DatabaseDataMap data, final Set<String> updateColumns, final Set<String> keyColumns) {
        final ApiActionResult result = this.apiService.updateImportRecordWithTrigger(table, (Map) data,
                (TriggerDescribe) null, (Set) updateColumns, (Set) keyColumns);
        msgs.addAll(result.getDetails());
        if (!result.isSuccess()) {
            Common.rollback();
            return new ApiActionResult(result.getResult(),
                    this.dataService.getMessageText("import_faild_msg", new Object[0]), (List) msgs);
        }
        return null;
    }

    @ResponseBody
    @RequestMapping({ "/export.do" })
    public ActionResult exportXls(@RequestBody final exportXlsModel request, final AuthDetails user)
            throws IOException {
        final ListFormModel form = (ListFormModel) ViewService.fetchFormModel(request.getFormId());
        final String tableName = form.getTableName();
        final TableDescribe table = this.dataService.getTableDesc(tableName);
        final String xlsFileName = this.dataService.getTableLabel(table.getId()) + ".xls";
        final List<Map<String, Object>> list = this.exportService.selectExportData(table, request.getRids(), null);
        if (list.size() == 0) {
            return new ActionResult(false, (Object) "no export data");
        }
        return this.exportService.exportAllToXls(table, list, xlsFileName, user, null);
    }

    @ResponseBody
    @RequestMapping({ "/exportAll.do" })
    public ActionResult exportAllXls(@RequestBody final GridRequestModel param, final AuthDetails user)
            throws IOException {
        final String formId = param.getId();
        final GridModel gm = (GridModel) ViewService.fetchWidgetModel(param.getId());
        final String tableName = param.getFilter().getTableName();
        final TableDescribe table = this.dataService.getTableDesc(tableName);
        final List<Map<String, Object>> list = (List<Map<String, Object>>) this.gridService.listAll(gm, param, user);
        final String xlsFileName = this.dataService.getTableLabel(table.getId()) + ".xls";
        if (list.size() == 0) {
            return new ActionResult(false, (Object) "no export data");
        }
        return this.exportService.exportAllToXls(table, list, xlsFileName, user, null);
    }
}
