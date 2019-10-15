package cn.com.easyerp.passage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.api.ApiResult;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.serializer.ClobSerializer;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
import cn.com.easyerp.storage.StorageService;

@Controller
@RequestMapping({ "/passage" })
public class PassageController extends FormViewControllerBase
{
    public static final String ID = "id";
    public static final String TABLE_NAME = "sys_passage";
    public static final String TABLE_NAME_ID = "passage_id";
    public static final String TABLE_NAME_ROW = "sys_passage_row";
    public static final String TABLE_NAME_ROW_ID = "passage_row_id";
    public static final String TABLE_NAME_ROW_FORMULA = "sys_passage_row_formula";
    @Autowired
    private DataService dataService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private ApiService apiService;
    @Autowired
    private StorageService storageService;
    
    @RequestMapping({ "/passage.view" })
    public ModelAndView view(@RequestBody final PassageRequestModel data) {
        final ActionType action = ActionType.edit;
        final Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", data.getPassage_id());
        final Map<String, Object> map = this.dataService.selectRecordByKey("sys_passage", param);
        final String type = map.get("type").toString();
        final String hasAppend = map.get("has_append").toString();
        final String mainTableName = map.get("main_table").toString();
        final String groupCols = map.get("group_cols").toString();
        final String dispCols = map.get("disp_cols").toString();
        final String filterTables = (map.get("filter_tables") != null) ? map.get("filter_tables").toString() : "";
        final String filterSql = (map.get("filter_sql") != null) ? map.get("filter_sql").toString() : "";
        final List<FieldModelBase> searchConditions = new ArrayList<FieldModelBase>();
        final Map<String, String> searchConditionsKey = new HashMap<String, String>();
        if (!filterTables.isEmpty()) {
            List<FieldModelBase> tempSearchConditions = new ArrayList<FieldModelBase>();
            final String[] split;
            final String[] filters = split = filterTables.split(";");
            for (final String filterTable : split) {
                final String[] filterItem = filterTable.split(":");
                final String[] filterColumn = filterItem[1].split("\\.");
                tempSearchConditions = this.dataService.buildEmptyDataRecord(filterColumn[0], null);
                for (final FieldModelBase field : tempSearchConditions) {
                    if (field.getColumn().equals(filterColumn[1])) {
                        searchConditions.add(field);
                        searchConditionsKey.put(field.getColumn(), filterItem[0]);
                        break;
                    }
                }
            }
        }
        final List<ColumnDescribe> headColumns = this.editHead(mainTableName, groupCols, dispCols);
        final TableDescribe table = this.dataService.getTableDesc(mainTableName);
        String mainIds = "";
        for (final String columnId : table.getIdColumns()) {
            if (mainIds.isEmpty()) {
                mainIds = mainIds.concat(columnId);
            }
            else {
                mainIds = mainIds.concat("," + columnId);
            }
        }
        final PassageFormModel form = new PassageFormModel(action, data.getPassage_id(), mainTableName, mainIds, type, "1".equals(hasAppend), "1".equals(data.getSearchType()), this.exchangeDate(data.getStart_date(), "yyyy-MM-dd"), this.exchangeDate(data.getEnd_date(), "yyyy-MM-dd"), groupCols, headColumns, searchConditions, searchConditionsKey, filterSql, new ArrayList<String>(), data.getMode());
        form.setHideButtons(data.getHideButtons());
        return this.buildModelAndView((FormModelBase)form);
    }
    
    @RequestMapping({ "/table.do" })
    @ResponseBody
    public ActionResult head(@RequestBody final PassageRequestModel data) {
        final PassageFormModel form = (PassageFormModel)ViewService.fetchFormModel(data.getId());
        final List<String> HeadPassColumns = this.editHeadPass(form, data.getStart_date(), data.getEnd_date(), form.getType());
        return new ActionResult(true, (Object)HeadPassColumns);
    }
    
