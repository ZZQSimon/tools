package cn.com.easyerp.framework.common;
 
 import java.util.List;
public class ApiActionResult extends ActionResult {
   private int result;
   private String message;
   private List<String> details;
   private boolean noPopup = false;
   
   public ApiActionResult(int result, String message, List<String> details) { this(result, message, details, null); }
 
   
   public ApiActionResult(int result, String message, List<String> details, Object data) {
     super((result == 0), data);
     this.message = message;
     this.result = result;
     this.details = details;
   }
   
   public int getResult() { return this.result; }
 
 
 
   
   public String getMessage() { return this.message; }
 
 
 
   
   public List<String> getDetails() { return this.details; }
 
 
 
   
   public boolean isNoPopup() { return this.noPopup; }
 
 
 
   
   public void setNoPopup(boolean noPopup) { this.noPopup = noPopup; }
 }


