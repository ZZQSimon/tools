 package cn.com.easyerp.module.interfaces;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 
 
 
 
 
 
 
 
 @Controller
 @RequestMapping({"/setting"})
 public class SettingController
   extends FormViewControllerBase
 {
   @Autowired
   private AuthService authService;
   @Autowired
   private DataService dataService;
   @Autowired
   private SettingService settingService;
   
   @RequestMapping({"/setting.view"})
   public ModelAndView view() {
     SettingModel model = new SettingModel(null, null);
     this.settingService.initModel(model);
     
     return new ModelAndView("setting", "form", model);
   }
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping(value = {"/displayContent.do"}, method = {RequestMethod.POST})
   public ActionResult importAction(@RequestBody Map<String, Object> params) {
     String type = (String)params.get("typeset");
     Map<String, List<Map<String, Object>>> result = null;
     try {
       result = this.settingService.getTypeContent(type);
     } catch (Exception e) {
       
       e.printStackTrace();
       return new ActionResult(true, e.getMessage());
     } 
     
     return new ActionResult(true, result);
   }
 
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping(value = {"/getUtype.do"}, method = {RequestMethod.POST})
   public ActionResult getucodeAction(@RequestBody Map<String, Object> params) {
     String type = (String)params.get("typeset");
     List<Map<String, Object>> result = null;
     try {
       result = this.settingService.getuTypeCode(type);
     } catch (Exception e) {
       
       e.printStackTrace();
       return new ActionResult(false, e.getMessage());
     } 
     
     return new ActionResult(true, result);
   }
 
 
 
 
 
   
   @ResponseBody
   @RequestMapping(value = {"/displayContent.view"}, method = {RequestMethod.POST})
   public ModelAndView listTable(@RequestBody Map<String, Object> params) {
     try {
       SettingModel model = new SettingModel(null, null);
       model.setSettingList(this.settingService.queryList((String)params.get("typeset")));
 
 
       
       return new ModelAndView("settinglist", "form", model);
     } catch (Exception e) {
       e.printStackTrace();
       ExportsModel model = new ExportsModel(null, null);
       List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>();
       Map<String, Object> tmpMap = new HashMap<String, Object>();
       tmpMap.put("发生错误", "发生错误！，错误为：" + e.getMessage());
       tmpList.add(tmpMap);
       model.setHeadList(tmpList);
       return new ModelAndView("settinglist", "form", model);
     } 
   }
 
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping(value = {"/update.do"}, method = {RequestMethod.POST})
   public ActionResult saveAction(@RequestBody Map<String, Object> params) {
     List dxCodes = (ArrayList)params.get("dx_code");
     List yyCodes = (ArrayList)params.get("yy_code");
     List sbCodes = (ArrayList)params.get("sb_code");
     String type = (String)params.get("dxtype");
     String utype = (String)params.get("utype");
     String tt = "";
     try {
       if (dxCodes.size() > 0) {
         for (int i = 0; i < dxCodes.size(); i++) {
           tt = tt + dxCodes.get(i).toString() + "@@@" + yyCodes.get(i).toString() + "@@@" + sbCodes.get(i).toString() + "&&&";
         }
         String tmp = this.settingService.updateSetting(type, utype, tt);
         return new ActionResult(true, tmp);
       } 
       return new ActionResult(false, "没有更新内容");
     }
     catch (Exception e) {
       e.printStackTrace();
       return new ActionResult(false, "出现错误！错误为：" + e.getMessage());
     } 
   }
 
 
 
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping(value = {"/searchCode.do"}, method = {RequestMethod.POST})
   public ActionResult searchCode(@RequestBody Map<String, Object> params) {
     String searchid = (String)params.get("searchid");
     String typeset = (String)params.get("typeset");
     List<Map<String, Object>> result = null;
     try {
       result = this.settingService.getsearchCode(searchid, typeset);
     } catch (Exception e) {
       
       e.printStackTrace();
       return new ActionResult(true, e.getMessage());
     } 
     
     return new ActionResult(true, result);
   }
 
 
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping(value = {"/searchSBinfolv1.do"}, method = {RequestMethod.POST})
   public ActionResult searchSBinfolv1(@RequestBody Map<String, Object> params) {
     String searchid = (String)params.get("searchid");
     String typeset = (String)params.get("typeset");
     List<Map<String, Object>> result = null;
     try {
       result = this.settingService.querySubjectlv1(searchid, typeset);
       if (result.size() == 0) {
         return new ActionResult(false, result);
       }
       return new ActionResult(true, result);
     }
     catch (Exception e) {
       
       e.printStackTrace();
       return new ActionResult(false, e.getMessage());
     } 
   }
 
 
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping(value = {"/searchSBinfo.do"}, method = {RequestMethod.POST})
   public ActionResult searchSBinfo(@RequestBody Map<String, Object> params) {
     String searchid = (String)params.get("searchid");
     String typeset = (String)params.get("typeset");
     List<Map<String, Object>> result = null;
     try {
       result = this.settingService.querySubject(searchid, typeset);
       System.out.println(result.size());
       return new ActionResult(true, result);
     }
     catch (Exception e) {
       
       e.printStackTrace();
       return new ActionResult(false, e.getMessage());
     } 
   }
 }


