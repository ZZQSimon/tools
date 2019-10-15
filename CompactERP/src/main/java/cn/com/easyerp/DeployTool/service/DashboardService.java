 package cn.com.easyerp.DeployTool.service;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.DeployTool.dao.DashboardDao;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 @Service
 public class DashboardService
 {
   @Autowired
   private DashboardDao dashDao;
   @Autowired
   private AuthService authService;
   @Autowired
   private CacheService cacheService;
   @Autowired
   private DataService dataService;
   
   public void saveDashBoard(DashboardDescribe dash) { this.dashDao.addDashBoard(dash); }
 
 
 
   
   public void saveSubscribe(DashboardDescribe dash) {
     List<String> users = this.dashDao.getUsers();
     String subscribe_status = "3".equals(dash.getDashboard_type()) ? "0" : "1";
     for (String user : users) {
       this.dashDao.addSubscribe(dash.getDashboard_id(), dash.getUUID(), user, subscribe_status);
     }
   }
 
   
   public List<DashboardDescribe> getDashboards() { return this.dashDao.getDashboards(); }
 
   
   public List<DatabaseDataMap> getTableList(String table, String where, String orderBy, int defaultCount) {
     List<AuthGroup> optionAuthGroup = this.authService.getOptionAuthGroup(table, "read");
     String groupAuthCondition = this.dataService.buildGroupAuthDataCondition(table, optionAuthGroup);
     return this.dashDao.getTableList(table, where, groupAuthCondition, orderBy, defaultCount);
   }
 
   
   public int getTableCount(String table, String where) { return this.dashDao.getTableCount(table, where); }
 
 
   
   public List<DatabaseDataMap> getChartList() { return this.dashDao.getChartList(); }
 
   
   public List<DatabaseDataMap> getSubscribesBysubscriber(String subscriber) {
     List<DatabaseDataMap> results = new ArrayList<DatabaseDataMap>();
     
     Map<String, DatabaseDataMap> subscribes = new HashMap<String, DatabaseDataMap>();
     for (DatabaseDataMap subscribe : this.dashDao.getSubscribesBysubscriber(subscriber)) {
       subscribes.put(subscribe.get("dashboard_id").toString(), subscribe);
     }
     
     Map<String, Map<String, Object>> dashboards = this.cacheService.getDashboards();
     for (Map.Entry<String, Map<String, Object>> entry : dashboards.entrySet()) {
       DatabaseDataMap result = new DatabaseDataMap();
       result.putAll((Map)entry.getValue());
       DatabaseDataMap subscribe = (DatabaseDataMap)subscribes.get(entry.getKey());
       if (subscribe == null) {
         result.put("seq", Integer.valueOf(999));
       } else {
         result.putAll(subscribe);
       } 
       results.add(result);
     } 
     
     return results;
   }
   
   public boolean updateSubscribes(List<SubscribeDescribe> subscribes, AuthDetails subscriber) {
     this.dashDao.reloadStatus(subscriber.getId());
     for (SubscribeDescribe subscribe : subscribes) {
       if (this.dashDao.existTheSubscribe(subscriber, subscribe.getDashboard_id()) != 0) {
         this.dashDao.updateSubscribe(subscribe, subscriber.getId()); continue;
       } 
       this.dashDao.addSubscribe(subscribe.getDashboard_id(), DashboardDescribe.getUUID(), subscriber.getId(), "1");
     } 
     
     return true;
   }
 
   
   public boolean updateSubscribesSeq(List<SubscribeDescribe> subscribes, String subscriber) {
     this.dashDao.reloadStatus(subscriber);
     for (SubscribeDescribe subscribe : subscribes) {
       this.dashDao.updateSubscribesSeq(subscribe, subscriber);
     }
     return true;
   }
 
 
 
 
   
   public List<DatabaseDataMap> getHomeSubscribes(String subscriber) {
     List<DatabaseDataMap> subscribes = this.dashDao.getHomeSubscribes(subscriber);
     for (DatabaseDataMap subscribe : subscribes) {
       Map<String, Object> dashboard = this.cacheService.getDashboard(subscribe.get("dashboard_id").toString());
       
       subscribe.putAll(dashboard);
       if ("9".equals(dashboard.get("dashboard_type"))) {
         buildDashMenu(subscribe);
       }
     } 
 
     
     return subscribes;
   }
 
   
   public List<Map<String, Object>> getSubscribes() { return this.dashDao.getSubscribes(); }
 
   
   public int isExistsDashboard(DashboardDescribe dash) { return this.dashDao.isExistsDashboard(dash); }
 
 
   
   public void updateDashboard(DashboardDescribe dash) { this.dashDao.updateDashboard(dash); }
 
   
   public void deleteDashboard(String dashboard_id) {
     this.dashDao.deleteDashboard(dashboard_id);
     this.dashDao.deleteSubscribe(dashboard_id);
   }
 
   
   public void cancelSubscribe(String subscribe_id) { this.dashDao.cancelSubscribe(subscribe_id); }
 
   
   public void addUserDashboard(AuthDetails user) {
     if (this.dashDao.existSubscribe(user) == 0) {
       List<DashboardDescribe> dashs = this.dashDao.getDashboards();
       
       for (DashboardDescribe dash : dashs) {
         String subscribe_status = "3".equals(dash.getSubscribe_type()) ? "0" : "1";
         this.dashDao.addSubscribe(dash.getDashboard_id(), dash.getUUID(), user.getId(), subscribe_status);
       } 
     } 
   }
   
   public void checkUserDashboard(AuthDetails user) {
     List<DashboardDescribe> dashs = this.dashDao.getDashboards();
 
     
     for (DashboardDescribe dash : dashs) {
       boolean exists = (this.dashDao.existTheSubscribe(user, dash.getDashboard_id()) == 0);
       if (exists && !"3".equals(dash.getSubscribe_type())) {
         String subscribe_status = "1";
         this.dashDao.addSubscribe(dash.getDashboard_id(), dash.getUUID(), user.getId(), subscribe_status); continue;
       }  if (!exists && "1".equals(dash.getSubscribe_type()))
         this.dashDao.updateSubscribeStatus(dash.getDashboard_id(), user.getId()); 
     } 
   }
   
   public void buildDashMenu(DatabaseDataMap dash) {
	   if (dash == null) {
           return;
       }
       if (dash.get("dashboard_param") == null) {
           return;
       }
       try {
           final Map<String, Object> map = (Map<String, Object>)Common.fromJson(((HashMap<String, Object>)dash).get("dashboard_param").toString(), new TypeReference<Map<String, Object>>() {});
           if (map.get("menus") == null) {
               dash.put("dashboard_param", null);
           }
           else if (map.get("menus") instanceof List) {
               final List<MenuModel> menuParam = new ArrayList<MenuModel>();
               for (int i = 0; i < ((List<String>) map.get("menus")).size(); ++i) {
                   final MenuModel menu = this.cacheService.getMenu(((List<String>) map.get("menus")).get(i).toString());
                   if (this.authService.newAuth(AuthService.getCurrentUser(), this.cacheService.getMenuAuthGroup(), 1, menu)) {
                       menuParam.add(menu);
                   }
               }
               dash.put("dashboard_param", menuParam);
           }
           else {
               dash.put("dashboard_param", null);
           }
       }
       catch (Exception e) {
           dash.put("dashboard_param", null);
       }
   }
 }


