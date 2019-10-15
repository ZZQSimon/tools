 package cn.com.easyerp.core.velocity;
 
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.easyerp.framework.common.ActionResult;
 
 
 
 
 
 
 
 @Controller
 @RequestMapping({"/vm"})
 public class DxToolController
 {
   @Autowired
   DxToolService service;
   
   @RequestMapping({"/form/{id}.do"})
   @ResponseBody
   public ActionResult form(@PathVariable("id") String id) { return new ActionResult(true, this.service.getCachedFormModel(id)); }
 }


