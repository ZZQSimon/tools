 package cn.com.easyerp.DeployTool.view;
 
 import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.ImportDeployDao;
import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.DeployTool.service.ImportDeployService;
import cn.com.easyerp.DeployTool.service.TableDeployService;
import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 
 
 
 
 @Controller
 @RequestMapping({"/importDeploy"})
 public class ImportDeployController
   extends FormViewControllerBase
 {
   @Autowired
   private ImportDeployDao importDeployDao;
   @Autowired
   private ImportDeployService importDeployService;
   @Autowired
   private TableDeployDao tableDeployDao;
   @Autowired
   private TableDeployService tableDeployService;
   @Autowired
   private CacheService cacheService;
   @Autowired
   private DataService dataService;
   
   @RequestMapping({"/importDeploy.view"})
   public ModelAndView url(@RequestBody ImportDeployRequestModel request) {
     ImportDeployModel form = new ImportDeployModel(request.getParent());
     return buildModelAndView(form);
   }
   
   @RequestMapping({"/getImportDeploy.do"})
   @ResponseBody
   public ActionResult getImoprtDeploy(@RequestBody ImportDeployRequestModel request) {
     String result = "get ImoprtDeploy fail";
     try {
       ImportDeployModel idm = this.importDeployService.getImportDeploy(request.getImportDeploy().getTable_id());
       return new ActionResult(true, idm);
     } catch (Exception e) {
       e.printStackTrace();
       return new ActionResult(false, result);
     } 
   }
 
 
   
   @ResponseBody
   @RequestMapping(value = {"/upload.do"}, method = {RequestMethod.POST})
   public ActionResult upload(HttpServletRequest req) throws Exception { return new ActionResult(true, this.importDeployService.process(req)); }
 
 
   
   @ResponseBody
   @RequestMapping({"/saveImportDeploy.do"})
   @Transactional
   public ActionResult saveImportDeploy(@RequestBody ImportDeployRequestModel request) {
     this.importDeployService.saveImportDeploy(request);
     try {
		this.importDeployService.createImportTemplate(request);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
     return new ActionResult(true, "save importDeploy succeed!");
   }
   
   @RequestMapping({"/selectTableDeploy.do"})
   @ResponseBody
   public ActionResult selectTableDeploy(AuthDetails auth) {
     List<TreeNode> list = new ArrayList<TreeNode>();
     
     List<Map<String, String>> maps = this.importDeployDao.selectBasicDeploy(auth.getLanguage_id());
     list.add(new TreeNode("5", "deploy_base_table", "#", getBasic(maps, "deploy_base_table")));
     list.add(new TreeNode("4", "deploy_professional_table", "#", getBasic(maps, "deploy_professional_table")));
 
     
     Map<String, TableDescribe> tableDescCache = this.cacheService.getTableDescCache();
     for (Map.Entry<String, TableDescribe> entry : tableDescCache.entrySet()) {
       TreeNode node = new TreeNode();
       
       if (((TableDescribe)entry.getValue()).getTable_type() == 5) {
         TableDescribe parentTable = this.cacheService.getTableDesc(((TableDescribe)entry.getValue()).getParent_id());
         if (parentTable == null || parentTable.getTable_type() != 5) {
           node.setId((String)entry.getKey() + "_5");
           node.setParent("5");
           node.setTable_id((String)entry.getKey());
           node.setText(this.dataService.i18nString(((TableDescribe)entry.getValue()).getI18n()));
         } else {
           node.setId((String)entry.getKey() + "_5");
           node.setParent(parentTable.getId() + "_5");
           node.setTable_id((String)entry.getKey());
           node.setText(this.dataService.i18nString(((TableDescribe)entry.getValue()).getI18n()));
         } 
       } else if (((TableDescribe)entry.getValue()).getTable_type() == 4) {
         TableDescribe parentTable = this.cacheService.getTableDesc(((TableDescribe)entry.getValue()).getParent_id());
         if (parentTable == null || parentTable.getTable_type() != 4) {
           node.setId((String)entry.getKey() + "_4");
           node.setParent("4");
           node.setTable_id((String)entry.getKey());
           node.setText(this.dataService.i18nString(((TableDescribe)entry.getValue()).getI18n()));
         } else {
           node.setId((String)entry.getKey() + "_4");
           node.setParent(parentTable.getId() + "_4");
           node.setTable_id((String)entry.getKey());
           node.setText(this.dataService.i18nString(((TableDescribe)entry.getValue()).getI18n()));
         } 
       } 
       
       if (node.getId() != null && !"".equals(node.getId())) {
         list.add(node);
       }
     } 
     return new ActionResult(true, list);
   }
 
   
   private String getBasic(List<Map<String, String>> maps, String str) {
     for (int i = 0; i < maps.size(); i++) {
       if (str.equals(((Map)maps.get(i)).get("id"))) {
         return (String)((Map)maps.get(i)).get("lan");
       }
     } 
     return "";
   }
   
   private String getType(String type) {
     String t = "";
     switch (type) {
       case "c_":
         t = "deploy_sys_table";
         break;
       case "m_":
         t = "deploy_base_table";
         break;
       case "t_":
         t = "deploy_professional_table";
         break;
       case "v_":
         t = "deploy_view_table";
         break;
       case "a_":
         t = "deploy_api_table";
         break;
     } 
 
 
     
     return t;
   }
 }


