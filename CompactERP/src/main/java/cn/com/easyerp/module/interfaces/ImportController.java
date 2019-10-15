 package cn.com.easyerp.module.interfaces;
 
 import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.storage.StorageService;
 
 
 
 
 
 
 
 
 
 
 
 
 @Controller
 @RequestMapping({"/imports"})
 public class ImportController
   extends FormViewControllerBase
 {
   @Autowired
   private AuthService authService;
   @Autowired
   private ImportService importService;
   @Autowired
   private StorageService storageService;
   
   @RequestMapping({"/imports.view"})
   public ModelAndView view() {
     ImportsModel model = new ImportsModel(null, null);
     List<Map<String, Object>> result = this.importService.getImportTypeList();
     if (result == null) {
       result = new ArrayList<Map<String, Object>>();
     }
     model.setTypeList(result);
     return new ModelAndView("imports", "form", model);
   }
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping(value = {"/importXml.do"}, method = {RequestMethod.POST})
   public ActionResult importAction(@RequestBody Map<String, Object> params) {
     try {
       String type = (String)params.get("type");
       if (params.get("uuid") == null) {
         String data = (String)params.get("data");
         String tmp = this.importService.importXML(type, data);
         if (tmp == null) {
           return new ActionResult(false, "导入XML时发生错误！");
         }
         return new ActionResult(true, tmp);
       } 
 
       
       String uuid = (String)params.get("uuid");
       FileItem fileItem = this.storageService.getUploadFile(uuid);
       try {
         InputStream inputStream = fileItem.getInputStream();
         String tmp = this.importService.importXML(type, inputStream);
         return new ActionResult(true, tmp);
       } catch (Exception e) {
         e.printStackTrace();
         return new ActionResult(false, "导入XML时发生错误！");
       }
     
     } catch (Exception e) {
       e.printStackTrace();
       return new ActionResult(false, "出现错误！错误为：" + e.getMessage());
     } 
   }
 
   
   @ResponseBody
   @RequestMapping(value = {"/displayData.do"}, method = {RequestMethod.POST})
   public ActionResult displayData(@RequestBody Map<String, Object> params) {
     try {
       String type = (String)params.get("type");
       
       String html = this.importService.displayData(type);
       if (html != null) {
         return new ActionResult(true, html);
       }
       
       return new ActionResult(false, "读取时数据库发生错误！");
     }
     catch (Exception e) {
       e.printStackTrace();
       return new ActionResult(false, "出现错误！错误为：" + e.getMessage());
     } 
   }
 
   
   @ResponseBody
   @RequestMapping(value = {"/readXml.do"}, method = {RequestMethod.POST})
   public ActionResult readXML(@RequestBody Map<String, Object> params) {
     try {
       String uuid = (String)params.get("uuid");
       String type = (String)params.get("type");
       
       FileItem fileItem = this.storageService.getUploadFile(uuid);
       InputStream inputStream = null;
       try {
         inputStream = fileItem.getInputStream();
       } catch (Exception e) {
         e.printStackTrace();
         return new ActionResult(false, "读取xml文件时发生错误！");
       } 
       
       Map<String, Object> html = null;
       
       html = this.importService.readXML(type, inputStream);
       
       return new ActionResult(true, html);
     } catch (Exception e) {
       e.printStackTrace();
       return new ActionResult(false, "出现错误！错误为：" + e.getMessage());
     } 
   }
 }


