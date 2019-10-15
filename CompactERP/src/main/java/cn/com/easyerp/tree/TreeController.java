package cn.com.easyerp.tree;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ExportService;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;

@Controller
@RequestMapping({ "/tree" })
public class TreeController extends FormViewControllerBase {
    private static final String EXPORT_FILE_NAME_FORMAT = "bom(%s).csv";
    @Autowired
    private DataService dataService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private ExportService exportService;

    @RequestMapping({ "/view/table.view" })
    public ModelAndView view(@RequestBody TreeFormRequestModel request) {
        String tableName = request.getTable();

        String parentidColumn = "";
        String refTableName = "";
        List<FieldModelBase> fieldsCondition = new ArrayList<FieldModelBase>();

        TableDescribe table = this.dataService.getTableDesc(tableName);

        List<FieldModelBase> fields = this.dataService.buildEmptyDataRecord(tableName, null);

        String valid_end_date = "";
        if (Common.split(table.getValid_date_cols(), ",", true).length > 1) {
            valid_end_date = Common.split(table.getValid_date_cols(), ",")[1];
        }

        List<FieldModelBase> fieldsCond = this.dataService.buildEmptyDataRecord(tableName, null);

        for (FieldModelBase field : fieldsCond) {
            if (field.getColumn().equals(table.getParent_id_column())) {
                parentidColumn = field.getColumn();
                ColumnDescribe desc = this.dataService.getColumnDesc(field);
                if (Common.notBlank(desc.getRef_table_name())) {
                    refTableName = desc.getRef_table_name();
                }
            }

            if (field.getColumn().equals(table.getParent_id_column())) {
                fieldsCondition.add(field);
                continue;
            }
            for (String idColumn : table.getIdColumns()) {
                if (field.getColumn().equals(idColumn)) {
                    if (!field.getColumn().equals(valid_end_date)
                            && !field.getColumn().equals(table.getChildren_id_column())) {
                        fieldsCondition.add(field);
                    }
                }
            }
        }

        int treeType = tableName.equals(refTableName) ? 1 : 2;

        TreeFormModel form = new TreeFormModel(request.getParent(), fields, tableName, refTableName, treeType,
                parentidColumn, fieldsCondition, request.getSearchMode(), request.isExtend());
        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/selectAllTreeNode.do" })
    public ActionResult SelectTreeData(@RequestBody TreeFormRequestModel request) {
        Map<String, Object> returnData = new HashMap<String, Object>();

        TreeFormModel form = (TreeFormModel) request.getWidget();
        String tableName = form.getTableName();
        int treeType = form.getTreeType();
        String refTableName = form.getRefTableName();
        List<FieldModelBase> fieldsCondition = form.getFieldsCondition();

        form.clearNodes();
        Set<String> columnsNeeded = new HashSet<String>();

        String conditions = request.getConditions();
        DatabaseDataMap databaseDataMap = new DatabaseDataMap();
        if (conditions.isEmpty()) {
            databaseDataMap = this.viewService.mapDataToDB(request.getParam());
            if (databaseDataMap.size() > 0) {
                for (FieldModelBase field : fieldsCondition) {
                    if (conditions.isEmpty()) {
                        conditions = databaseDataMap.get(field.getColumn()).toString();
                        continue;
                    }
                    conditions = conditions + "," + databaseDataMap.get(field.getColumn()).toString();
                }
            }
        } else {

            String[] conditionTemp = Common.split(conditions, ",", true);
            for (int i = 0; i < conditionTemp.length; i++) {
                databaseDataMap.put(((FieldModelBase) fieldsCondition.get(i)).getColumn(), conditionTemp[i]);
            }
        }

        TableDescribe table = this.dataService.getTableDesc(tableName);

        for (ColumnDescribe desc : table.getColumns()) {
            for (String idColumn : table.getIdColumns()) {
                if (desc.getColumn_name().equals(idColumn)) {
                    columnsNeeded.add(desc.getColumn_name());
                }
            }
            if (desc.getColumn_name().equals(table.getParent_id_column())
                    || desc.getColumn_name().equals(table.getName_column())) {
                columnsNeeded.add(desc.getColumn_name());
            }
        }

        if (treeType == 1 && conditions.isEmpty())

        {
            List<Map<String, Object>> records = this.dataService.selectRecordsByValues(tableName, columnsNeeded);
            for (Map<String, Object> record : records) {
                String parent, id = record.get(table.getIdColumns()[0]).toString();
                if (record.get(table.getParent_id_column()) == null
                        || record.get(table.getParent_id_column()).toString().isEmpty()) {
                    parent = "#";
                } else {
                    parent = record.get(table.getParent_id_column()).toString();
                }
                String text = record.get(table.getName_column()).toString();

                form.addNode(new TreeNodeModel(id, parent, text));
            }
        } else {
            Map<String, Object> valueMap = new HashMap<String, Object>();
            if (treeType == 1)

            {
                valueMap.put(table.getIdColumns()[0], databaseDataMap.get(table.getParent_id_column()));
                List<Map<String, Object>> records = this.dataService.selectRecordsByValues(tableName, columnsNeeded,
                        valueMap);
                if (records.size() != 0) {
                    Map<String, Object> record = (Map) records.get(0);
                    form.addNode(new TreeNodeModel(record.get(table.getIdColumns()[0]).toString(), "#",
                            record.get(table.getName_column()).toString()));

                    switch (request.getSearchMode()) {
                    case 2:
                        inRecursiveQuery(form, databaseDataMap, "");

                        returnData.put("conditions", conditions);
                        returnData.put("nodes", form.getNodes());
                        return new ActionResult(true, returnData);
                    }
                    recursiveQuery(form, databaseDataMap, "");
                }
            } else if (!conditions.isEmpty()) {
                TableDescribe refTable = this.dataService.getTableDesc(refTableName);
                valueMap.put(refTable.getIdColumns()[0], databaseDataMap.get(table.getParent_id_column()));
                List<Map<String, Object>> records = this.dataService.selectRecordsByValues(refTableName, null,
                        valueMap);
                if (records.size() != 0) {
                    Map<String, Object> record = (Map) records.get(0);
                    String id = record.get(refTable.getIdColumns()[0]).toString();
                    TreeNodeModel node = new TreeNodeModel(id, "#", record.get(refTable.getName_column()).toString());
                    form.addNode(node);
                    switch (request.getSearchMode()) {
                    case 2:
                        inRecursiveQuery(form, databaseDataMap, id);
                        returnData.put("conditions", conditions);
                        returnData.put("nodes", form.getNodes());
                        return new ActionResult(true, returnData);
                    }
                    recursiveQuery(form, databaseDataMap, id);
                }
            }
        }
        returnData.put("conditions", conditions);
        returnData.put("nodes", form.getNodes());
        return new ActionResult(true, returnData);
    }

    private TreeNodeModel buildRecursiveNode(TableDescribe table, String refTableName, String nameColumn,
            String idColumn, String parentId, Map<String, Object> record) {
        if (table.getId().equals(refTableName)) {
            return new TreeNodeModel(record.get(table.getIdColumns()[0]).toString(),
                    record.get(table.getParent_id_column()).toString(), record.get(nameColumn).toString());
        }
        return new TreeNodeModel(ViewService.getNextTagId("t"), parentId,
                ((Map) record.get(idColumn + ".ref")).get(nameColumn).toString());
    }

    private void recursiveQuery(TreeFormModel form, Map<String, Object> conditions, String parentId) {
        String nameColumn;
        List<Map<String, Object>> records;
        String tableName = form.getTableName();
        String refTableName = form.getRefTableName();
        TableDescribe table = this.dataService.getTableDesc(tableName);

        Map<String, Object> newConditions = new HashMap<String, Object>();
        Set<String> columnsNeeded = new HashSet<String>();

        String idColumn = table.getChildren_id_column();
        String oldIdColumn = table.getParent_id_column();
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            if (((String) entry.getKey()).equals(idColumn)) {
                newConditions.put(oldIdColumn, entry.getValue());
                continue;
            }
            newConditions.put(entry.getKey(), entry.getValue());
        }

        String[] valid_data = Common.split(table.getValid_date_cols(), ",", true);

        if (tableName.equals(refTableName)) {
            nameColumn = table.getName_column();

            columnsNeeded.add(idColumn);
            columnsNeeded.add(nameColumn);
            columnsNeeded.add(oldIdColumn);

            records = this.dataService.selectRecordsByValues(tableName, columnsNeeded, newConditions);
        } else {
            TableDescribe refTtable = this.dataService.getTableDesc(refTableName);
            nameColumn = refTtable.getName_column();

            String where = "";
            String start_date = "";

            String valid_date_value = "";

            if (valid_data.length == 1) {
                start_date = valid_data[0];
                valid_date_value = newConditions.get(start_date).toString();
                newConditions.remove(start_date);
                where = "'" + valid_date_value + "'" + " >= " + tableName + "." + start_date;
            } else if (valid_data.length > 1) {
                start_date = valid_data[0];
                String end_date = valid_data[1];
                valid_date_value = newConditions.get(start_date).toString();
                newConditions.remove(start_date);
                where = "'" + valid_date_value + "'" + " between " + tableName + "." + start_date + " and " + tableName
                        + "." + end_date;
            }

            records = this.dataService.selectRecordsByValues(tableName, null, newConditions, where);
            newConditions.put(start_date, valid_date_value);
        }

        for (Map<String, Object> record : records) {

            TreeNodeModel node = buildRecursiveNode(table, refTableName, nameColumn, idColumn, parentId, record);

            node.setData(new RecordModel(this.dataService.buildModel(tableName, record)));
            form.addNode(node);

            newConditions.put(idColumn, record.get(idColumn));
            newConditions.remove(oldIdColumn);
            recursiveQuery(form, newConditions, node.getId());
        }
    }

