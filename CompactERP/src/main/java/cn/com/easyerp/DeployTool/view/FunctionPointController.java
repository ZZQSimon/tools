 package cn.com.easyerp.DeployTool.view;
 
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

import cn.com.easyerp.DeployTool.dao.FunctionPointDao;
import cn.com.easyerp.DeployTool.service.FunctionPoint;
import cn.com.easyerp.DeployTool.service.Url;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 @Controller
 @RequestMapping({"/functionPoint"})
 public class FunctionPointController extends FormViewControllerBase {
   @Autowired
   private FunctionPointDao functionPointDao;
   
   @RequestMapping({"functionPoint.view"})
   public ModelAndView functionPoint(@RequestBody FunctionPointRequestModel request) {
     FunctionPointModel model = new FunctionPointModel(request.getParent());
     return buildModelAndView(model);
   }
 
 
 
 
   
   @RequestMapping({"/selectFunctionPoint.do"})
   @ResponseBody
   public ActionResult selectFunctionPoint() {
     Map<String, Object> map = new HashMap<String, Object>(); try {
       int count;
       List<FunctionPoint> list = this.functionPointDao.selectFunctionPoint();
       List<Url> urlList = this.functionPointDao.selectUrl();
       Map<String, Object> aMap = new HashMap<String, Object>();
       aMap.put("url_id", urlList);
       
       if (list == null) {
         count = 0;
       } else {
         count = list.size();
       } 
       map.put("aMap", aMap);
       map.put("count", Integer.valueOf(count));
       map.put("list", list);
     } catch (Exception e) {
       e.printStackTrace();
     } 
     return new ActionResult(true, map);
   }
 
 
 
 
   
   @RequestMapping(value = {"/selectDimFunctionPoint.do"}, method = {RequestMethod.POST})
   @ResponseBody
   public ActionResult selectDimFunctionPoint(@RequestBody FunctionPointRequestModel detail) {
     Map<String, Object> map = new HashMap<String, Object>();
     if (detail.getParams() != null || !detail.getParams().equals("")) {
       String params = detail.getParams();
       List<FunctionPoint> list = this.functionPointDao.selectDimFunctionPoint("%" + params + "%");
       map.put("list", list);
       if (list.size() > 0) {
         map.put("count", Integer.valueOf(list.size()));
       } else {
         map.put("count", Integer.valueOf(0));
       } 
     } 
     return new ActionResult(true, map);
   }
 
 
 
 
   
   @RequestMapping(value = {"/saveFunctionPoint.do"}, method = {RequestMethod.POST})
   @ResponseBody
   public ActionResult saveFunctionPoint(@RequestBody FunctionPointRequestModel detail) {
     boolean flag = true;
     try {
       List<FunctionPoint> list = new ArrayList<FunctionPoint>();
       
       if (detail.getInsert().size() > 0) {
         for (FunctionPoint point : detail.getInsert()) {
           FunctionPoint functionPoint = new FunctionPoint();
           functionPoint.setName(point.getName());
           functionPoint.setModule(point.getModule());
           functionPoint.setParam(point.getParam());
           functionPoint.setTable_id(point.getTable_id());
           functionPoint.setUrl_id(point.getUrl_id());
           list.add(functionPoint);
         } 
         this.functionPointDao.addFunctionPoint(list);
       
       }
       else if (detail.getUpdate().size() > 0) {
         for (FunctionPoint point : detail.getUpdate()) {
           FunctionPoint functionPoint = new FunctionPoint();
           functionPoint.setId(point.getId());
           functionPoint.setName(point.getName());
           functionPoint.setModule(point.getModule());
           functionPoint.setParam(point.getParam());
           functionPoint.setTable_id(point.getTable_id());
           functionPoint.setUrl_id(point.getUrl_id());
           list.add(functionPoint);
         } 
         this.functionPointDao.updateFunctionPointById(list);
       
       }
       else if (detail.getDeleted().size() > 0) {
         for (FunctionPoint point : detail.getDeleted()) {
           FunctionPoint functionPoint = new FunctionPoint();
           functionPoint.setId(point.getId());
           list.add(functionPoint);
         } 
         this.functionPointDao.deleteFunctionPoint(list);
       } 
       flag = true;
     } catch (Exception e) {
       e.printStackTrace();
       flag = false;
     } 
     return new ActionResult(true, Boolean.valueOf(flag));
   }
 }


