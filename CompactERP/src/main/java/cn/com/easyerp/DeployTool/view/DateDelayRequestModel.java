 package cn.com.easyerp.DeployTool.view;
 
 import cn.com.easyerp.DeployTool.service.Frontdata;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 public class DateDelayRequestModel
   extends FormRequestModelBase<DateDelayModel>
 {
   private Frontdata frontdata;
   
   public Frontdata getFrontdata() { return this.frontdata; }
 
 
   
   public void setFrontdata(Frontdata frontdata) { this.frontdata = frontdata; }
 }


