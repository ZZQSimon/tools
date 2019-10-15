package cn.com.easyerp.report;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/report" })
public class ReportController extends FormViewControllerBase {
    @Autowired
    private DataService dataService;
    @Autowired
    @Qualifier("defaultReportService")
    private ReportService service;
    @Autowired
    private ReportCacheService reportCacheService;
    @Autowired
    private FilterService filterService;

    @RequestMapping({ "/report.view" })
    public ModelAndView view(@RequestBody ReportFormRequestModel data) {
        String report_id = data.getReport_id();
        ReportModel reportModel = (ReportModel) this.reportCacheService.getEntry(report_id);

        String title = this.dataService.getMessageText(report_id, new Object[0]);

        ReportService interceptor = reportModel.getService();
        if (interceptor == null)
            interceptor = this.service;
        ReportFormModel form = interceptor.form(data.getParent(), report_id, reportModel.getTable_id(), title, data);
        return buildModelAndView(form);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/pre_print.do" })
    public ActionResult pre_print(@RequestBody ReportFormRequestModel request, AuthDetails user, HttpServletRequest req)
            throws IOException {
        ReportModel report = (ReportModel) this.reportCacheService.getEntry(request.getReport_id());
        ReportService interceptor = report.getService();
        if (interceptor == null)
            interceptor = this.service;
        this.service.checkParam(request);
        return interceptor.print(report, this.filterService.toWhere(request.getFilter()), user, request);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/print.do" })
    public ActionResult print(@RequestBody ReportFormRequestModel request, AuthDetails user) throws IOException {
        ReportModel report = (ReportModel) this.reportCacheService.getEntry(request.getReport_id());
        ReportService interceptor = report.getService();
        if (interceptor == null) {
            interceptor = this.service;
        }
        return interceptor.print(report, this.filterService.toWhere(request.getFilter()), user, request);
    }

    @RequestMapping({ "/preview.do" })
    public ModelAndView preview(@RequestParam String viewUrl, @RequestParam String downloadUrl,
            HttpServletRequest request, HttpServletResponse res) {
        res.setCharacterEncoding("utf-8");
        ModelAndView mv = new ModelAndView("preview");
        if (viewUrl.indexOf(".\\") != -1 || viewUrl.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        if (downloadUrl.indexOf(".\\") != -1 || downloadUrl.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        request.setAttribute("viewUrl", viewUrl);
        request.setAttribute("downloadUrl", downloadUrl);
        return mv;
    }

    @RequestMapping({ "/fileview.do" })
    public ModelAndView fileview(@RequestParam String viewUrl, HttpServletRequest request) {
        ModelAndView mv = new ModelAndView("preview_file");
        if (viewUrl.indexOf(".\\") != -1 || viewUrl.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        request.setAttribute("viewUrl", viewUrl);
        return mv;
    }
}
