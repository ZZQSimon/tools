 package cn.com.easyerp.core.widget.grid;
 
 import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 
 @Controller
 @RequestMapping({"/widget/grid"})
 public class UserColumnController
   extends FormViewControllerBase
 {
   @Autowired
   private UserColumnService userColumnService;
   
   @RequestMapping({"userColumn.view"})
   public ModelAndView userColumn(@RequestBody UserColumnRequestModel request, AuthDetails user) {
     UserColumnModel form = new UserColumnModel(request.getParent());
     TableDescribe tableDesc = this.cacheService.getTableDesc(request.getTableName());
     List<UserColumn> userColumn = this.userColumnService.getUserColumn(user, tableDesc);
     if (userColumn != null && userColumn.size() != 0)
       form.setUserColumns(userColumn); 
     form.setTable(request.getTableName());
     return buildModelAndView(form); } @Autowired
   private DataService dataService; @Autowired
   private CacheService cacheService; @ResponseBody
   @RequestMapping({"saveUserColumn.do"})
   public ActionResult saveUserColumn(@RequestBody UserColumnRequestModel request, AuthDetails user) {
     TableDescribe tableDesc = this.cacheService.getTableDesc(request.getTableName());
     this.userColumnService.deleteUserColumn(tableDesc, user, request.getUserColumns());
     if (request.getUserColumns() != null && request.getUserColumns().size() != 0)
       this.userColumnService.insertUserColumn(request.getUserColumns(), user); 
     return new ActionResult(true, "success");
   }
 }


