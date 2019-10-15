 package cn.com.easyerp.DeployTool.view;
 
 import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.TimingTaskDao;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.timedTask.SysTimedTaskService;
import cn.com.easyerp.core.timedTask.entity.TimeTaskBusinessTimeDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskEventDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskSysTimeDescribe;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 @Controller
 @RequestMapping({"/timingTask"})
 public class TimingTaskController
   extends FormViewControllerBase
 {
   @Autowired
   private TimingTaskDao timingTaskDao;
   @Autowired
   private CacheService cacheService;
   @Autowired
   private SysTimedTaskService sysTimedTaskService;
   @Autowired
   private DataService dataService;
   
   @RequestMapping({"/timingTask.view"})
   public ModelAndView timingTask(@RequestBody TimingTaskRequestModel request) {
     TimingTaskModel form = new TimingTaskModel(request.getParent());
     List<TimeTaskDescribe> timingTask = this.timingTaskDao.selectAllTimingTask();
     for (int i = 0; i < timingTask.size(); i++) {
       if (!"".equals(((TimeTaskDescribe)timingTask.get(i)).getInternational_id()) && ((TimeTaskDescribe)timingTask.get(i)).getInternational_id() != null) {
         I18nDescribe name_i18n = this.cacheService.getMsgI18n(((TimeTaskDescribe)timingTask.get(i)).getInternational_id());
         ((TimeTaskDescribe)timingTask.get(i)).setName_i18n(name_i18n);
       } 
       
       List<TimeTaskEventDescribe> timeTaskEvent = this.timingTaskDao.selectTimeTaskEvent(((TimeTaskDescribe)timingTask.get(i)).getTask_id());
       
       List<TimeTaskSysTimeDescribe> timeTaskSysTime = this.timingTaskDao.selectTimeTaskSysTime(((TimeTaskDescribe)timingTask.get(i)).getTask_id());
       
       List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTime = this.timingTaskDao.selectTimeTaskBusinessTime(((TimeTaskDescribe)timingTask.get(i)).getTask_id());
       ((TimeTaskDescribe)timingTask.get(i)).setTimeTaskEventDescribes(timeTaskEvent);
       ((TimeTaskDescribe)timingTask.get(i)).setTimeTaskSysTimeDescribes(timeTaskSysTime);
       ((TimeTaskDescribe)timingTask.get(i)).setTimeTaskBusinessTimeDescribes(timeTaskBusinessTime);
     } 
     form.setTimingTask(timingTask);
     return buildModelAndView(form);
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   @ResponseBody
   @RequestMapping({"/retrieveTimeTask.do"})
   public ActionResult retrieveTimeTask(@RequestBody TimingTaskRequestModel request) {
     String retrieveValue = request.getRetrieveValue();
     List<TimeTaskDescribe> timingTask = this.timingTaskDao.selectAllTimingTask();
     List<TimeTaskDescribe> retrieveTimeTask = new ArrayList<TimeTaskDescribe>();
     for (int i = 0; i < timingTask.size(); i++) {
       if (!"".equals(((TimeTaskDescribe)timingTask.get(i)).getInternational_id()) && ((TimeTaskDescribe)timingTask.get(i)).getInternational_id() != null) {
         I18nDescribe name_i18n = this.cacheService.getMsgI18n(((TimeTaskDescribe)timingTask.get(i)).getInternational_id());
         ((TimeTaskDescribe)timingTask.get(i)).setName_i18n(name_i18n);
       } 
     } 
     if ("".equals(retrieveValue) || retrieveValue == null) {
       retrieveTimeTask = timingTask;
     } else {
       for (int i = 0; i < timingTask.size(); i++) {
         I18nDescribe timeTaskI18N = ((TimeTaskDescribe)timingTask.get(i)).getName_i18n();
         String language_id = this.dataService.getCurrentLanguage();
         switch (language_id) {
           case "cn":
             if (timeTaskI18N.getCn().indexOf(retrieveValue) != -1) {
               retrieveTimeTask.add(timingTask.get(i));
             }
             break;
           case "en":
             if (timeTaskI18N.getEn().indexOf(retrieveValue) != -1) {
               retrieveTimeTask.add(timingTask.get(i));
             }
             break;
           case "jp":
             if (timeTaskI18N.getJp().indexOf(retrieveValue) != -1) {
               retrieveTimeTask.add(timingTask.get(i));
             }
             break;
           case "other1":
             if (timeTaskI18N.getOther1().indexOf(retrieveValue) != -1) {
               retrieveTimeTask.add(timingTask.get(i));
             }
             break;
           case "other2":
             if (timeTaskI18N.getOther2().indexOf(retrieveValue) != -1) {
               retrieveTimeTask.add(timingTask.get(i));
             }
             break;
           default:
             if (timeTaskI18N.getCn().indexOf(retrieveValue) != -1) {
               retrieveTimeTask.add(timingTask.get(i));
             }
             break;
         } 
       } 
     } 
     return new ActionResult(true, retrieveTimeTask);
   }
   
   @ResponseBody
   @RequestMapping({"/saveTimingTask.do"})
   public ActionResult saveTimingTask(@RequestBody TimingTaskRequestModel request) {
     List<TimeTaskDescribe> saveTimingTask = request.getSaveTimingTask();
     for (TimeTaskDescribe timeTaskDescribe : saveTimingTask) {
       
       if (timeTaskDescribe.getSaveType() == 1) {
         String[] uuids = UUID.randomUUID().toString().split("-");
         String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
         timeTaskDescribe.setInternational_id(uuid);
         I18nDescribe name_i18n = timeTaskDescribe.getName_i18n();
         name_i18n.setInternational_id(uuid);
         this.timingTaskDao.insertInternational(name_i18n);
         this.timingTaskDao.insertTimingTask(timeTaskDescribe);
         if (timeTaskDescribe.getTimeTaskEventDescribes().size() > 0) {
           List<TimeTaskEventDescribe> timeTaskEventDescribes = timeTaskDescribe.getTimeTaskEventDescribes();
           for (TimeTaskEventDescribe timeTaskEventDescribe : timeTaskEventDescribes) {
             this.timingTaskDao.insertTimeTaskEvent(timeTaskEventDescribe);
           }
         } 
         if (timeTaskDescribe.getTimeTaskSysTimeDescribes().size() > 0) {
           List<TimeTaskSysTimeDescribe> timeTaskSysTimeDescribes = timeTaskDescribe.getTimeTaskSysTimeDescribes();
           for (TimeTaskSysTimeDescribe timeTaskSysTimeDescribe : timeTaskSysTimeDescribes) {
             this.timingTaskDao.insertTimeTaskSysTime(timeTaskSysTimeDescribe);
           }
         } 
         if (timeTaskDescribe.getTimeTaskBusinessTimeDescribes().size() > 0) {
           List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTimeDescribes = timeTaskDescribe.getTimeTaskBusinessTimeDescribes();
           for (TimeTaskBusinessTimeDescribe timeTaskBusinessTimeDescribe : timeTaskBusinessTimeDescribes) {
             this.timingTaskDao.insertTimeTaskBusinessTime(timeTaskBusinessTimeDescribe);
           }
         } 
       } 
       
       if (timeTaskDescribe.getSaveType() == 2) {
         this.timingTaskDao.deleteTimingTask(timeTaskDescribe);
         this.timingTaskDao.insertTimingTask(timeTaskDescribe);
         if (timeTaskDescribe.getTimeTaskEventDescribes().size() > 0) {
           List<TimeTaskEventDescribe> timeTaskEventDescribes = timeTaskDescribe.getTimeTaskEventDescribes();
           for (TimeTaskEventDescribe timeTaskEventDescribe : timeTaskEventDescribes) {
             this.timingTaskDao.insertTimeTaskEvent(timeTaskEventDescribe);
           }
         } 
         if (timeTaskDescribe.getTimeTaskSysTimeDescribes().size() > 0) {
           List<TimeTaskSysTimeDescribe> timeTaskSysTimeDescribes = timeTaskDescribe.getTimeTaskSysTimeDescribes();
           for (TimeTaskSysTimeDescribe timeTaskSysTimeDescribe : timeTaskSysTimeDescribes) {
             this.timingTaskDao.insertTimeTaskSysTime(timeTaskSysTimeDescribe);
           }
         } 
         if (timeTaskDescribe.getTimeTaskBusinessTimeDescribes().size() > 0) {
           List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTimeDescribes = timeTaskDescribe.getTimeTaskBusinessTimeDescribes();
           for (TimeTaskBusinessTimeDescribe timeTaskBusinessTimeDescribe : timeTaskBusinessTimeDescribes) {
             this.timingTaskDao.insertTimeTaskBusinessTime(timeTaskBusinessTimeDescribe);
           }
         } 
       } 
       
       if (timeTaskDescribe.getSaveType() == 3) {
         this.timingTaskDao.deleteTimingTask(timeTaskDescribe);
       }
     } 
     this.sysTimedTaskService.reload();
     List<TimeTaskDescribe> timingTask = this.timingTaskDao.selectAllTimingTask();
     for (int i = 0; i < timingTask.size(); i++) {
       if (!"".equals(((TimeTaskDescribe)timingTask.get(i)).getInternational_id()) && ((TimeTaskDescribe)timingTask.get(i)).getInternational_id() != null) {
         I18nDescribe name_i18n = this.cacheService.getMsgI18n(((TimeTaskDescribe)timingTask.get(i)).getInternational_id());
         ((TimeTaskDescribe)timingTask.get(i)).setName_i18n(name_i18n);
       } 
       
       List<TimeTaskEventDescribe> timeTaskEvent = this.timingTaskDao.selectTimeTaskEvent(((TimeTaskDescribe)timingTask.get(i)).getTask_id());
       
       List<TimeTaskSysTimeDescribe> timeTaskSysTime = this.timingTaskDao.selectTimeTaskSysTime(((TimeTaskDescribe)timingTask.get(i)).getTask_id());
       
       List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTime = this.timingTaskDao.selectTimeTaskBusinessTime(((TimeTaskDescribe)timingTask.get(i)).getTask_id());
       ((TimeTaskDescribe)timingTask.get(i)).setTimeTaskEventDescribes(timeTaskEvent);
       ((TimeTaskDescribe)timingTask.get(i)).setTimeTaskSysTimeDescribes(timeTaskSysTime);
       ((TimeTaskDescribe)timingTask.get(i)).setTimeTaskBusinessTimeDescribes(timeTaskBusinessTime);
     } 
     return new ActionResult(true, timingTask);
   }
 }


