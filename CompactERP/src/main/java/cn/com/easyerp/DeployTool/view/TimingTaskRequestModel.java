 package cn.com.easyerp.DeployTool.view;
 
 import java.util.List;

import cn.com.easyerp.core.timedTask.entity.TimeTaskDescribe;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 public class TimingTaskRequestModel
   extends FormRequestModelBase<TimingTaskModel> {
   private String task_id;
   private List<TimeTaskDescribe> saveTimingTask;
   private String retrieveValue;
   
   public String getRetrieveValue() { return this.retrieveValue; }
 
   
   public void setRetrieveValue(String retrieveValue) { this.retrieveValue = retrieveValue; }
 
   
   public List<TimeTaskDescribe> getSaveTimingTask() { return this.saveTimingTask; }
 
   
   public void setSaveTimingTask(List<TimeTaskDescribe> saveTimingTask) { this.saveTimingTask = saveTimingTask; }
 
 
   
   public String getTask_id() { return this.task_id; }
 
 
   
   public void setTask_id(String task_id) { this.task_id = task_id; }
 }


