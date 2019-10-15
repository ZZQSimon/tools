 package cn.com.easyerp.core.exception;

public class ValidateException
   extends RuntimeException
 {
   private String id;
   
   public ValidateException(String id, Throwable cause) {
     super(cause);
     this.id = id;
   }
 
   
   public ValidateException(String id, String message) {
     super(message);
     this.id = id;
   }
 
 
   
   public String getId() { return this.id; }
 }


