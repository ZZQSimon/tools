 package cn.com.easyerp.DeployTool.view;
 
 import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.DeployTool.service.UrlInterfaceService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 @RequestMapping({"/urlInterface"})
 @Controller
 public class UrlInterfaceController extends FormViewControllerBase {
   private static final String TREE_ROOT_I18N_KEY_URL = "url_tree_text";
   private static final String TREE_ROOT_I18N_KEY_OUR_URL = "url_tree_out_url";
   private static final String TREE_ROOT_I18N_KEY_JAVA_API = "java_api_tree_text";
   private static final String TREE_ROOT_I18N_KEY_PROC_API = "proc_api_tree_text";
   private static final String TREE_ROOT_I18N_KEY_OUTSIDE_INTERFACE = "outside_interface_tree_text";
   private static final String TREE_ROOT_I18N_KEY_REPORT = "report_tree_text";
   @Autowired
   private CacheService cacheService;
   @Autowired
   private DataService dataService;
   @Autowired
   private UrlInterfaceService urlInterfaceService;
   
   @RequestMapping({"/urlInterface.view"})
   public ModelAndView urlInterface(@RequestBody UrlInterfaceRequestModel request) {
     UrlInterfaceModel form = new UrlInterfaceModel(request.getParent());
     form.setType(request.getType());
     return buildModelAndView(form);
   }
   @RequestMapping({"/selectUrlInterface.do"})
   @ResponseBody
   public ActionResult selectUrlInterface(@RequestBody UrlInterfaceRequestModel request) {
     Map<String, UrlInterfaceDescribe> urlInterface = this.cacheService.getUrlInterface();
     List<TreeNode> treeNodes = buildUrlInterfaceTreeNode(urlInterface, request.getType());
     return new ActionResult(true, treeNodes);
   }
   @ResponseBody
   @RequestMapping({"/CRUDUrlInterface.do"})
   public ActionResult CRUDUrlInterface(@RequestBody UrlInterfaceRequestModel request) {
     this.urlInterfaceService.CRUDUrlInterface(request);
     return new ActionResult(true);
   } private List<TreeNode> buildUrlInterfaceTreeNode(Map<String, UrlInterfaceDescribe> urlInterface, String type) {
     TreeNode parentNodeOutsideInterface, parentNodeProcApi, parentNodeJavaApi, parentNodeOutUrl, parentNodeUrl;
     List<TreeNode> treeNodes = new ArrayList<TreeNode>();
     switch (type) {
       case "1":
         parentNodeUrl = new TreeNode();
         parentNodeUrl.setId("url_tree_text");
         parentNodeUrl.setParent("#");
         parentNodeUrl.setText(this.dataService.i18nString(this.cacheService.getMsgI18n("url_tree_text")));
         treeNodes.add(parentNodeUrl);
         
         parentNodeOutUrl = new TreeNode();
         parentNodeOutUrl.setId("url_tree_out_url");
         parentNodeOutUrl.setParent("#");
         parentNodeOutUrl.setText(this.dataService.i18nString(this.cacheService.getMsgI18n("url_tree_out_url")));
         treeNodes.add(parentNodeOutUrl);
         break;
       case "2":
         parentNodeJavaApi = new TreeNode();
         parentNodeJavaApi.setId("java_api_tree_text");
         parentNodeJavaApi.setParent("#");
         parentNodeJavaApi.setText(this.dataService.i18nString(this.cacheService.getMsgI18n("java_api_tree_text")));
         treeNodes.add(parentNodeJavaApi);
         
         parentNodeProcApi = new TreeNode();
         parentNodeProcApi.setId("proc_api_tree_text");
         parentNodeProcApi.setParent("#");
         parentNodeProcApi.setText(this.dataService.i18nString(this.cacheService.getMsgI18n("proc_api_tree_text")));
         treeNodes.add(parentNodeProcApi);
         
         parentNodeOutsideInterface = new TreeNode();
         parentNodeOutsideInterface.setId("outside_interface_tree_text");
         parentNodeOutsideInterface.setParent("#");
         parentNodeOutsideInterface.setText(this.dataService.i18nString(this.cacheService.getMsgI18n("outside_interface_tree_text")));
         treeNodes.add(parentNodeOutsideInterface);
         break;
       default:
         throw new ApplicationException("has no type \"" + type + "\"");
     } 
 
     
     for (Map.Entry<String, UrlInterfaceDescribe> entry : urlInterface.entrySet()) {
       UrlInterfaceDescribe url = (UrlInterfaceDescribe)entry.getValue();
       if ("1".equals(type)) {
         switch (url.getType()) {
           case 1:
             treeNodes.add(buildTreeNodeForParent(url, "url_tree_text"));
             break;
           case 5:
             treeNodes.add(buildTreeNodeForParent(url, "url_tree_out_url"));
             break;
         } 
         continue;
       } 
       if ("2".equals(type)) {
         switch (url.getType()) {
           case 2:
             treeNodes.add(buildTreeNodeForParent(url, "java_api_tree_text"));
             break;
           case 3:
             treeNodes.add(buildTreeNodeForParent(url, "proc_api_tree_text"));
             break;
           case 4:
             treeNodes.add(buildTreeNodeForParent(url, "outside_interface_tree_text"));
             break;
         } 
 
       
       }
     } 
     return treeNodes;
   }
   private TreeNode buildTreeNodeForParent(UrlInterfaceDescribe url, String parent) {
     TreeNode treeNode = new TreeNode();
     treeNode.setId(url.getId());
     treeNode.setParent(parent);
     if (url.getI18n() == null) {
       treeNode.setText(url.getName());
     } else {
       treeNode.setText(this.dataService.i18nText(url.getI18n()));
     } 
     treeNode.setData(url);
     return treeNode;
   }
 }