    private void inRecursiveQuery(TreeFormModel form, Map<String, Object> conditions, String parentId) {
        String nameColumn;
        List<Map<String, Object>> records;
        String tableName = form.getTableName();
        String refTableName = form.getRefTableName();
        TableDescribe table = this.dataService.getTableDesc(tableName);

        Map<String, Object> newConditions = new HashMap<String, Object>();
        Set<String> columnsNeeded = new HashSet<String>();

        String idColumn = table.getParent_id_column();
        String oldIdColumn = table.getChildren_id_column();
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            if (((String) entry.getKey()).equals(idColumn)) {
                newConditions.put(oldIdColumn, entry.getValue());
                continue;
            }
            newConditions.put(entry.getKey(), entry.getValue());
        }

        String[] valid_data = Common.split(table.getValid_date_cols(), ",", true);

        if (tableName.equals(refTableName)) {
            nameColumn = table.getName_column();

            columnsNeeded.add(oldIdColumn);
            columnsNeeded.add(nameColumn);
            columnsNeeded.add(idColumn);

            records = this.dataService.selectRecordsByValues(tableName, columnsNeeded, newConditions);
        } else {
            TableDescribe refTtable = this.dataService.getTableDesc(refTableName);
            nameColumn = refTtable.getName_column();

            String where = "";
            String start_date = "";

            String valid_date_value = "";

            if (valid_data.length == 1) {
                start_date = valid_data[0];
                valid_date_value = newConditions.get(start_date).toString();
                newConditions.remove(start_date);
                where = "'" + valid_date_value + "'" + " >= " + tableName + "." + start_date;
            } else if (valid_data.length > 1) {
                start_date = valid_data[0];
                String end_date = valid_data[1];
                valid_date_value = newConditions.get(start_date).toString();
                newConditions.remove(start_date);
                where = "'" + valid_date_value + "'" + " between " + tableName + "." + start_date + " and " + tableName
                        + "." + end_date;
            }

            records = this.dataService.selectRecordsByValues(tableName, null, newConditions, where);
            newConditions.put(start_date, valid_date_value);
        }

