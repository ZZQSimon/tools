 package cn.com.easyerp.module.interfaces;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 
 
 
 
 
 
 
 
 
 @Controller
 @RequestMapping({"/export"})
 public class ExportController
   extends FormViewControllerBase
 {
   @Autowired
   private AuthService authService;
   @Autowired
   private ExtExportService exportService;
   
   @RequestMapping({"/export.view"})
   public ModelAndView view() {
     ExportsModel model = new ExportsModel(null, null);
     this.exportService.initModel(model);
     return new ModelAndView("exports", "form", model);
   }
 
 
 
 
   
   @ResponseBody
   @RequestMapping(value = {"/queryHead.view"}, method = {RequestMethod.POST})
   public ModelAndView headTable(@RequestBody Map<String, Object> params) {
     try {
       ExportsModel model = new ExportsModel(null, null);
       model.setHeadList(this.exportService.queryHead((String)params.get("typeexp"), (String)params.get("start"), (String)params.get("end"), (String)params.get("status")));
       model.settitleName(this.exportService.queryTitleName());
       
       return new ModelAndView("head", "form", model);
     } catch (Exception e) {
       e.printStackTrace();
       ExportsModel model = new ExportsModel(null, null);
       List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
       Map<String, Object> tmpMap = new HashMap<String, Object>();
       tmpMap.put("发生错误", "发生错误！，错误为：" + e.getMessage());
       tmpList.add(tmpMap);
       model.setHeadList(tmpList);
       return new ModelAndView("head", "form", model);
     } 
   }
 
 
 
 
 
   
   @ResponseBody
   @RequestMapping(value = {"/queryBody.view"}, method = {RequestMethod.POST})
   public ModelAndView bodyTable(@RequestBody Map<String, Object> params) {
     try {
       ExportsModel model = new ExportsModel(null, null);
       model.setBodyList(this.exportService.queryBody((String)params.get("typeexp"), (String)params.get("keyid")));
       model.settitleName(this.exportService.queryTitleName());
       
       return new ModelAndView("body", "form", model);
     } catch (Exception e) {
       e.printStackTrace();
       ExportsModel model = new ExportsModel(null, null);
       List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
       Map<String, Object> tmpMap = new HashMap<String, Object>();
       tmpMap.put("发生错误", "发生错误！，错误为：" + e.getMessage());
       tmpList.add(tmpMap);
       model.setBodyList(tmpList);
       return new ModelAndView("body", "form", model);
     } 
   }
 
 
 
 
 
 
 
   
   @ResponseBody
   @RequestMapping(value = {"/exportXml.do"}, method = {RequestMethod.POST})
   public ActionResult exportAction(@RequestBody Map<String, Object> params) throws Exception {
     try {
       String type = (String)params.get("typeexp");
       String start = (String)params.get("start");
       String end = (String)params.get("end");
       List<String> keyid = (List)params.get("keyid");
       return this.exportService.exportData(type, start, end, keyid);
     } catch (Exception e) {
       return new ActionResult(false, "发生错误！原因为：" + e.getMessage());
     } 
   }
 
 
 
 
 
 
 
 
 
   
   @ResponseBody
   @RequestMapping(value = {"/cancelStatus.do"}, method = {RequestMethod.POST})
   public ActionResult cancelAction(@RequestBody Map<String, Object> params) throws Exception {
     try {
       return this.exportService.cancelStatus((String)params.get("typeexp"), (List)params.get("keyid"));
     } catch (Exception e) {
       return new ActionResult(false, "发生错误！原因为：" + e.getMessage());
     } 
   }
 }


