 package cn.com.easyerp.DeployTool.view;
 
 import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.service.ChartReportService;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 @Controller
 @RequestMapping({"/chartReport"})
 public class ChartReportController
   extends FormViewControllerBase {
   @RequestMapping({"/chartReport.view"})
   public ModelAndView dictionary(@RequestBody ChartReportRequestModel request) {
     ChartReportModel form = new ChartReportModel(request.getParent());
     form.setChartReports(this.reportService.getReportsByType(request.getType()));
     form.setType(request.getType());
     return buildModelAndView(form);
   } @Autowired
   private ChartReportService reportService;
   @ResponseBody
   @RequestMapping({"/saveReport.do"})
   public ActionResult saveReport(@RequestBody ChartReportRequestModel request, AuthDetails user) {
     Map<String, Object> result = new HashMap<String, Object>();
     result.put("i18nName", request.getReport().getInternational_id());
     ReportModel report = this.reportService.saveReport(request.getReport(), user);
     result.put("ret", "succeed");
     result.put("msg", "Saved successfully!");
     result.put("report", report);
     result.put("chartReports", this.reportService.getReportsByType(request.getType()));
     return new ActionResult(true, result);
   }
 
   
   @ResponseBody
   @RequestMapping({"/deleteReport.do"})
   public ActionResult deleteReport(@RequestBody ChartReportRequestModel request) {
     Map<String, Object> result = new HashMap<String, Object>();
     
     this.reportService.deleteReport(request.getReport());
     result.put("ret", "succeed");
     result.put("msg", "Delete success");
     result.put("chartReports", this.reportService.getReportsByType(request.getType()));
     return new ActionResult(true, result);
   }
 }


