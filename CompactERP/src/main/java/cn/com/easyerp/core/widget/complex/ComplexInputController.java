package cn.com.easyerp.core.widget.complex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.ComplexColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.view.form.detail.DetailForm;
import cn.com.easyerp.core.view.form.detail.DetailFormService;
import cn.com.easyerp.core.view.form.detail.DetailRequestModel;
import cn.com.easyerp.core.widget.ComplexInputFieldModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;

@Controller
@RequestMapping({ "/widget/complex" })
public class ComplexInputController extends DetailForm {
    @Autowired
    private DataService dataService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private GridService gridService;
    @Autowired
    private DetailFormService builder;

    protected DetailFormService getBuilder() {
        return this.builder;
    }

    private List<RecordModel> buildRecords(List<Map<String, Object>> list, TableDescribe table,
            ComplexColumnDescribe complex, GridModel grid) {
        if (list == null)
            return null;
        List<RecordModel> records = new ArrayList<RecordModel>(list.size());
        String detailColumn = complex.getDetail_tbl_number_col();
        String detailTable = complex.getDetail_tbl();
        for (Map<String, Object> map : list) {
            List<FieldModelBase> fields = this.dataService.buildModel(table, map, true);
            if (!complex.extended) {

                Object amount = null;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    boolean found = false;
                    for (FieldModelBase field : fields) {
                        if (field.getColumn().equals(entry.getKey())) {
                            found = true;
                            break;
                        }
                    }
                    if (found)
                        continue;
                    amount = entry.getValue();
                }

                fields.add(new FieldModelBase(detailTable, detailColumn, amount));
            }
            RecordModel record = new RecordModel(fields);
            record.setParent(grid.getId());
            records.add(record);
        }
        return records;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<? extends RecordModel> initStd(ComplexInputRequestModel request, ComplexInputFieldModel field,
            ComplexColumnDescribe complex, GridModel grid) {
        TableDescribe baseTable = this.dataService.getTableDesc(complex.getBase_tbl());

        if (!request.isReload() && field.getDetails() != null) {
            return buildRecords(field.getDetails(), baseTable, complex, grid);
        }
        String sql = (request.getAction() == ActionType.create) ? complex.getIns_disp_sql() : complex.getUpd_disp_sql();
        List<Map<String, Object>> list = this.dataService.dynamicQueryList(sql, request.getParam());

        if (list.size() == 0)
            throw new ApplicationException(this.dataService.getMessageText("no reference data", new Object[0]));

        Set<String> resultColumns = ((Map) list.get(0)).keySet();
        List<String> columns = new ArrayList<String>(resultColumns.size());
        for (ColumnDescribe column : baseTable.getColumns()) {
            if (resultColumns.contains(column.getColumn_name()))
                columns.add(column.getColumn_name());
        }
        field.setColumnNames(columns);

        List<RecordModel> records = buildRecords(list, baseTable, complex, grid);

        field.setDetails(list);
        return records;
    }

    private List<? extends RecordModel> initExtended(ComplexInputRequestModel request, ComplexInputFieldModel field,
            ComplexColumnDescribe complex, GridModel grid) {
        TableDescribe detailTable = this.dataService.getTableDesc(complex.getDetail_tbl());

        if (field.getColumnNames() != null)
            return buildRecords(field.getDetails(), detailTable, complex, grid);
        String tableName = complex.getDetail_tbl();

        List<ColumnDescribe> columns = detailTable.getColumns();
        List<String> columnNames = new ArrayList<String>(columns.size() - 3);
        for (ColumnDescribe column : columns) {
            String name = column.getColumn_name();
            if (name.equals(complex.getDetail_tbl_pid_col()) || name.equals(complex.getDetail_tbl_pnm_col())
                    || name.equals(complex.getDetail_tbl_number_col()) || name.equals("is_old"))
                continue;
            columnNames.add(name);
        }
        field.setColumnNames(columnNames);

        if (request.getAction() == ActionType.edit) {
            List<Map<String, Object>> list = this.dataService.selectRecordsByValues(tableName, field.allColumnNames(),
                    request.getParam());
            field.setDetails(list);
        }
        return buildRecords(field.getDetails(), detailTable, complex, grid);
    }

    @RequestMapping({ "/dialog.view" })
    public ModelAndView dialog(@RequestBody ComplexInputRequestModel request) {
        String id = request.getParent();
        ComplexInputFieldModel field = (ComplexInputFieldModel) ViewService.fetchFieldModel(id);
        ColumnDescribe desc = this.dataService.getColumnDesc(field);
        ComplexColumnDescribe complex = this.dataService.getComplexColumn(desc.getComplex_id());

        String tableName = complex.getDetail_tbl();

        GridModel grid = new GridModel(tableName, null, true);
        grid.setStrict(true);
        if (complex.isExtended()) {
            grid.setRecords(initExtended(request, field, complex, grid));
        } else {
            grid.setRecords(initStd(request, field, complex, grid));
        }

        ComplexInputFormModel form = new ComplexInputFormModel(request.getAction(), id, desc.getTable_id(),
                desc.getColumn_name(), field.getColumnNames(), complex, grid);

        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/save.do" })
    public ActionResult save(@RequestBody ComplexInputRequestModel request) {
        ComplexInputFormModel form = (ComplexInputFormModel) ViewService.fetchFormModel(request.getId());
        ComplexInputFieldModel field = (ComplexInputFieldModel) ViewService.fetchFieldModel(form.getParent());
        GridModel grid = form.getGrid();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(grid.getRecords().size());
        ComplexColumnDescribe desc = form.getDesc();
        String detailColumn = desc.getDetail_tbl_number_col();
        for (RecordModel record : grid.getRecords()) {
            if (record.getStatus() == RecordModel.Status.deleted)
                continue;
            Map<String, Object> map = new HashMap<String, Object>(record.getFields().size());
            for (FieldModelBase f : record.getFields()) {
                String column = f.getColumn();
                if (!desc.isExtended() && column.equals(detailColumn)) {
                    map.put(column, request.getParam().get(record.getId()));
                    continue;
                }
                map.put(column, f.getValue());
            }
            list.add(map);
        }
        field.setDetails(list);
        return Common.ActionOk;
    }

    public ActionResult create(@RequestBody DetailRequestModel request, HttpServletRequest httpRequest,
            AuthDetails user) {
        GridModel grid = (GridModel) this.viewService.parents(request.getId(), 1);
        ComplexInputFieldModel field = (ComplexInputFieldModel) this.viewService.parents(grid.getParent(), 1);
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam(), field.allColumnNames());
        this.gridService.insert(grid, databaseDataMap);
        return Common.ActionOk;
    }

    public ActionResult edit(@RequestBody DetailRequestModel request, HttpServletRequest httpRequest,
            AuthDetails user) {
        RecordModel record = (RecordModel) this.viewService.parents(request.getId(), 1);
        GridModel grid = (GridModel) ViewService.fetchWidgetModel(record.getParent());
        ComplexInputFieldModel field = (ComplexInputFieldModel) this.viewService.parents(grid.getParent(), 1);
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam(), field.allColumnNames());
        this.gridService.update(grid, record, databaseDataMap);
        return Common.ActionOk;
    }
}
