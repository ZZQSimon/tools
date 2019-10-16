package cn.com.easyerp.generate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.AutoGenTableDesc;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.velocity.DxToolService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.view.TableFormRequestModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.complex.ComplexInputService;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.core.widget.grid.RecordModelWithTags;
import cn.com.easyerp.core.widget.grid.StdGridModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.ril.ReferenceInputListFormRequestModel;

@Controller
@RequestMapping({ "/ag" })
public class AutoGenerateController extends FormViewControllerBase {
    @Autowired
    private DataService dataService;
    @Autowired
    private ViewService viewService;

    @SuppressWarnings("rawtypes")
    @RequestMapping({ "/dialog.view" })
    public ModelAndView dialog(@RequestBody TableFormRequestModel request) {
        StdGridModel list = (StdGridModel) ViewService.fetchWidgetModel(request.getParent());
        String tableName = list.getTable();
        TableDescribe table = this.dataService.getTableDesc(tableName);
        AutoGenTableDesc ag = table.getAutoGen(request.getId());
        if (ag.getMode() == AutoGenTableDesc.AutoGenMode.single) {
            return single(ag, request.getParent());
        }
        return batch(table, ag, request.getParent());
    }

    @Autowired
    private GridService gridService;
    @Autowired
    private ComplexInputService complexInputService;
    @Autowired
    private DxToolService dxToolService;

    public ModelAndView batch(TableDescribe table, AutoGenTableDesc ag, String parent) {
        AutoGenerateFilterModel filter = new AutoGenerateFilterModel(ag);

        StdGridModel grid = new StdGridModel(table.getId());

        boolean hasSum = false;
        List<String> columns = new ArrayList<String>();
        TableDescribe view = this.dataService.getTableDesc(ag.getRef_view());
        Set<String> inputs = new HashSet<String>();
        for (ColumnDescribe column : table.getColumns()) {
            if (!hasSum && column.getSum_flag() != null)
                hasSum = true;
            String columnName = column.getColumn_name();
            if ("_SYS_COLUMN".equals(column.getGroup_name()) || table.getIdColumns()[0].equals(columnName)
                    || column.getData_type() == 13)
                continue;
            columns.add(columnName);
            if (view.getColumn(columnName) != null && !view.getColumn(columnName).isRo_insert()) {
                inputs.add(columnName);
            }
        }

        AutoGenerateFormModel form = new AutoGenerateFormModel(parent, ag.getRef_view(), ag, filter, grid, hasSum,
                columns, inputs);

        return buildModelAndView(form);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping({ "/list.do" })
    @ResponseBody
    public ActionResult list(@RequestBody AutoGenerateFormRequestModel request) {
        AutoGenerateFormModel form = (AutoGenerateFormModel) ViewService.fetchFormModel(request.getId());
        TableDescribe view = this.dataService.getTableDesc(form.getTableName());
        TableDescribe table = this.dataService.getTableDesc(form.getGrid().getTable());
        TableDescribe parentTable = this.dataService.getTableDesc(table.getParent_id());
        Map<String, Object> filterData = request.getFilterData();
        for (Map.Entry<String, Object> entry : filterData.entrySet()) {
            ColumnDescribe column;
            String name = (String) entry.getKey();

            if (name.startsWith("_parent.")) {
                name = name.substring(8);
                column = parentTable.getColumn(name);
            } else {

                if (name.endsWith(".to") || name.endsWith(".from"))
                    name = Common.split(name, ".")[0];
                column = view.getColumn(name);
            }
            entry.setValue(ViewService.convertToDBValue(column, entry.getValue()));
        }
        List<Map<String, Object>> data = this.dataService.dynamicQueryList(form.getAg().getGen_sql(),
                request.getFilterData());

        List<RecordModel> ret = new ArrayList<RecordModel>(data.size());
        StdGridModel grid = form.getGrid();
        for (Map<String, Object> recordData : data) {

            List<FieldModelBase> fields = this.dataService.buildModel(table, recordData, false);

            Map<String, String> tagMap = new HashMap<String, String>();
            for (FieldModelBase field : fields) {

                if (form.getInputs().contains(field.getColumn()))
                    tagMap.put(field.getId(), dxToolService.field(field, form));
            }
            RecordModelWithTags record = new RecordModelWithTags(fields);
            ret.add(record);
            record.setParent(grid.getId());
            record.setFieldTags(tagMap);
        }
        grid.setRecords(ret);
        return new ActionResult(true, ret);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping({ "/save.do" })
    @ResponseBody
    public ActionResult save(@RequestBody ReferenceInputListFormRequestModel request) {
        AutoGenerateFormModel form = (AutoGenerateFormModel) ViewService.fetchFormModel(request.getId());
        GridModel parentGrid = (GridModel) ViewService.fetchWidgetModel(form.getParent());
        Map<String, Map<String, Object>> data = request.getData();
        for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
            RecordModel record = this.gridService.insert(parentGrid, (Map) entry.getValue(), true);

            this.complexInputService.saveToRecord(record, ((Map) entry.getValue()).keySet());
        }
        return Common.ActionOk;
    }

    public ModelAndView single(AutoGenTableDesc ag, String parent) {
        String view = ag.getRef_view();
        List<FieldModelBase> fields = this.dataService.buildEmptyDataRecord(view, null);
        AutoGenerateSingleModeFormModel form = new AutoGenerateSingleModeFormModel(parent, view, fields, ag);

        return buildModelAndView(form);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping({ "/single/create.do" })
    @ResponseBody
    public ActionResult singleCreate(@RequestBody AutoGenerateSingleModeFormRequestModel request) {
        AutoGenerateSingleModeFormModel form = (AutoGenerateSingleModeFormModel) request.getWidget();
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        DatabaseDataMap dbData = this.viewService.convertToDBValues(table, request.getParam());
        List<Map<String, Object>> list = this.dataService.dynamicQueryList(form.getAg().getGen_sql(), dbData);
        if (list.size() == 0)
            return new ActionResult(false, this.dataService.getMessageText("no_auto_gen_data", new Object[0]));
        if (list.size() > 1)
            return new ActionResult(false, this.dataService.getMessageText("too_much_auto_gen_data", new Object[0]));
        GridModel grid = (GridModel) ViewService.fetchWidgetModel(form.getParent());
        this.gridService.insert(grid, (Map) list.get(0));
        return Common.ActionOk;
    }
}
