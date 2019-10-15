package cn.com.easyerp.batch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ImportFormModel;
import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.grid.ExportingGridModel;
import cn.com.easyerp.core.widget.grid.RecordModel;

@Controller
public class CsvImportController extends FormViewControllerBase {
    @Autowired
    private DataService dataService;

    @RequestMapping({ "/csv/import.view" })
    public ModelAndView view(@RequestBody FormRequestModelBase<CsvImportFormModel> request) {
        CsvImportFormModel form = (CsvImportFormModel) ViewService.fetchFormModel(request.getParent());
        BatchDescribe<CsvImportParamModel> batch = this.dataService.getBatch(form.getBatchId());
        TableDescribe table = this.dataService.getTableDesc(((CsvImportParamModel) batch.getData()).getTable());
        List<DatabaseDataMap> data = this.dataService.selectAllData(table, form.getTempTable());
        List<RecordModel> records = this.dataService.dumpFailedImportData(table, data);

        String tip = this.dataService.getMessageText("total_and_error",
                new Object[] { Integer.valueOf(data.size()), Integer.valueOf(records.size()) });
        ExportingGridModel grid = new ExportingGridModel(table.getId());
        grid.setRecords(records);
        ImportFormModel importForm = new ImportFormModel(request.getParent(), table.getId(), grid, null, data, tip,
                false);
        importForm.setImport_type(request.getImport_type());
        return buildModelAndView(importForm);
    }
}
