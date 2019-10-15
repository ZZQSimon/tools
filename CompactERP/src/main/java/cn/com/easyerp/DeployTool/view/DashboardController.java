 package cn.com.easyerp.DeployTool.view;
 
 import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.service.DashboardDescribe;
import cn.com.easyerp.DeployTool.service.DashboardService;
import cn.com.easyerp.DeployTool.service.SubscribeDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.chart.ChartService;
import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.report.ReportCacheService;
import cn.com.easyerp.storage.StorageService;
 
 
 
 
 
 
 
 
 
 
 
 
 
 @Controller
 @RequestMapping({"/dashboard"})
 public class DashboardController
   extends FormViewControllerBase
 {
   @Autowired
   private DashboardService dashService;
   @Autowired
   private ReportCacheService reportCacheService;
   @Autowired
   private ChartService chartService;
   @Autowired
   private DataService dataService;
   @Autowired
   private CacheService cacheService;
   @Autowired
   private StorageService service;
   
   @RequestMapping({"/dashboard.view"})
   public ModelAndView url(@RequestBody ImportDeployRequestModel request) {
     DashboardModel form = new DashboardModel(request.getParent());
     return buildModelAndView(form);
   }
 
   
   @RequestMapping(value = {"/saveDashboard.do"}, method = {RequestMethod.POST})
   @ResponseBody
   public ActionResult saveDashboard(@RequestBody DashboardRequestModel req) {
     DashboardDescribe dash = req.getDashboard();
     int exists = this.dashService.isExistsDashboard(dash);
     if (exists > 0) {
       this.dashService.updateDashboard(dash);
     } else {
       this.dashService.saveDashBoard(dash);
     } 
     return new ActionResult(true, "dashboard save succeed");
   }
   
   @RequestMapping(value = {"/getDashboards.do"}, method = {RequestMethod.POST})
   @ResponseBody
   public ActionResult getDashboards(@RequestBody DashboardRequestModel req) {
     List<DashboardDescribe> list = this.dashService.getDashboards();
     return new ActionResult(true, list);
   }
   
   @RequestMapping({"/getTableData.do"})
   @ResponseBody
   public ActionResult getTableData(@RequestBody DashboardRequestModel req) {
     Map<String, Object> data = new HashMap<String, Object>();
     String table = req.getTable();
     List<DatabaseDataMap> list = this.dashService.getTableList(table, req.getCondition().trim(), req.getOrderBy(), req.getDefaultCount());
     
     data.put("list", list);
     data.put("count", Integer.valueOf(list.size()));
     return new ActionResult(true, data);
   }
   
   @RequestMapping({"/getChartList.do"})
   @ResponseBody
   public ActionResult getChartList(@RequestBody DashboardRequestModel req) {
     List<DatabaseDataMap> list = this.dashService.getChartList();
     return new ActionResult(true, list);
   }
   
   @RequestMapping({"/getChartData.do"})
   @ResponseBody
   public ActionResult getChartData(@RequestBody DashboardRequestModel req) {
     ReportModel report = this.reportCacheService.getChartById(req.getReport_id());
     if (report.getCount_column() != null && !"".equals(report.getCount_column())) {
       report.setReport_column(report.getCount_column());
     }
     Map chartData = this.chartService.getChartData(report, req.getCondition());
     if (chartData == null) {
       chartData.put("msg", this.dataService.getMessageText("No_Data_Now", new Object[0]));
       chartData.put("ret", "false");
       return new ActionResult(false, chartData);
     } 
     chartData.put("msg", this.dataService.getMessageText("No_Data_Now", new Object[0]));
     chartData.put("ret", "true");
     return new ActionResult(true, chartData);
   }
 
   
   @RequestMapping({"/getSubscribes.do"})
   @ResponseBody
   public ActionResult getSubscribes(@RequestBody DashboardRequestModel req) {
     String subscriber = AuthService.getCurrentUser().getId();
     List<DatabaseDataMap> list = this.dashService.getSubscribesBysubscriber(subscriber);
     if (list != null && list.size() != 0)
       for (int i = 0; i < list.size(); i++) {
         if (((DatabaseDataMap)list.get(i)).get("dashboard_id") != null)
         {
           
           if ("9".equals(((DatabaseDataMap)list.get(i)).get("dashboard_type"))) {
             this.dashService.buildDashMenu((DatabaseDataMap)list.get(i));
           } else {
             
             Map<String, Object> dashboard = this.cacheService.getDashboard(((DatabaseDataMap)list.get(i)).get("dashboard_id").toString());
             if (dashboard != null)
             {
               ((DatabaseDataMap)list.get(i)).put("dashboard_param", dashboard.get("dashboard_param")); } 
           } 
         }
       }  
     return new ActionResult(true, list);
   }
 
   
   @RequestMapping({"/updateSubscribes.do"})
   @ResponseBody
   public ActionResult updateSubscribes(@RequestBody DashboardRequestModel req) {
     AuthDetails subscriber = AuthService.getCurrentUser();
     List<SubscribeDescribe> subscribes = req.getSubscribes();
     boolean succeed = this.dashService.updateSubscribes(subscribes, subscriber);
     return new ActionResult(succeed, "Saved successfully!");
   }
   
   @RequestMapping({"/updateSubscribesSeq.do"})
   @ResponseBody
   public ActionResult updateSubscribesSeq(@RequestBody DashboardRequestModel req) {
     String subscriber = AuthService.getCurrentUser().getId();
     List<SubscribeDescribe> subscribes = req.getSubscribes();
     boolean succeed = this.dashService.updateSubscribesSeq(subscribes, subscriber);
     return new ActionResult(succeed, "");
   }
   
   @RequestMapping({"/getHomeSubscribes.do"})
   @ResponseBody
   public ActionResult getHomeSubscribes(@RequestBody DashboardRequestModel req) {
     String subscriber = AuthService.getCurrentUser().getId();
     List<DatabaseDataMap> list = this.dashService.getHomeSubscribes(subscriber);
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
     
     DatabaseDataMap map = new DatabaseDataMap();
     map.put("dashboard_id", "1");
     map.put("dashboard_name", "帮助文档");
     map.put("dashboard_size", "1");
     map.put("dashboard_type", "8");
     map.put("dashboard_title", "帮助文档");
     map.put("dashboard_richtext", "帮助文档");
     map.put("subscribe_id", "帮助文档");
     list.add(map);
     
     DatabaseDataMap map3 = new DatabaseDataMap();
     map3.put("dashboard_id", "3");
     map3.put("dashboard_name", "订阅仪表盘");
     map3.put("dashboard_size", "1");
     map3.put("dashboard_type", "10");
     map3.put("dashboard_title", "订阅仪表盘");
     map3.put("dashboard_richtext", "订阅仪表盘");
     map3.put("subscribe_id", "订阅仪表盘");
     list.add(map3);
     
     return new ActionResult(true, list);
   }
   
   @RequestMapping({"/downloadHelpDocumentation.do"})
   public void downloadHelpDocumentation(HttpServletResponse response) {
     String upload_root = this.cacheService.getSystemParam().getUpload_root();
     String filePath = upload_root + "/Adidas HRIS User Guide_Store Manager.pdf";
     try {
       File file = new File(filePath);
       
       String filename = file.getName();
       
       String ext = filename.substring(filename.lastIndexOf(".") + 1);
       
       InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
       byte[] buffer = new byte[fis.available()];
       fis.read(buffer);
       fis.close();
       
       response.reset();
       
       response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
       response.addHeader("Content-Length", "" + file.length());
       OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
       response.setContentType("application/octet-stream");
       toClient.write(buffer);
       toClient.flush();
       toClient.close();
     } catch (Exception ex) {
       ex.printStackTrace();
     } 
   }
   @RequestMapping({"/deleteDashboard.do"})
   @ResponseBody
   public ActionResult deleteDashboard(@RequestBody DashboardRequestModel req) {
     String dashboard_id = req.getDashboard().getDashboard_id();
     this.dashService.deleteDashboard(dashboard_id);
     return new ActionResult(true, "Delete the success");
   }
   
   @RequestMapping({"/cancelSubscribe.do"})
   @ResponseBody
   public ActionResult cancelSubscribe(@RequestBody DashboardRequestModel req) {
     String subscribe_id = req.getDashboard().getSubscribe_id();
     this.dashService.cancelSubscribe(subscribe_id);
     return new ActionResult(true, "Delete the success");
   }
   
   @RequestMapping({"/ifSubscribe.do"})
   @ResponseBody
   public ActionResult ifSubscribe(@RequestBody DashboardRequestModel req) {
     String subscribe_id = req.getDashboard().getSubscribe_id();
     this.dashService.cancelSubscribe(subscribe_id);
     return new ActionResult(true, "Delete the success");
   }
 
   
   @ResponseBody
   @RequestMapping({"/download.do"})
   public ActionResult download(@RequestBody WidgetRequestModelBase request, AuthDetails user) throws Exception {
     File file = new File(this.service.absolutePath(request.getId()));
     String fname = file.getName();
     String str = fname.substring(fname.indexOf(".") + 1);
     Date dates = new Date();
     String name = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + user.getId() + "." + str;
     try {
       IOUtils.copy(new FileInputStream(file), new FileOutputStream(new File(this.dataService.getRootPath() + "/tmp/" + name)));
     } catch (Exception e) {
       e.printStackTrace();
     } 
     String viewPath = this.service.getStorageFilePath(request.getId());
     return this.service.createDownload(file.getName(), "/tmp/" + name, new FileInputStream(file), (int)file.length());
   }
 }