    @RequestMapping({ "/list.do" })
    @ResponseBody
    public ActionResult list(@RequestBody final PassageRequestModel data) {
        final Map<String, Object> ret = new HashMap<String, Object>();
        final String message = this.dateRangeCheck(data.getStart_date(), data.getEnd_date());
        if (!message.isEmpty()) {
            ret.put("msg", message);
            return new ActionResult(true, (Object)ret);
        }
        final PassageFormModel form = (PassageFormModel)ViewService.fetchFormModel(data.getId());
        final List<String> HeadPassColumns = this.editHeadPass(form, data.getStart_date(), data.getEnd_date(), form.getType());
        ret.put("headPassColumns", HeadPassColumns);
        final TableDescribe tableRow = this.dataService.getTableDesc("sys_passage_row");
        final Set<String> columnsNeededRow = new HashSet<String>(tableRow.getColumns().size());
        for (final ColumnDescribe column : tableRow.getColumns()) {
            columnsNeededRow.add(column.getColumn_name());
        }
        final TableDescribe tableRowFormula = this.dataService.getTableDesc("sys_passage_row_formula");
        final Set<String> columnsNeededFormula = new HashSet<String>(tableRowFormula.getColumns().size());
        for (final ColumnDescribe column2 : tableRowFormula.getColumns()) {
            columnsNeededFormula.add(column2.getColumn_name());
        }
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("passage_id", form.getPassageId());
        final List<Map<String, Object>> rows = this.dataService.selectRecordsByValues("sys_passage_row", columnsNeededRow, param);
        final List<String> passageObjs = new ArrayList<String>();
        for (final Map<String, Object> row : rows) {
            passageObjs.add(row.get("disp_name_key").toString());
        }
        ret.put("passageObjs", passageObjs);
        int index = 0;
        final TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        param = new HashMap<String, Object>();
        String[] orderby = new String[form.getHeadColumns().size()];
        final Set<String> columnsNeeded = new HashSet<String>(form.getHeadColumns().size());
        for (final String columnId : table.getIdColumns()) {
            columnsNeeded.add(columnId);
        }
        for (final ColumnDescribe column3 : form.getHeadColumns()) {
            if (!columnsNeeded.contains(column3.getColumn_name())) {
                columnsNeeded.add(column3.getColumn_name());
            }
            orderby[index] = column3.getColumn_name();
            ++index;
        }
        final List<Map<String, Object>> pkeys = data.getPkeys();
        if (pkeys.size() > 0) {
            final Map<String, Object> valueMap = pkeys.get(pkeys.size() - 1);
            for (final String key : form.getMainIds().split(",")) {
                if (data.getParam().containsKey(key)) {
                    valueMap.put(key, data.getParam().get(key));
                }
            }
            final List<Map<String, Object>> records = this.dataService.selectRecordsByValues(form.getTableName(), columnsNeeded, valueMap);
            if (records.size() > 0) {
                ret.put("msg", this.dataService.getMessageText("repeat key", new Object[0]));
                return new ActionResult(true, (Object)ret);
            }
        }
        String tempWhere = null;
        if (!form.getFilterSql().isEmpty()) {
            tempWhere = form.getFilterSql();
            for (final String key2 : form.getSearchConditionsKey().keySet()) {
                final String filterKey = form.getSearchConditionsKey().get(key2);
                final String value = data.getParam().get(key2).toString();
                tempWhere = tempWhere.replaceFirst(filterKey, value);
            }
            if (!data.getWhere().isEmpty()) {
                tempWhere = tempWhere + " and " + data.getWhere();
            }
        }
        else if (!data.getWhere().isEmpty()) {
            tempWhere = data.getWhere();
        }
        List<Map<String, Object>> records = this.dataService.selectRecordsByValues(form.getTableName(), columnsNeeded, (List<ColumnValue>)null, tempWhere, orderby);
        if (form.isSearchAll() && !form.isHasAppend() && records.size() == 0) {
            tempWhere = null;
            if (!data.getWhere().isEmpty()) {
                tempWhere = data.getWhere();
            }
            records = this.dataService.selectRecordsByValues(form.getTableName(), columnsNeeded, (List<ColumnValue>)null, tempWhere, orderby);
        }
        index = 0;
        String excuteSql = "";
        String tempSql = "";
        boolean isSql = false;
        boolean isDetailSql = false;
        String mainId = "";
        String[] tempMainIds = null;
        String tempKeyWhere = "";
        Map<String, Object> record = new HashMap<String, Object>();
        Map<String, String> mainIds = new HashMap<String, String>();
        Map<String, String> newMainIds = new HashMap<String, String>();
        Map<String, Object> tempRecord = new HashMap<String, Object>();
        List<FieldModelBase> fields = new ArrayList<FieldModelBase>();
        List<PassageFieldModel> passageFields = new ArrayList<PassageFieldModel>();
        PassageRecordModel recordModel = null;
        final List<PassageRecordModel> retRecord = new ArrayList<PassageRecordModel>();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> detailResultList = new ArrayList<Map<String, Object>>();
        final Map<String, Object> resultMap = new HashMap<String, Object>();
        for (int recordNum = 0; recordNum < records.size() + pkeys.size(); ++recordNum) {
            mainId = "";
            mainIds = new HashMap<String, String>();
            if (recordNum < records.size()) {
                record = records.get(recordNum);
            }
            else {
                if (records.size() > 0) {
                    record = records.get(records.size() - 1);
                }
                tempRecord = pkeys.get(recordNum - records.size());
                final String[] split2;
                tempMainIds = (split2 = form.getMainIds().split(","));
                for (final String tempMainId : split2) {
                    if (!tempRecord.containsKey(tempMainId)) {
                        if (record.isEmpty()) {
                            tempRecord.put(tempMainId, "");
                        }
                        else {
                            tempRecord.put(tempMainId, record.get(tempMainId));
                        }
                    }
                }
                record = tempRecord;
            }
            for (final String columnId2 : table.getIdColumns()) {
                if (record.containsKey(columnId2)) {
                    String columnVal = "";
                    if (record.get(columnId2) != null) {
                        columnVal = record.get(columnId2).toString();
                    }
                    mainId = mainId.concat(columnId2 + columnVal);
                    mainIds.put(columnId2, columnVal);
                }
            }
            for (final ColumnDescribe column4 : form.getHeadColumns()) {
                tempRecord = new HashMap<String, Object>();
                tempRecord.put(column4.getColumn_name(), record.get(column4.getColumn_name()));
                fields.addAll(this.dataService.buildModel(form.getTableName(), tempRecord));
            }
            for (final Map<String, Object> row2 : rows) {
                isDetailSql = false;
                detailResultList = new ArrayList<Map<String, Object>>();
                tempSql = ((row2.get("detail_sql") != null) ? row2.get("detail_sql").toString() : "");
                if (!tempSql.isEmpty()) {
                    isDetailSql = true;
                    for (final String key3 : form.getSearchConditionsKey().keySet()) {
                        final String filterKey2 = form.getSearchConditionsKey().get(key3);
                        final String value2 = data.getParam().get(key3).toString();
                        tempSql = tempSql.replaceAll(filterKey2, value2);
                    }
                    detailResultList = this.dataService.dynamicQueryList(tempSql, resultMap);
                }
                param = new HashMap<String, Object>();
                param.put("passage_row_id", row2.get("id"));
                orderby = new String[] { "level" };
                final List<Map<String, Object>> formulas = this.dataService.selectRecordsByValues("sys_passage_row_formula", columnsNeededFormula, param, null, orderby);
                for (final Map<String, Object> formula : formulas) {
                    isSql = false;
                    resultList = new ArrayList<Map<String, Object>>();
                    tempSql = ClobSerializer.convert((Clob)formula.get("sql"));
                    if (!tempSql.isEmpty()) {
                        isSql = true;
                        for (final String key4 : form.getSearchConditionsKey().keySet()) {
                            final String filterKey3 = form.getSearchConditionsKey().get(key4);
                            final String value3 = data.getParam().get(key4).toString();
                            tempSql = tempSql.replaceFirst(filterKey3, value3);
                        }
                        newMainIds = new HashMap<String, String>();
                        if (form.getSearchConditions().size() > 0) {
                            tempKeyWhere = "";
                            if (tempSql.indexOf(" where ") > 0) {
                                tempKeyWhere = "where";
                            }
                            else if (tempSql.indexOf(" WHERE ") > 0) {
                                tempKeyWhere = "WHERE";
                            }
                            if (!tempKeyWhere.isEmpty()) {
                                for (final Map.Entry<String, String> entry : mainIds.entrySet()) {
                                    if (tempSql.substring(tempSql.lastIndexOf(tempKeyWhere)).indexOf(entry.getKey()) < 0) {
                                        newMainIds.put(entry.getKey(), entry.getValue());
                                    }
                                }
                            }
                            else {
                                newMainIds = mainIds;
                            }
                        }
                        else {
                            newMainIds = mainIds;
                        }
                        excuteSql = "select ";
                        excuteSql = excuteSql.concat(" number ");
                        excuteSql = excuteSql.concat(" ,passageDate ");
                        for (final Map.Entry<String, String> entry : mainIds.entrySet()) {
                            excuteSql = excuteSql.concat(" ,");
                            if (!newMainIds.containsKey(entry.getKey())) {
                                excuteSql = excuteSql.concat(" '" + entry.getValue() + "' as ");
                            }
                            excuteSql = excuteSql.concat("passage_" + entry.getKey());
                        }
                        if (isDetailSql) {
                            excuteSql = excuteSql.concat(" ,detailId ");
                        }
                        excuteSql = excuteSql.concat(" from ");
                        excuteSql = excuteSql.concat(" ( ");
                        excuteSql = excuteSql.concat(tempSql);
                        excuteSql = excuteSql.concat(" ) temp ");
                        excuteSql = excuteSql.concat(" where ");
                        excuteSql = excuteSql.concat(" passageDate ");
                        excuteSql = excuteSql.concat(" between '");
                        excuteSql = excuteSql.concat(this.exchangeDate(data.getStart_date(), "yyyyMMdd"));
                        excuteSql = excuteSql.concat("' and '");
                        excuteSql = excuteSql.concat(this.exchangeDate(data.getEnd_date(), "yyyyMMdd"));
                        excuteSql = excuteSql.concat("' ");
                        for (final Map.Entry<String, String> entry : newMainIds.entrySet()) {
                            excuteSql = excuteSql.concat(" and ");
                            excuteSql = excuteSql.concat("passage_" + entry.getKey());
                            excuteSql = excuteSql.concat(" = '");
                            excuteSql = excuteSql.concat(entry.getValue());
                            excuteSql = excuteSql.concat("' ");
                        }
                        resultList = this.dataService.dynamicQueryList(excuteSql, resultMap);
                    }
                    final PassageFieldModel passageField = new PassageFieldModel(formula.get("id").toString(), Integer.parseInt(formula.get("level").toString()), formula.get("cond").toString(), Integer.parseInt(formula.get("type").toString()), ClobSerializer.convert((Clob)formula.get("formula")), isSql, resultList);
                    passageFields.add(passageField);
                }
                recordModel = new PassageRecordModel(mainId, mainIds, fields, passageFields, row2.get("id").toString(), row2.get("disp_name_key").toString(), row2.get("upd_statement").toString(), row2.get("edit_cond").toString(), Integer.valueOf(row2.get("total_row").toString()) != 0, Integer.valueOf(row2.get("total_col").toString()) != 0, row2.get("edit_part_bg_color").toString(), row2.get("edit_part_fg_color").toString(), row2.get("unedit_part_bg_color").toString(), row2.get("unedit_part_fg_color").toString(), Integer.valueOf(row2.get("decimal_digit").toString()), isDetailSql, detailResultList);
                retRecord.add(recordModel);
                passageFields = new ArrayList<PassageFieldModel>();
            }
            fields = new ArrayList<FieldModelBase>();
        }
        ret.put("passRecords", retRecord);
        ret.put("msg", "");
        return new ActionResult(true, (Object)ret);
    }
    
