 package cn.com.easyerp.core.view.form.index;
 
 import java.util.List;

import cn.com.easyerp.core.approve.MyApproveAndWaitMeApprove;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 @Widget("indexApprove")
 public class IndexApproveModel
   extends FormModelBase
 {
   private List<MyApproveAndWaitMeApprove> myApprove;
   private List<MyApproveAndWaitMeApprove> waitMeApprove;
   private int myApproveSize;
   private int waitMeApproveSize;
   
   public String getTitle() { return "indexApprove"; }
 
 
   
   public IndexApproveModel() { super(ActionType.view, null); }
 
   
   public List<MyApproveAndWaitMeApprove> getMyApprove() { return this.myApprove; }
 
 
   
   public void setMyApprove(List<MyApproveAndWaitMeApprove> myApprove) { this.myApprove = myApprove; }
 
 
   
   public List<MyApproveAndWaitMeApprove> getWaitMeApprove() { return this.waitMeApprove; }
 
 
   
   public void setWaitMeApprove(List<MyApproveAndWaitMeApprove> waitMeApprove) { this.waitMeApprove = waitMeApprove; }
 
 
   
   public int getMyApproveSize() { return this.myApproveSize; }
 
 
   
   public void setMyApproveSize(int myApproveSize) { this.myApproveSize = myApproveSize; }
 
 
   
   public int getWaitMeApproveSize() { return this.waitMeApproveSize; }
 
 
   
   public void setWaitMeApproveSize(int waitMeApproveSize) { this.waitMeApproveSize = waitMeApproveSize; }
 }


