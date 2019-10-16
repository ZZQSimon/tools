package cn.com.easyerp.core.evaluate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.approve.FlowBlock;
import cn.com.easyerp.core.approve.FlowBlockEditColumn;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableCheckRuleDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.ChangeModifyRequestModel;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.evaluate.DataModel.ChildModel;
import cn.com.easyerp.core.evaluate.DataModel.DataModel;
import cn.com.easyerp.core.evaluate.DataModel.EvalResponseModel;
import cn.com.easyerp.core.evaluate.DataModel.Model;
import cn.com.easyerp.core.logger.Loggable;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;

@Service
public class CacheModelService {
    public static final String SESSION_MODEL_CACHE_KEY = "SESSION_MODEL_CACHE_KEY";
    public static final String SESSION_LIST_MODEL_CACHE_KEY = "SESSION_LIST_MODEL_CACHE_KEY";
    SimpleDateFormat sf;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private FormulaService formulaService;
    @Autowired
    private GridService gridService;
    @Autowired
    private DataService dataService;
    // @Autowired
    // private ViewService viewService;
    @Autowired
    private SystemDao systemDao;
    @Loggable
    private Logger logger;

    public CacheModelService() {
        this.sf = new SimpleDateFormat("yyyy-MM-dd");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void cacheModel(final String key, final Model model) {
        final Map<String, Model> modelCache = (Map<String, Model>) Common.getSessionObject("SESSION_MODEL_CACHE_KEY");
        if (modelCache == null) {
            Common.putSessionObject("SESSION_MODEL_CACHE_KEY", (Object) new HashMap<>());
        }
        ((Map) Common.getSessionObject("SESSION_MODEL_CACHE_KEY")).put(key, model);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void cacheModel(final String key, final String dataKey, final Model model) {
        Map<String, Map<String, Model>> modelCache = (Map<String, Map<String, Model>>) Common
                .getSessionObject("SESSION_LIST_MODEL_CACHE_KEY");
        if (modelCache == null) {
            Common.putSessionObject("SESSION_LIST_MODEL_CACHE_KEY", (Object) new HashMap<>());
        }
        modelCache = (Map<String, Map<String, Model>>) Common.getSessionObject("SESSION_LIST_MODEL_CACHE_KEY");
        if (modelCache.get(key) == null) {
            modelCache.put(key, new HashMap<String, Model>());
        }
        ((Map<String, Model>) ((Map) Common.getSessionObject("SESSION_LIST_MODEL_CACHE_KEY")).get(key)).put(dataKey,
                model);
    }

    @SuppressWarnings({ "unchecked" })
    public Model getCacheModel(final String key) {
        final Map<String, Model> modelCache = (Map<String, Model>) Common.getSessionObject("SESSION_MODEL_CACHE_KEY");
        if (modelCache == null || modelCache.size() == 0) {
            return null;
        }
        return modelCache.get(key);
    }

    @SuppressWarnings("unchecked")
    public Model getCacheModel(final String key, final String dataKey) {
        final Map<String, Map<String, Model>> modelCache = (Map<String, Map<String, Model>>) Common
                .getSessionObject("SESSION_LIST_MODEL_CACHE_KEY");
        if (modelCache == null || modelCache.size() == 0) {
            return null;
        }
        final Map<String, Model> modelMap = modelCache.get(key);
        if (modelMap == null || modelMap.size() == 0) {
            return null;
        }
        return modelMap.get(dataKey);
    }

    @SuppressWarnings("unchecked")
    public void clearListModel(final String key) {
        final Map<String, Map<String, Model>> modelCache = (Map<String, Map<String, Model>>) Common
                .getSessionObject("SESSION_LIST_MODEL_CACHE_KEY");
        if (modelCache == null || modelCache.size() == 0) {
            return;
        }
        modelCache.put(key, null);
    }

    public void deleteChildRecord(final String id, final String childId, final String dataId) {
        this.deleteChildRecord(id, childId, dataId, false);
    }

    public void deleteChildRecord(final String id, final String childId, final String dataId,
            final boolean isUpdateStatus) {
        final Model parentModel = this.getCacheModel(id);
        if (parentModel == null || parentModel.getChildren() == null) {
            return;
        }
        final ChildModel childModel = parentModel.getChildren().get(childId);
        final List<DataModel> childrenDatas = childModel.getChildrenDatas();
        for (final DataModel data : childrenDatas) {
            if (data.getId().equals(dataId)) {
                if (isUpdateStatus) {
                    data.setStatus("delete");
                    break;
                }
                childrenDatas.remove(data);
                break;
            }
        }
    }

    public Map<String, EvalResponseModel> evaluateForm(final ChangeModifyRequestModel request) {
        final Model cacheModel = this.getCacheModel(request.getId());
        if (cacheModel == null) {
            return null;
        }
        // final TableDescribe table =
        // this.cacheService.getTableDesc(cacheModel.getTableName());
        return this.evaluateAllColumn(cacheModel, request.getColumn(), null, true);
    }

    public Map<String, EvalResponseModel> evaluateAllColumn(final Model cacheModel, final String columnName,
            Map<String, EvalResponseModel> allChanged, final boolean isFirst) {
        final TableDescribe table = this.cacheService.getTableDesc(cacheModel.getTableName());
        if (isFirst) {
            allChanged = new HashMap<String, EvalResponseModel>();
        }
        final List<String> changed = new ArrayList<String>();
        for (final Map.Entry<String, FieldModelBase> entry : cacheModel.getFieldMap().entrySet()) {
            if (entry.getValue().getColumn().equals(columnName)) {
                continue;
            }
            EvalResponseModel evalColumn = null;
            final ColumnDescribe column = table.getColumn(entry.getValue().getColumn());
            if (column.getFormula() != null && !"".equals(column.getFormula())
                    && this.columnContained(column.getFormula(), columnName)) {
                final Object value = this.formulaService.evaluate(cacheModel.getId(), null, column.getColumn_name(),
                        column.getFormula(), null);
                changed.add(column.getColumn_name());
                this.changeValue(cacheModel.getId(), column.getColumn_name(), value);
                if (evalColumn == null) {
                    evalColumn = new EvalResponseModel();
                }
                evalColumn.setFieldId(entry.getValue().getId());
                evalColumn.setValue(value);
            }
            if (column.getPrefix() != null && !"".equals(column.getPrefix())
                    && this.columnContained(column.getPrefix(), columnName)) {
                final Object value = this.formulaService.evaluate(cacheModel.getId(), null, null, column.getPrefix(),
                        null);
                if (evalColumn == null) {
                    evalColumn = new EvalResponseModel();
                }
                evalColumn.setPrefix((value == null) ? "" : value.toString());
            }
            if (column.getSuffix() != null && !"".equals(column.getSuffix())
                    && this.columnContained(column.getSuffix(), columnName)) {
                final Object value = this.formulaService.evaluate(cacheModel.getId(), null, null, column.getSuffix(),
                        null);
                if (evalColumn == null) {
                    evalColumn = new EvalResponseModel();
                }
                evalColumn.setSuffix((value == null) ? "" : value.toString());
            }
            if (evalColumn == null) {
                continue;
            }
            allChanged.put(column.getColumn_name(), evalColumn);
        }
        for (final String change : changed) {
            this.evaluateAllColumn(cacheModel, change, allChanged, false);
        }
        if (!isFirst) {
            return allChanged;
        }
        return allChanged;
    }

    public Map<String, Boolean> evaluateReadonly(final String id) {
        final Model cacheModel = this.getCacheModel(id);
        if (cacheModel == null) {
            return null;
        }
        Map<String, Boolean> readonly = null;
        final TableDescribe table = this.cacheService.getTableDesc(cacheModel.getTableName());
        for (final Map.Entry<String, FieldModelBase> entry : cacheModel.getFieldMap().entrySet()) {
            final ColumnDescribe column = table.getColumn(entry.getValue().getColumn());
            if (column.getRead_only_condition() != null && !"".equals(column.getRead_only_condition())) {
                if (readonly == null) {
                    readonly = new HashMap<String, Boolean>();
                }
                final Object readOnlyCondition = this.formulaService.evaluate(cacheModel.getId(),
                        column.getRead_only_condition());
                if (readOnlyCondition instanceof Boolean) {
                    readonly.put(column.getColumn_name(), (Boolean) readOnlyCondition);
                } else {
                    readonly.put(column.getColumn_name(), false);
                }
            }
        }
        return readonly;
    }

    private boolean columnContained(String formula, final String column) {
        final Pattern patternSql = Pattern.compile("dxf.sql\\(\\\\?['\"](.*?)\\\\?['\"]\\)");
        final Matcher matcherSql = patternSql.matcher(formula);
        while (matcherSql.find()) {
            formula = this.formulaService.replaceSql(formula);
        }
        final String reg = "\\{" + column + "[\\.|\\}]";
        final Pattern pattern = Pattern.compile(reg);
        final Matcher matcher = pattern.matcher(formula);
        return matcher.find();
    }

    public void changeModify(final String id, final String currentBlock, final String columnName, final Object value) {
        final Model cacheModel = this.getCacheModel(id);
        if (cacheModel == null) {
            return;
        }
        final TableDescribe table = this.cacheService.getTableDesc(cacheModel.getTableName());
        if (currentBlock != null && !"".equals(currentBlock)) {
            final Map<String, Map<String, FlowBlock>> flowBlocks = (Map<String, Map<String, FlowBlock>>) this.cacheService
                    .getFlowBlock();
            if (flowBlocks != null && flowBlocks.get(table.getId()) != null
                    && flowBlocks.get(table.getId()).get(currentBlock) != null
                    && flowBlocks.get(table.getId()).get(currentBlock).getFlowBlockEditColumns() != null) {
                final List<FlowBlockEditColumn> flowBlockEditColumns = flowBlocks.get(table.getId()).get(currentBlock)
                        .getFlowBlockEditColumns();
                for (final FlowBlockEditColumn editColumn : flowBlockEditColumns) {
                    if (editColumn.getColumn() != null && editColumn.getColumn().equals(columnName)) {
                        this.changeValue(id, columnName, value);
                        break;
                    }
                }
            }
        } else {
            final ColumnDescribe column = table.getColumn(columnName);
            final String action = cacheModel.getAction();
            switch (action) {
            case "edit":
            case "view": {
                if (column.getIs_id_column() == 1) {
                    return;
                }
                if (column.isRo_update()) {
                    return;
                }
                break;
            }
            case "create": {
                if (column.isRo_insert()) {
                    return;
                }
                break;
            }
            }
            if (column.getRead_only_condition() != null && !"".equals(column.getRead_only_condition())) {
                final Object readOnlyCondition = this.formulaService.evaluate(id, column.getRead_only_condition());
                if (readOnlyCondition instanceof Boolean && !(boolean) readOnlyCondition) {
                    this.changeValue(id, columnName, value);
                }
            } else {
                this.changeValue(id, columnName, value);
            }
        }
    }

    public void changeModify(final Model cacheModel, final String column, final Object val) {
        if (column == null || "".equals(column)) {
            return;
        }
        if (cacheModel.getFieldMap() == null || cacheModel.getFieldMap().get(column) == null) {
            return;
        }
        cacheModel.getFieldMap().get(column).setValue(val);
    }

    public void changeModify(final String parent, final String id, final RecordModel record) {
        final Model cacheModel = this.getCacheModel(parent);
        if (cacheModel == null) {
            throw new ApplicationException("cache error");
        }
        if (cacheModel.getChildren() == null || cacheModel.getChildren().get(id) == null) {
            throw new ApplicationException("cache error");
        }
        final ChildModel childModel = cacheModel.getChildren().get(id);
        final List<DataModel> childrenDatas = childModel.getChildrenDatas();
        switch (record.getStatus()) {
        case inserted: {
            final DataModel dataModel = new DataModel(record.getId(), record.getParent());
            dataModel.setStatus("insert");
            dataModel.setData(record.getFieldMap());
            if (childrenDatas == null) {
                childModel.setChildrenDatas(new ArrayList<DataModel>());
            }
            childModel.getChildrenDatas().add(dataModel);
            break;
        }
        case updated: {
            if (childrenDatas == null || childrenDatas.size() == 0) {
                throw new ApplicationException("cache error");
            }
            for (int i = 0; i < childrenDatas.size(); ++i) {
                if (record.getId().equals(childrenDatas.get(i).getId())) {
                    childrenDatas.get(i).setData(record.getFieldMap());
                    childrenDatas.get(i).setStatus("update");
                }
            }
            break;
        }
        case deleted: {
            if (childrenDatas == null || childrenDatas.size() == 0) {
                throw new ApplicationException("cache error");
            }
            for (int i = 0; i < childrenDatas.size(); ++i) {
                if (record.getId().equals(childrenDatas.get(i).getId())) {
                    childrenDatas.remove(i);
                    break;
                }
            }
            break;
        }
        case none: {
            break;
        }
        default: {
            throw new ApplicationException("no child type");
        }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void changeValue(final String id, final String column, final Object value) {
        final Model cacheModel = this.getCacheModel(id);
        if (cacheModel == null) {
            throw new ApplicationException("cache error");
        }
        final FieldModelBase fieldModelBase = cacheModel.getFieldMap().get(column);
        if (fieldModelBase == null) {
            throw new ApplicationException("no column: " + column);
        }
        fieldModelBase.setValue(value);
        if (fieldModelBase instanceof FieldWithRefModel) {
            final Map<String, Object> ref = (Map<String, Object>) ((FieldWithRefModel) fieldModelBase).getRef();
            if (ref == null) {
                return;
            }
            final TableDescribe table = this.cacheService.getTableDesc(fieldModelBase.getTable());
            final ColumnDescribe columnDesc = table.getColumn(column);
            final TableDescribe refTable = this.cacheService.getTableDesc(columnDesc.getRef_table_name());
            final String[] idColumns = refTable.getIdColumns();
            if (idColumns == null) {
                return;
            }
            final Map<String, Object> idValue = new HashMap<String, Object>();
            idValue.put(idColumns[0], value);
            final List<Map<String, Object>> data = this.systemDao.selectDataByKey(refTable.getId(), idValue);
            if (data.size() > 1) {
                throw new ApplicationException(
                        "table: " + refTable.getId() + " has more than one data by id: " + value);
            }
            if (data.size() == 1) {
                ((FieldWithRefModel) fieldModelBase).setRef((Map) data.get(0));
            }
        }
    }

    public boolean checkSubmit(final DetailFormModel form, final Map<String, Object> data) {
        final Model cacheModel = this.getCacheModel(form.getId());
        if (cacheModel == null) {
            throw new ApplicationException("cache error");
        }
        final TableDescribe table = this.cacheService.getTableDesc(form.getTableName());
        this.checkRuleCheck(form.getId(), this.getCheckRules(table, form.getAction().name()));
        this.checkData(table, cacheModel.getFieldMap(), data);
        return false;
    }

    protected boolean checkChildrenSubmit(final Model cacheModel, final GridModel grid, final String action) {
        for (final RecordModel record : grid.getRecords()) {
            final TableDescribe table = this.cacheService.getTableDesc(grid.getTable());
            final Map<String, Object> data = this.gridService.buildRecordData(record);
            final List<DataModel> childrenDatas = cacheModel.getChildren().get(grid.getId()).getChildrenDatas();
            for (final DataModel dataModel : childrenDatas) {
                if (dataModel.getId().equals(record.getId())) {
                    this.checkData(table, dataModel.getData(), data);
                    break;
                }
            }
        }
        return false;
    }

    private void checkData(final TableDescribe table, final Map<String, FieldModelBase> fieldMap,
            final Map<String, Object> data) {
        for (final Map.Entry<String, FieldModelBase> field : fieldMap.entrySet()) {
            final ColumnDescribe column = table.getColumn(field.getValue().getColumn());
            final Object value = field.getValue().getValue();
            final Object columnData = data.get(column.getColumn_name());
            if (!"upd_date".equals(column.getColumn_name())) {
                if ("cre_date".equals(column.getColumn_name())) {
                    continue;
                }
                if (!this.checkDataByColumn(table, column.getColumn_name(), value, columnData)) {
                    this.logger.error("data error:", (Object) ("table:" + table.getId() + ",column:"
                            + column.getColumn_name() + ",value:" + value + ",columnData:" + columnData));
                    throw new ApplicationException("data error");
                }
                continue;
            }
        }
    }

    private boolean checkDataByColumn(final TableDescribe table, final String columnName, Object value,
            Object columnData) {
        if (value == null) {
            value = "";
        }
        if (columnData == null) {
            columnData = "";
        }
        value = value.toString().replace("<", "&lt;");
        value = value.toString().replace(">", "&gt;");
        columnData = columnData.toString().replace("<", "&lt;");
        columnData = columnData.toString().replace(">", "&gt;");
        final ColumnDescribe column = table.getColumn(columnName);
        switch (column.getData_type()) {
        case 1:
        case 8:
        case 9:
        case 10:
        case 13:
        case 14: {
            return value.toString().equals(columnData.toString());
        }
        case 7: {
            return true;
        }
        case 15: {
            return true;
        }
        case 2:
        case 3: {
            if (value.equals("")) {
                value = 0;
            }
            if (columnData.equals("")) {
                columnData = 0;
            }
            if (value instanceof BigDecimal) {
                final int compare = ((BigDecimal) value).compareTo(new BigDecimal(columnData.toString()));
                return compare == 0;
            }
            if (value instanceof Double) {
                final Long roundValue = Math.round((double) value);
                final Long roundColumnData = Math.round(new Double(columnData.toString()));
                final int compare2 = roundValue.compareTo(roundColumnData);
                return compare2 == 0;
            }
            final Long roundValue = Math.round(new Double(value.toString()));
            final Long roundColumnData = Math.round(new Double(columnData.toString()));
            final int compare2 = roundValue.compareTo(roundColumnData);
            return compare2 == 0;
        }
        case 5: {
            if (value instanceof Boolean && columnData instanceof Integer) {
                return (!(boolean) value || (int) columnData != 0) && ((boolean) value || (int) columnData == 0);
            }
            if (value.equals("false")) {
                value = 0;
            }
            if (columnData.equals("false")) {
                columnData = 0;
            }
            if (value.equals("true")) {
                value = 1;
            }
            if (columnData.equals("true")) {
                columnData = 1;
            }
            return value.toString().equals(columnData.toString());
        }
        case 4: {
            if (columnData instanceof Timestamp) {
                columnData = ((Timestamp) columnData).getTime();
            } else if (columnData instanceof Date) {
                columnData = ((Date) columnData).getTime();
            } else if (columnData instanceof String) {
                try {
                    final Date parseColumnData = this.sf.parse(columnData.toString());
                    columnData = parseColumnData.getTime();
                } catch (Exception ex) {
                }
            }
            if (value instanceof Timestamp) {
                value = ((Timestamp) value).getTime();
            } else if (value instanceof Date) {
                value = ((Date) value).getTime();
            } else if (value instanceof String) {
                try {
                    final Date parseValueData = this.sf.parse(value.toString());
                    value = parseValueData.getTime();
                } catch (Exception ex2) {
                }
            }
            if (value instanceof Long && columnData instanceof Long) {
                final int compare = ((Long) value).compareTo((Long) columnData);
                return compare == 0;
            }
            return value.toString().equals(columnData.toString()) || true;
        }
        case 11:
        case 12: {
            if (columnData instanceof Timestamp) {
                columnData = ((Timestamp) columnData).getTime();
            } else if (columnData instanceof Date) {
                columnData = ((Date) columnData).getTime();
            }
            if (value instanceof Timestamp) {
                value = ((Timestamp) value).getTime();
            } else if (value instanceof Date) {
                value = ((Date) value).getTime();
            }
            if (value instanceof Long && columnData instanceof Long) {
                final int compare = ((Long) value).compareTo((Long) columnData);
                return compare == 0;
            }
            return value.toString().equals(columnData.toString());
        }
        case 6: {
            return value.toString().equals(columnData.toString());
        }
        default: {
            throw new ApplicationException(String.format("not defined data type: '%d'", column.getData_type()));
        }
        }
    }

    private boolean checkRuleCheck(final String id, final List<TableCheckRuleDescribe> checkRules) {
        if (checkRules == null || checkRules.size() == 0) {
            return true;
        }
        for (final TableCheckRuleDescribe rule : checkRules) {
            final Object evaluate = this.formulaService.evaluate(id, rule.getFormula());
            if (!(evaluate instanceof Boolean)) {
                throw new ApplicationException("formula is error: " + rule.getFormula());
            }
            if ((boolean) evaluate && rule.getCheck_level() != 2) {
                final String error_msg_id = rule.getError_msg_id();
                final I18nDescribe msgI18n = this.cacheService.getMsgI18n(error_msg_id);
                String[] msgParams = null;
                if (rule.getError_msg_param() != null) {
                    final Object msgParam = this.formulaService.evaluate(id, rule.getError_msg_param());
                    if (msgParam != null) {
                        msgParams = msgParam.toString().split(",");
                    }
                }
                final String messageText = this.dataService.getMessageText(msgI18n.getInternational_id(),
                        (Object[]) msgParams);
                throw new ApplicationException(messageText);
            }
        }
        return false;
    }

    private List<TableCheckRuleDescribe> getCheckRules(final TableDescribe table, final String action) {
        if (table == null || table.getCheckRules() == null) {
            return null;
        }
        final List<TableCheckRuleDescribe> checkRules = (List<TableCheckRuleDescribe>) table.getCheckRules();
        final List<TableCheckRuleDescribe> resultRules = new ArrayList<TableCheckRuleDescribe>();
        switch (action) {
        case "edit": {
            for (final TableCheckRuleDescribe rule : checkRules) {
                if (rule.getEdit_submit() == 1) {
                    resultRules.add(rule);
                }
            }
            break;
        }
        case "create": {
            for (final TableCheckRuleDescribe rule : checkRules) {
                if (rule.getCreate_submit() == 1) {
                    resultRules.add(rule);
                }
            }
            break;
        }
        }
        return (resultRules.size() == 0) ? null : resultRules;
    }
}
