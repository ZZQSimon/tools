 package cn.com.easyerp.core.widget.file;
 
 import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;
 
 
 
 
 
 
 
 public class FileRequestModel
   extends WidgetRequestModelBase<FileFieldModel>
 {
   private String value;
   
   public String getValue() { return this.value; }
 
 
 
   
   public void setValue(String value) { this.value = value; }
 }