    @Transactional
    @RequestMapping({ "/save.do" })
    @ResponseBody
    public ActionResult save(@RequestBody final PassageRequestModel data) {
        String updStatement = "";
        ApiDescribe apiDescribe = new ApiDescribe();
        final List<ApiDescribe> apis = new ArrayList<ApiDescribe>();
        ViewDataMap parm = new ViewDataMap();
        final List<DataMap> parms = new ArrayList<DataMap>();
        final PassageFormModel form = (PassageFormModel)ViewService.fetchFormModel(data.getId());
        final ViewDataMap searchConditions = new ViewDataMap();
        if (form.getSearchConditionsKey().size() > 0) {
            for (final String key : form.getSearchConditionsKey().keySet()) {
                final String filterKey = form.getSearchConditionsKey().get(key);
                final Object value = data.getParam().get(key);
                searchConditions.put(filterKey, value);
                data.getParam().remove(key);
            }
        }
        for (final Map.Entry<String, Object> entry : data.getParam().entrySet()) {
            updStatement = entry.getKey();
            for (final Map<String, Object> rowVals : (List<Map<String, Object>>) entry.getValue()) {
                apiDescribe = new ApiDescribe();
                apiDescribe.setId(updStatement);
                apis.add(apiDescribe);
                parm = new ViewDataMap();
                parm.putAll(searchConditions);
                final Map<String, String> mainIds = (Map<String, String>) rowVals.get("mainIds");
                final String[] split;
                final String[] tempMainIds = split = form.getMainIds().split(",");
                for (final String tempMainId : split) {
                    if (!mainIds.containsKey(tempMainId)) {
                        parm.put("passage_" + tempMainId, "");
                    }
                    else {
                        parm.put("passage_" + tempMainId, mainIds.get(tempMainId));
                    }
                }
                parm.put("detailId", rowVals.get("detailId"));
                if ("1".equals(form.getType())) {
                    parm.put("passageDate", rowVals.get("date"));
                }
                else {
                    parm.put("passageDate", form.getHeadPassColumns_date().get(Integer.valueOf(rowVals.get("dateIndex").toString())));
                }
                parm.put("passageNumber", rowVals.get("number"));
                parm = this.convertToDBValues(this.dataService.getTableDesc(updStatement), parm);
                parms.add(parm);
            }
        }
        final ApiResult apiResult = this.apiService.exec((List)apis, (List)parms);
        final Map<String, Object> returnData = new HashMap<String, Object>();
        String resultMsg = this.dataService.getMessageText("DCS-002", new Object[0]);
        if (!apiResult.isSuccess()) {
            resultMsg = apiResult.getMessages().get(0);
        }
        returnData.put("msg", resultMsg);
        returnData.put("ret", "true");
        return new ActionResult(true, (Object)returnData);
    }
    