        for (Map<String, Object> record : records) {
            TreeNodeModel node = buildRecursiveNode(table, refTableName, nameColumn, idColumn, parentId, record);

            node.setData(new RecordModel(this.dataService.buildModel(tableName, record)));
            form.addNode(node);

            newConditions.put(idColumn, record.get(idColumn));
            newConditions.remove(oldIdColumn);
            inRecursiveQuery(form, newConditions, node.getId());
        }
    }

    @ResponseBody
    @RequestMapping({ "/deleteCheck.do" })
    public ActionResult deleteCheck(@RequestBody final TreeFormRequestModel request) {
        final TreeFormModel form = (TreeFormModel) request.getWidget();
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        if (form.getTreeType() == 1) {
            final Set<String> columnsNeeded = new HashSet<String>();
            final TableDescribe table = this.dataService.getTableDesc(form.getTableName());
            for (final ColumnDescribe desc : table.getColumns()) {
                if (desc.getColumn_name().equals(table.getIdColumns()[0])
                        || desc.getColumn_name().equals(table.getParent_id_column())
                        || desc.getColumn_name().equals(table.getName_column())) {
                    columnsNeeded.add(desc.getColumn_name());
                }
            }
            final Map<String, Object> valueMap = new HashMap<String, Object>();
            valueMap.put(table.getParent_id_column(), request.getParam().get(table.getIdColumns()[0]));
            records = this.dataService.selectRecordsByValues(form.getTableName(), columnsNeeded, valueMap);
        }
        String msg;
        if (records.size() == 0) {
            msg = this.dataService.getMessageText("DCS-004", new Object[0]);
        } else {
            msg = this.dataService.getMessageText("DCS-005", new Object[0]);
        }
        return new ActionResult(true, (Object) msg);
    }

    @ResponseBody
    @RequestMapping({ "/selectnode.do" })
    public ActionResult Select(@RequestBody TreeFormRequestModel request) {
        TreeFormModel form = (TreeFormModel) request.getWidget();

        String id = request.getParam().get("id").toString();

        Map<String, Object> map = null;
        String tableName = form.getTableName();
        TableDescribe table = this.dataService.getTableDesc(tableName);

        Map<String, Object> newConditions = new HashMap<String, Object>();
        switch (form.getTreeType()) {
        case 1:
            newConditions.put(table.getChildren_id_column(), id);
            break;
        case 2:
            RecordModel record = form.getRecord(id);

            for (String idColumn : table.getIdColumns()) {
                newConditions.put(idColumn, ((FieldModelBase) record.getFieldMap().get(idColumn)).getValue());
            }
            break;
        }

        List<Map<String, Object>> record = this.dataService.selectRecordsByValues(tableName, null, newConditions);

        if (record.size() != 0) {
            map = (Map) record.get(0);
        }

        return new ActionResult(true, map);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/create.do" })
    public ActionResult create(@RequestBody TreeFormRequestModel request) {
        TreeFormModel form = (TreeFormModel) request.getWidget();

        String[] conditions = request.getConditions().split(",");
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam());
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());

        if (form.getTreeType() == 2 && !table.getValid_date_cols().isEmpty()) {

            String message = dateRangeCheck(conditions, table.getValid_date_cols(), databaseDataMap);
            if (!message.isEmpty()) {
                throw new ApplicationException(message);
            }

            String id = databaseDataMap.get(table.getChildren_id_column()).toString();
            boolean result = recursiveCheck(form.getTableName(), databaseDataMap, id, true);
            if (!result) {
                throw new ApplicationException(this.dataService.getMessageText("DCS-PKEXIST",
                        new Object[] { databaseDataMap.get(table.getChildren_id_column()) }));
            }
        }
        if (this.dataService.insertRecord(form.getTableName(), databaseDataMap) != 1) {
            throw new ApplicationException(this.dataService.getMessageText("DCS-901", new Object[0]));
        }
        return new ActionResult(true, this.dataService.getMessageText("DCS-001", new Object[0]));
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/edit.do" })
    public ActionResult edit(@RequestBody TreeFormRequestModel request) {
        int ret;
        TreeFormModel form = (TreeFormModel) request.getWidget();

        String[] conditions = request.getConditions().split(",");
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam());
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());

        if (form.getTreeType() == 2 && !table.getValid_date_cols().isEmpty()) {

            String message = dateRangeCheck(conditions, table.getValid_date_cols(), databaseDataMap);
            if (!message.isEmpty()) {
                throw new ApplicationException(message);
            }

            ret = updateRecord(form.getTableName(), conditions[conditions.length - 1], table

                    .getValid_date_cols(), databaseDataMap);
        } else {

            ret = this.dataService.updateRecord(form.getTableName(), databaseDataMap);
        }

        if (ret != 1) {
            throw new ApplicationException(this.dataService.getMessageText("DCS-902", new Object[0]));
        }
        return new ActionResult(true, this.dataService.getMessageText("DCS-002", new Object[0]));
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/delete.do" })
    public ActionResult delete(@RequestBody TreeFormRequestModel request) {
        TreeFormModel form = (TreeFormModel) request.getWidget();
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        String id = request.getParam().get("id").toString();

        switch (form.getTreeType()) {
        case 1:
            deleteForSingleTable(form, table, id);
            break;
        case 2:
            deleteForBom(table, form.getRecord(id));
            break;
        }

        return new ActionResult(true, this.dataService.getMessageText("DCS-003", new Object[0]));
    }

    @ResponseBody
    @RequestMapping({ "/export.do" })
    public ActionResult export(@RequestBody TreeFormRequestModel request, AuthDetails user) throws IOException {
        TreeFormModel form = (TreeFormModel) request.getWidget();
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        String fileName = String.format("bom(%s).csv",
                new Object[] { ((TreeNodeModel) form.getNodes().get(0)).getText() });
        TreeRecordGetter getter = new TreeRecordGetter(form);
        return this.exportService.exportToCsv(table, request.getIds(), fileName, user, getter);
    }

    private void deleteForSingleTable(TreeFormModel form, TableDescribe table, String id) {
        form.clearNodes();
        Set<String> columnsNeeded = new HashSet<String>();
        columnsNeeded.add(table.getChildren_id_column());

        Map<String, Object> values = new HashMap<String, Object>();
        values.put(table.getIdColumns()[0], id);

        String tableName = table.getId();

        List<Map<String, Object>> records = this.dataService.selectRecordsByValues(tableName, columnsNeeded, values);
        Map<String, Object> data = new HashMap<String, Object>();
        if (records.size() != 0) {
            Map<String, Object> record = (Map) records.get(0);
            form.addNode(new TreeNodeModel(record.get(table.getIdColumns()[0]).toString(), "#",
                    record.get(table.getName_column()).toString()));

            data.put(table.getChildren_id_column(), id);

            recursiveQuery(form, data, "");
        }

        List<ColumnValue> key = new ArrayList<ColumnValue>();
        ColumnValue cv = new ColumnValue(table.getIdColumns()[0], null);
        key.add(cv);
        for (TreeNodeModel node : form.getNodes()) {
            cv.setValue(node.getId());
            if (this.dataService.deleteRecord(tableName, key) != 1) {
                throw new ApplicationException(this.dataService.getMessageText("DCS-903", new Object[0]));
            }
        }
    }

    private void deleteForBom(TableDescribe table, RecordModel record) {
        List<ColumnValue> key = new ArrayList<ColumnValue>();

        for (String column : table.getIdColumns())
            key.add(new ColumnValue(column, ((FieldModelBase) record.getFieldMap().get(column)).getValue()));
        if (this.dataService.deleteRecord(table.getId(), key) != 1) {
            throw new ApplicationException(this.dataService.getMessageText("DCS-903", new Object[0]));
        }
    }

    private String dateRangeCheck(String[] conditions, String validDatacols, Map<String, Object> data) {
        String validDate = conditions[conditions.length - 1];

        String end_date_value = "";
        String[] validData = Common.split(validDatacols, ",", true);
        boolean checkResult = true;

        if (validData.length > 1) {
            String start_date_value = data.get(validData[0]).toString();
            end_date_value = data.get(validData[1]).toString();
            if (start_date_value.compareTo(end_date_value) > 0) {
                checkResult = false;
            } else if (start_date_value.compareTo(validDate) > 0 || end_date_value.compareTo(validDate) < 0) {
                checkResult = false;
            }
        }

        String message = "";
        if (!checkResult) {
            String errorMsg = this.dataService.getMessageText("incorrect value", new Object[0]);
            message = this.dataService.getMessageText("Start_Date", new Object[0]);
            if (!end_date_value.isEmpty()) {
                message = message + "/" + this.dataService.getMessageText("End_Date", new Object[0]);
            }
            message = message + " " + errorMsg;
        }
        return message;
    }

    private boolean recursiveCheck(String tableName, Map<String, Object> data, String id, boolean firstFlag) {
        TableDescribe table = this.dataService.getTableDesc(tableName);
        String parent_id = data.get(table.getParent_id_column()).toString();

        if (parent_id.compareTo(id) == 0) {
            return false;
        }

        List<Map<String, Object>> records = recursiveSelect(tableName, data, firstFlag);
        if (firstFlag) {
            if (records.size() > 0) {
                return false;
            }

            if (!recursiveCheck(tableName, data, id, false)) {
                return false;
            }
        } else {
            for (Map<String, Object> record : records) {
                String parentId = record.get(table.getParent_id_column()).toString();
                if (parentId.compareTo(id) == 0) {
                    return false;
                }
                if (!recursiveCheck(tableName, record, id, false)) {
                    return false;
                }
            }
        }
        return true;
    }

    private List<Map<String, Object>> recursiveSelect(String tableName, Map<String, Object> data, boolean firstFlag) {
        Set<String> columnsNeeded = new HashSet<String>();
        Map<String, Object> valueMap = new HashMap<String, Object>();
        TableDescribe table = this.dataService.getTableDesc(tableName);

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (firstFlag) {
                for (String key : table.getIdColumns()) {
                    if (key.equals(entry.getKey())) {
                        valueMap.put(entry.getKey(), entry.getValue());
                        break;
                    }
                }
                continue;
            }
            if (((String) entry.getKey()).equals(table.getParent_id_column())) {
                valueMap.put(table.getChildren_id_column(), entry.getValue());
                continue;
            }
            if (!((String) entry.getKey()).equals(table.getChildren_id_column())) {
                for (String key : table.getIdColumns()) {
                    if (key.equals(entry.getKey())) {
                        valueMap.put(entry.getKey(), entry.getValue());

                        break;
                    }
                }
            }
        }
        String[] validData = Common.split(table.getValid_date_cols(), ",", true);
        for (String valid_date : validData) {
            valueMap.put(valid_date, data.get(valid_date));
        }

        String where = "";

        if (validData.length > 1) {
            String start_date = validData[0];
            String end_date = validData[1];
            String start_date_value = valueMap.get(start_date).toString();
            valueMap.remove(start_date);
            String end_date_value = valueMap.get(end_date).toString();
            valueMap.remove(end_date);

            where = "('" + start_date_value + "'" + " between " + tableName + "." + start_date + " and " + tableName
                    + "." + end_date + " or '" + end_date_value + "'" + " between " + tableName + "." + start_date
                    + " and " + tableName + "." + end_date + " or '" + start_date_value + "'" + " <= " + tableName + "."
                    + start_date + " and '" + end_date_value + "'" + " >= " + tableName + "." + end_date + ")";
        }

        Collections.addAll(columnsNeeded, table.getIdColumns());
        for (String valid_date : validData) {
            if (!columnsNeeded.contains(valid_date)) {
                columnsNeeded.add(valid_date);
            }
        }

        return this.dataService.selectRecordsByValues(tableName, columnsNeeded, valueMap, where);
    }

    private int updateRecord(String tableName, String validDate, String validDatacols, Map<String, Object> data) {
        TableDescribe table = this.dataService.getTableDesc(tableName);

        boolean hasValidData = false;
        HashSet<String> keyColumns = new HashSet<String>();
        for (String column : table.getIdColumns()) {
            if (!validDatacols.contains(column)) {
                keyColumns.add(column);
            } else {
                hasValidData = true;
            }
        }

        String where = null;
        if (hasValidData) {
            String[] validData = Common.split(validDatacols, ",", true);
            if (validData.length > 1) {
                where = "'" + validDate + "'" + " between " + validData[0] + " and " + validData[1];
            }
        }

        return this.dataService.updateRecord(tableName, data, null, keyColumns, where);
    }
}
