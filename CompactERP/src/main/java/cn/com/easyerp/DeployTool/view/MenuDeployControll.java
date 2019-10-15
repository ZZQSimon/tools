 package cn.com.easyerp.DeployTool.view;
 
 import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.MenuDeployDao;
import cn.com.easyerp.DeployTool.service.MenuDeployDetails;
import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.menu.PageModel;
import cn.com.easyerp.framework.common.ActionResult;
 
 @Controller
 @RequestMapping({"/menuDeploy"})
 public class MenuDeployControll extends FormViewControllerBase {
   @Autowired
   private MenuDeployDao menuDeployDao;
   @Autowired
   private CacheService cacheService;
   
   @RequestMapping({"/menuDeploy.view"})
   public ModelAndView menuDeploy(@RequestBody MenuDeployRequestModel request) {
     MenuDeployModel form = new MenuDeployModel(request.getParent());
     return buildModelAndView(form);
   }
   
   @ResponseBody
   @RequestMapping({"/initMenuDeploy.do"})
   public ActionResult initMenuDeploy(AuthDetails user) {
     List<MenuDeployDetails> menuDeployList = this.menuDeployDao.selectMenu(user.getId());
     List<TreeNode> treeData = new ArrayList<TreeNode>();
     TreeNode mobileNode = new TreeNode();
     mobileNode.setParent("#");
     mobileNode.setId("mobileMenu");
     mobileNode.setData("mobileMenu");
     treeData.add(mobileNode);
     TreeNode PCNode = new TreeNode();
     PCNode.setParent("#");
     PCNode.setId("PCMenu");
     PCNode.setData("PCMenu");
     switch (user.getLanguage_id()) {
       case "cn":
         mobileNode.setText("手机菜单");
         PCNode.setText("PC菜单");
         break;
       case "en":
         mobileNode.setText("MobileMenu");
         PCNode.setText("PCMenu");
         break;
       case "jp":
         mobileNode.setText("");
         PCNode.setText("");
         break;
       case "other1":
         mobileNode.setText("");
         PCNode.setText("");
         break;
       case "other2":
         mobileNode.setText("");
         PCNode.setText("");
         break;
     } 
     
     treeData.add(PCNode);
     for (MenuDeployDetails menuDeployDetails : menuDeployList) {
       TreeNode node = new TreeNode();
       if (menuDeployDetails.getParent_id() == null || "".equals(menuDeployDetails.getParent_id())) {
         if (menuDeployDetails.getIs_mobile_menu() == 1) {
           node.setParent("mobileMenu");
         } else {
           node.setParent("PCMenu");
         } 
       } else {
         node.setParent(menuDeployDetails.getParent_id());
       } 
       
       node.setId(menuDeployDetails.getId());
       switch (AuthService.getCurrentUser().getLanguage_id()) {
         case "cn":
           if ("".equals(menuDeployDetails.getCn()) || menuDeployDetails.getCn() == null) {
             node.setText(getDefaultLanguageText(menuDeployDetails)); break;
           } 
           node.setText(menuDeployDetails.getCn());
           break;
         
         case "en":
           if ("".equals(menuDeployDetails.getEn()) || menuDeployDetails.getEn() == null) {
             node.setText(getDefaultLanguageText(menuDeployDetails)); break;
           } 
           node.setText(menuDeployDetails.getEn());
           break;
         
         case "jp":
           if ("".equals(menuDeployDetails.getJp()) || menuDeployDetails.getJp() == null) {
             node.setText(getDefaultLanguageText(menuDeployDetails)); break;
           } 
           node.setText(menuDeployDetails.getJp());
           break;
         
         case "other1":
           if ("".equals(menuDeployDetails.getOther1()) || menuDeployDetails.getOther1() == null) {
             node.setText(getDefaultLanguageText(menuDeployDetails)); break;
           } 
           node.setText(menuDeployDetails.getOther1());
           break;
         
         case "other2":
           if ("".equals(menuDeployDetails.getOther2()) || menuDeployDetails.getOther2() == null) {
             node.setText(getDefaultLanguageText(menuDeployDetails)); break;
           } 
           node.setText(menuDeployDetails.getOther2());
           break;
       } 
       
       if (!"".equals(menuDeployDetails.getPage_id()) && menuDeployDetails.getPage_id() != null) {
         Map<String, PageModel> pages = this.cacheService.getPages();
         if (pages.get(menuDeployDetails.getPage_id()) != null) {
           menuDeployDetails.setParam(((PageModel)pages.get(menuDeployDetails.getPage_id())).getParam());
           menuDeployDetails.setUrl(((PageModel)pages.get(menuDeployDetails.getPage_id())).getUrl_id());
         } 
       } 
       node.setData(menuDeployDetails);
       treeData.add(node);
     } 
     return new ActionResult(true, treeData);
   }
   public String getDefaultLanguageText(MenuDeployDetails menuDeployDetails) {
     String default_language = this.cacheService.getSystemParam().getDefault_language();
     String text = "";
     switch (default_language) {
       case "cn":
         text = menuDeployDetails.getCn();
         break;
       case "en":
         text = menuDeployDetails.getEn();
         break;
       case "jp":
         text = menuDeployDetails.getJp();
         break;
       case "other1":
         text = menuDeployDetails.getOther1();
         break;
       case "other2":
         text = menuDeployDetails.getOther2();
         break;
     } 
     return text;
   }
   
   @ResponseBody
   @RequestMapping({"/initMenuDeployById.do"})
   public ActionResult initMenuDeployById(@RequestBody MenuDeployRequestModel request) {
     List<MenuDeployDetails> menuDeployList = this.menuDeployDao.selectMenuById(request.getId());
     return new ActionResult(true, menuDeployList);
   }
   
   @ResponseBody
   @RequestMapping({"/menuDeploySave.do"})
   public ActionResult menuDeploySave(@RequestBody MenuDeployRequestModel request) {
     Map<String, List<MenuDeployDetails>> savaDataMap = request.getMenuDeploySave();
     List<MenuDeployDetails> insertlist = (List)savaDataMap.get("insert");
     List<MenuDeployDetails> updatelist = (List)savaDataMap.get("update");
     if (insertlist.size() > 0) {
       for (MenuDeployDetails insert : insertlist) {
         String[] uuids = UUID.randomUUID().toString().split("-");
         String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
         for (MenuDeployDetails getParentId : insertlist) {
           if (getParentId.getParent_id().equals(insert.getId())) {
             getParentId.setParent_id(uuid);
           }
         } 
         for (MenuDeployDetails getParentId : updatelist) {
           if (getParentId.getParent_id().equals(insert.getId())) {
             getParentId.setParent_id(uuid);
           }
         } 
         insert.setId(uuid);
         insert.setIcon_out(insert.getIcon());
         insert.setPage_id(uuid);
         if ("#".equals(insert.getParent_id())) {
           insert.setParent_id("");
         }
         if ("PCMenu".equals(insert.getParent_id()) || "mobileMenu".equals(insert.getParent_id())) {
           insert.setParent_id("");
         }
         if ("".equals(insert.getInternational_id()) || insert.getInternational_id() == null || "undefined"
           .equals(insert.getInternational_id())) {
           insert.setInternational_id(uuid);
           insert.setId_international(uuid);
           this.menuDeployDao.addInternational(insert);
         } 
         if (!"".equals(insert.getPage_id()) && insert.getPage_id() != null) {
           this.menuDeployDao.addPage(insert);
         }
       } 
       for (MenuDeployDetails insert : insertlist) {
         this.menuDeployDao.addMenuDeploy(insert);
       }
     } 
     if (updatelist.size() > 0) {
       for (MenuDeployDetails update : updatelist) {
         String[] uuids = UUID.randomUUID().toString().split("-");
         String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
         this.menuDeployDao.deleteMenuDeployById(update.getId());
         update.setIcon_out(update.getIcon());
         if (!"".equals(update.getUrl()) && update.getUrl() != null) {
           this.menuDeployDao.deletePage(update.getPage_id());
           this.menuDeployDao.addPage(update);
         } 
         if ("PCMenu".equals(update.getParent_id()) || "mobileMenu".equals(update.getParent_id())) {
           update.setParent_id("");
         }
         if ("".equals(update.getInternational_id()) || update.getInternational_id() == null) {
           update.setInternational_id(uuid);
           update.setId_international(uuid);
           this.menuDeployDao.addInternational(update);
         } 
         this.menuDeployDao.addMenuDeploy(update);
       } 
     }
     List<MenuDeployDetails> deletelist = (List)savaDataMap.get("deletes");
     if (deletelist.size() > 0) {
       for (MenuDeployDetails delete : deletelist) {
         this.menuDeployDao.deleteMenuDeploy(delete.getId());
         this.menuDeployDao.deletePage(delete.getPage_id());
       } 
     }
     return new ActionResult(true, "保存成功！");
   }
 }


