 package cn.com.easyerp.DeployTool.view;
 
 import java.util.List;
import java.util.Map;

import cn.com.easyerp.DeployTool.service.MenuDeployDetails;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 public class MenuDeployRequestModel extends FormRequestModelBase<MenuDeployModel> {
   private String id;
   private Map<String, List<MenuDeployDetails>> menuDeploySave;
   
   public Map<String, List<MenuDeployDetails>> getMenuDeploySave() { return this.menuDeploySave; }
 
 
   
   public void setMenuDeploySave(Map<String, List<MenuDeployDetails>> menuDeploySave) { this.menuDeploySave = menuDeploySave; }
 
 
   
   public String getId() { return this.id; }
 
 
   
   public void setId(String id) { this.id = id; }
 }


