package cn.com.easyerp.chart;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.report.ReportCacheService;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Controller
@RequestMapping({ "/chart" })
public class ChartController extends FormViewControllerBase {
    @Autowired
    private DataService dataService;
    @Autowired
    private ReportCacheService reportCacheService;
    @Autowired
    private ChartService chartService;
    // private ChartShowModel csm;

    @RequestMapping({ "/chart.view" })
    public ModelAndView view(@RequestBody ChartFormRequestModel data) {
        String report_id = data.getReport_id();

        ReportModel report = (ReportModel) this.reportCacheService.getEntry(report_id);
        WidgetModelBase wmb = new WidgetModelBase();

        ChartFormModel form = new ChartFormModel(data.getParent(), report_id, wmb, report.getTable_id());
        form.getFilter().setFixedData(data.getFixedData());
        return buildModelAndView(form);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/filter/{form_id}.do" })
    public ActionResult show(@RequestBody Map<String, String> param, @PathVariable String form_id) {
        ReportModel report = this.reportCacheService.getChartById((String) param.get("report_id"));
        if (!Common.isBlank(report.getCount_column())) {
            report.setReport_column(report.getCount_column());
        }
        Map<String, Object> returnData = this.chartService.getChartData(report, (String) param.get("where"));
        if (!returnData.containsKey("series")) {
            returnData.put("msg", this.dataService.getMessageText("No_Data_Now", new Object[0]));
            returnData.put("ret", "false");
            return new ActionResult(true, returnData);
        }
        returnData.put("divId", param.get("div_id"));
        returnData.put("ret", "true");
        return new ActionResult(true, returnData);
    }
}
