package cn.com.easyerp.ril;

import java.util.ArrayList;
import java.util.HashMap;
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

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.velocity.DxToolService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.complex.ComplexInputService;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.core.widget.grid.RecordModelWithTags;
import cn.com.easyerp.core.widget.grid.StdGridModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;

@Controller
@RequestMapping({ "/ril" })
public class ReferenceInputListController extends FormViewControllerBase {
    @Autowired
    private ViewService viewService;
    @Autowired
    private DataService dataService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private ComplexInputService complexInputService;
    @Autowired
    private DxToolService dxToolService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping({ "/table.view" })
    public ModelAndView view(@RequestBody ReferenceInputListFormRequestModel request) {
        StdGridModel grid = new StdGridModel(request.getTable());

        List<FieldModelBase> fields = this.dataService.buildEmptyDataRecord(request.getSource(), null);
        List<FieldModelBase> filters = new ArrayList<FieldModelBase>(request.getSourceFilters().size());
        List<String> llll = request.getSourceFilters();
        for (String name : llll) {
            for (int i = 0; i < fields.size(); i++) {
                if (((FieldModelBase) fields.get(i)).getColumn().equals(name))
                    filters.add(fields.remove(i));
            }
        }
        ReferenceInputListFormModel form = new ReferenceInputListFormModel(request, filters, grid);
        grid.setParent(form.getId());
        return buildModelAndView(form);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping({ "/list.do" })
    @ResponseBody
    public ActionResult list(@RequestBody ReferenceInputListFormRequestModel request) {
        ReferenceInputListFormModel form = (ReferenceInputListFormModel) ViewService.fetchFormModel(request.getId());
        List<Map<String, Object>> data = this.dataService.selectRecordsByValues(form.getSource(), null,
                this.viewService.mapDataToDB(request.getFilterData()));

        List<RecordModel> ret = new ArrayList<RecordModel>(data.size());
        Map<String, Object> fakeMap = new HashMap<String, Object>();
        Set<Map.Entry<String, String>> mapping = form.getMapping().entrySet();
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());

        String key = table.getIdColumns()[0];
        StdGridModel grid = form.getGrid();
        for (Map<String, Object> recordData : data) {
            fakeMap.clear();
            for (Map.Entry<String, String> map : mapping) {
                fakeMap.put(map.getKey(), recordData.get(map.getValue()));
            }
            List<FieldModelBase> fields = this.dataService.buildModel(table, fakeMap, false);
            Map<String, String> tagMap = new HashMap<String, String>();
            for (FieldModelBase field : fields) {
                ColumnDescribe desc = this.dataService.getColumnDesc(field);
                if (desc.isVirtual() || DataService.isSystemGroup(desc.getGroup_name())
                        || form.getMapping().containsKey(desc.getColumn_name()) || key.equals(desc.getColumn_name())) {
                    continue;
                }
                tagMap.put(field.getId(), this.dxToolService.field(field));
            }
            RecordModelWithTags record = new RecordModelWithTags(fields);
            ret.add(record);
            record.setParent(grid.getId());
            record.setFieldTags(tagMap);
        }
        grid.setRecords(ret);
        form.setFilterData(request.getFilterData());
        return new ActionResult(true, ret);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    @RequestMapping({ "/save.do" })
    @ResponseBody
    public ApiActionResult save(@RequestBody ReferenceInputListFormRequestModel request) {
        ReferenceInputListFormModel form = (ReferenceInputListFormModel) ViewService.fetchFormModel(request.getId());
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        List<String> msgs = new ArrayList<String>();

        Map<String, Map<String, Object>> mmap = request.getData();

        for (Map.Entry<String, Map<String, Object>> entry : mmap.entrySet()) {
            String rowId = (String) entry.getKey();
            DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB((Map) entry.getValue());
            RecordModelWithTags record = (RecordModelWithTags) ViewService.fetchWidgetModel(rowId);
            this.dataService.prepareAutoKey(table, databaseDataMap);
            this.complexInputService.insert(record.getFields(), (String) databaseDataMap.get(table.getIdColumns()[0]));
            ApiActionResult result = this.apiService.insertRecordWithTrigger(table, databaseDataMap, false, null, null);
            msgs.addAll(result.getDetails());
            if (!result.isSuccess()) {
                Common.rollback();
                return new ApiActionResult(result.getResult(), "Insert failed", msgs);
            }
        }
        return new ApiActionResult(0, this.dataService.getMessageText("Insert success", new Object[0]), msgs);
    }
}