    public ViewDataMap convertToDBValues(final TableDescribe table, final ViewDataMap values) {
        final ViewDataMap ret = new ViewDataMap(values.size());
        for (final Map.Entry<String, Object> entry : values.entrySet()) {
            final String key = entry.getKey();
            if (table.getColumn(key) != null) {
                ret.put(key, entry.getValue());
            }
        }
        return ret;
    }
    
    private HSSFCellStyle createStyle(final HSSFWorkbook wb, final short align, final HSSFFont font) {
        final HSSFCellStyle style = wb.createCellStyle();
        style.setBorderBottom((short)1);
        style.setBorderTop((short)1);
        style.setBorderLeft((short)1);
        style.setBorderRight((short)1);
        style.setAlignment(align);
        style.setFont(font);
        return style;
    }
    
    @RequestMapping({ "/export.do" })
    @ResponseBody
    public ActionResult export(@RequestBody final PassageRequestModel data) throws IOException {
        final String fileName = "passgeExport.xls";
        final HSSFWorkbook wb = new HSSFWorkbook();
        final HSSFSheet sheet = wb.createSheet("passage");
        final HSSFFont noDataFont = wb.createFont();
        final HSSFFont editingDataFont = wb.createFont();
        editingDataFont.setColor((short)8);
        final HSSFFont staticDataFont = wb.createFont();
        staticDataFont.setColor((short)23);
        final HSSFCellStyle editingDataStyle = this.createStyle(wb, (short)1, editingDataFont);
        final HSSFCellStyle staticDataStyle = this.createStyle(wb, (short)1, staticDataFont);
        final HSSFCellStyle titleStyle = this.createStyle(wb, (short)2, noDataFont);
        titleStyle.setVerticalAlignment((short)0);
        titleStyle.setFillPattern((short)16);
        titleStyle.setFillForegroundColor((short)23);
        titleStyle.setFillBackgroundColor((short)23);
        final HSSFCellStyle headStyle = this.createStyle(wb, (short)2, noDataFont);
        headStyle.setVerticalAlignment((short)0);
        headStyle.setFillPattern((short)16);
        headStyle.setFillForegroundColor((short)22);
        headStyle.setFillBackgroundColor((short)22);
        int columnCnt = 0;
        Map<String, Integer> subUniteInfo = new HashMap<String, Integer>();
        final Map<String, Map<String, Integer>> uniteInfo = new HashMap<String, Map<String, Integer>>();
        final Map<String, Map<String, Integer>> firstUniteInfo = new HashMap<String, Map<String, Integer>>();
        final List<Map<String, Integer>> uniteInfoList = new ArrayList<Map<String, Integer>>();
        HSSFRow row = null;
        HSSFCell cell = null;
        String[] keys = null;
        for (int rowNum = 0; rowNum < data.getExportData().size(); ++rowNum) {
            row = sheet.createRow(rowNum);
            row.setHeight((short)290);
            final List<Map<String, Object>> dataRow = data.getExportData().get(rowNum);
            columnCnt = dataRow.size();
            if (keys == null) {
                keys = new String[columnCnt];
            }
            String key = "";
            for (int colNum = 0; colNum < dataRow.size(); ++colNum) {
                final Map<String, Object> dataCell = dataRow.get(colNum);
                final CellType type = CellType.valueOf((String) dataCell.get("type"));
                final String value = dataCell.get("val").toString();
                final boolean isEdit = (boolean) dataCell.get("isEdit");
                boolean isKeyChange = false;
                if (rowNum > 0) {
                    key = key.concat(",").concat(value);
                    isKeyChange = !key.equals(keys[colNum]);
                    if (isKeyChange) {
                        keys[colNum] = key;
                    }
                }
                cell = row.createCell(colNum);
                if (rowNum == 0) {
                    if (type != CellType.data) {
                        if (!firstUniteInfo.containsKey(value)) {
                            subUniteInfo = new HashMap<String, Integer>();
                            subUniteInfo.put("startRow", rowNum);
                            subUniteInfo.put("startCol", colNum);
                        }
                        else {
                            subUniteInfo = firstUniteInfo.get(value);
                            subUniteInfo.put("endRow", rowNum);
                            subUniteInfo.put("endCol", colNum);
                        }
                        firstUniteInfo.put(value, subUniteInfo);
                    }
                }
                else if (rowNum == 1) {
                    if (type != CellType.data) {
                        final String preValue = sheet.getRow(0).getCell(colNum).getStringCellValue();
                        if (!value.equals(preValue)) {
                            subUniteInfo = firstUniteInfo.get(preValue);
                            if (subUniteInfo != null) {
                                if (subUniteInfo.get("endRow") != null) {
                                    uniteInfoList.add(subUniteInfo);
                                }
                                firstUniteInfo.remove(preValue);
                            }
                        }
                        if (!uniteInfo.containsKey(value)) {
                            subUniteInfo = new HashMap<String, Integer>();
                            subUniteInfo.put("startRow", rowNum);
                            subUniteInfo.put("startCol", colNum);
                        }
                        else {
                            subUniteInfo = uniteInfo.get(value);
                            subUniteInfo.put("endRow", rowNum);
                            subUniteInfo.put("endCol", colNum);
                        }
                        uniteInfo.put(value, subUniteInfo);
                    }
                }
                else if (type != CellType.data) {
                    if (!uniteInfo.containsKey(value)) {
                        subUniteInfo = new HashMap<String, Integer>();
                        subUniteInfo.put("startRow", rowNum);
                        subUniteInfo.put("startCol", colNum);
                    }
                    else {
                        subUniteInfo = uniteInfo.get(value);
                        if (colNum == 0) {
                            if (isKeyChange) {
                                if (subUniteInfo.get("endRow") != null) {
                                    uniteInfoList.add(subUniteInfo);
                                }
                                uniteInfo.remove(value);
                                subUniteInfo = new HashMap<String, Integer>();
                                subUniteInfo.put("startRow", rowNum);
                                subUniteInfo.put("startCol", colNum);
                            }
                            else {
                                subUniteInfo.put("endRow", rowNum);
                                subUniteInfo.put("endCol", colNum);
                            }
                        }
                        else if (colNum == subUniteInfo.get("startCol")) {
                            if (isKeyChange) {
                                if (subUniteInfo.get("endRow") != null) {
                                    uniteInfoList.add(subUniteInfo);
                                }
                                uniteInfo.remove(value);
                                subUniteInfo = new HashMap<String, Integer>();
                                subUniteInfo.put("startRow", rowNum);
                                subUniteInfo.put("startCol", colNum);
                            }
                            else {
                                final String preCellVal = data.getExportData().get(rowNum - 1).get(colNum - 1).get("val").toString();
                                final String CellVal = data.getExportData().get(rowNum).get(colNum - 1).get("val").toString();
                                if (preCellVal.equals(CellVal)) {
                                    subUniteInfo.put("endRow", rowNum);
                                    subUniteInfo.put("endCol", colNum);
                                }
                                else {
                                    if (subUniteInfo.get("endRow") != null) {
                                        uniteInfoList.add(subUniteInfo);
                                    }
                                    uniteInfo.remove(value);
                                    subUniteInfo = new HashMap<String, Integer>();
                                    subUniteInfo.put("startRow", rowNum);
                                    subUniteInfo.put("startCol", colNum);
                                }
                            }
                        }
                        else {
                            subUniteInfo.put("endRow", rowNum);
                            subUniteInfo.put("endCol", colNum);
                        }
                    }
                    uniteInfo.put(value, subUniteInfo);
                    if (rowNum > 0) {
                        final String preValue = sheet.getRow(rowNum - 1).getCell(colNum).getStringCellValue();
                        if (!value.equals(preValue)) {
                            subUniteInfo = uniteInfo.get(preValue);
                            if (subUniteInfo != null) {
                                if (subUniteInfo.get("endRow") != null) {
                                    uniteInfoList.add(subUniteInfo);
                                }
                                uniteInfo.remove(preValue);
                            }
                        }
                    }
                }
                cell.setCellValue(value);
                switch (type) {
                    case data: {
                        if (isEdit) {
                            cell.setCellStyle(editingDataStyle);
                            break;
                        }
                        cell.setCellStyle(staticDataStyle);
                        break;
                    }
                    case head: {
                        cell.setCellStyle(titleStyle);
                        break;
                    }
                    case rowHead: {
                        cell.setCellStyle(headStyle);
                        break;
                    }
                }
            }
        }
        for (final String uniteKey : uniteInfo.keySet()) {
            subUniteInfo = uniteInfo.get(uniteKey);
            if (subUniteInfo.get("endRow") != null) {
                uniteInfoList.add(subUniteInfo);
            }
        }
        for (int colNum2 = 0; colNum2 < columnCnt; ++colNum2) {
            sheet.autoSizeColumn(colNum2);
        }
        for (final Map<String, Integer> subUnite : uniteInfoList) {
            final CellRangeAddress cra = new CellRangeAddress((int)subUnite.get("startRow"), (int)subUnite.get("endRow"), (int)subUnite.get("startCol"), (int)subUnite.get("endCol"));
            sheet.addMergedRegion(cra);
        }
        final File outputFile = this.storageService.tmp(fileName.substring(0, fileName.length() - 4), fileName.substring(fileName.length() - 4));
        final FileOutputStream fos = new FileOutputStream(outputFile);
        wb.write((OutputStream)fos);
        fos.flush();
        fos.close();
        final FileInputStream stream = new FileInputStream(outputFile.getAbsolutePath());
        final String viewUrl = this.storageService.getTmpFilePath(fileName);
        return this.storageService.createDownload(fileName, viewUrl, (InputStream)stream, stream.available());
    }
    
