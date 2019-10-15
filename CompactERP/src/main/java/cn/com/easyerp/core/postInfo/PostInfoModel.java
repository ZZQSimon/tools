 package cn.com.easyerp.core.postInfo;
 
 import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 @Widget("postInfo")
 public class PostInfoModel
   extends FormModelBase
 {
   protected PostInfoModel(String parent) { super(ActionType.view, parent); }
 
 
   
   public String getTitle() { return "postInfo"; }
 }


