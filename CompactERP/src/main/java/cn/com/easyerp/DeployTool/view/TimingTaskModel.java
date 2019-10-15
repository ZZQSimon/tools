 package cn.com.easyerp.DeployTool.view;
 
 import java.util.List;

import cn.com.easyerp.core.timedTask.entity.TimeTaskDescribe;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 @Widget("timingTask")
 public class TimingTaskModel
   extends FormModelBase
 {
   private List<TimeTaskDescribe> timingTask;
   
   public List<TimeTaskDescribe> getTimingTask() { return this.timingTask; }
 
   
   public void setTimingTask(List<TimeTaskDescribe> timingTask) { this.timingTask = timingTask; }
 
   
   protected TimingTaskModel(String parent) { super(ActionType.view, parent); }
 
 
   
   public String getTitle() { return "timingTask"; }
 }