    private List<ColumnDescribe> editHead(final String mainTableName, final String groupCols, final String dispCols) {
        final List<ColumnDescribe> passageHead = new ArrayList<ColumnDescribe>();
        final TableDescribe table = this.dataService.getTableDesc(mainTableName);
        final String cols = groupCols + "," + dispCols;
        for (final String groupCol : Common.split(cols, ",")) {
            for (final ColumnDescribe column : table.getColumns()) {
                if (groupCol.equals(column.getColumn_name())) {
                    passageHead.add(column);
                }
            }
        }
        return passageHead;
    }
    
    private List<String> editHeadPass(final PassageFormModel form, final Date startDate, final Date endDate, final String type) {
        final List<String> passageHeadPass = new ArrayList<String>();
        final List<Long> passageHeadPass_date = new ArrayList<Long>();
        form.setHeadPassColumns_date(passageHeadPass_date);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if ("0".equals(type)) {
            for (Date nextDate = startDate; nextDate.compareTo(endDate) < 0; nextDate = DateUtils.addDays(nextDate, 1)) {
                passageHeadPass_date.add(nextDate.getTime());
                passageHeadPass.add(sdf.format(nextDate));
            }
        }
        else if ("1".equals(type)) {
            sdf = new SimpleDateFormat("yyyy-MM");
            for (Date nextDate = startDate; nextDate.compareTo(endDate) < 0; nextDate = DateUtils.addMonths(nextDate, 1)) {
                passageHeadPass_date.add(nextDate.getTime());
                passageHeadPass.add(sdf.format(nextDate));
            }
        }
        else if ("2".equals(type)) {
            for (Date nextDate = startDate; nextDate.compareTo(endDate) < 0; nextDate = DateUtils.addWeeks(nextDate, 1)) {
                passageHeadPass_date.add(nextDate.getTime());
                passageHeadPass.add(sdf.format(nextDate));
            }
        }
        passageHeadPass_date.add(endDate.getTime());
        passageHeadPass.add(sdf.format(endDate));
        form.setHeadPassColumns_date(passageHeadPass_date);
        return passageHeadPass;
    }
    
    private String exchangeDate(final Date date, final String format) {
        if (date == null) {
            return "";
        }
        final SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    
    private String dateRangeCheck(final Date startDate, final Date endDate) {
        String message = "";
        if (startDate == null) {
            message = this.dataService.getMessageText("Start_Date", new Object[0]);
            message = message + " " + this.dataService.getMessageText("incorrect value", new Object[0]);
        }
        else if (endDate == null) {
            message = this.dataService.getMessageText("End_Date", new Object[0]);
            message = message + " " + this.dataService.getMessageText("incorrect value", new Object[0]);
        }
        else if (startDate.compareTo(endDate) > 0) {
            message = this.dataService.getMessageText("Start_Date", new Object[0]);
            message = message + "/" + this.dataService.getMessageText("End_Date", new Object[0]);
            message = message + " " + this.dataService.getMessageText("incorrect value", new Object[0]);
        }
        return message;
    }
    
    private enum CellType
    {
        data, 
        head, 
        rowHead;
    }
}
