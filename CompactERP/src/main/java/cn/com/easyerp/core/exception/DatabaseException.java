 package cn.com.easyerp.core.exception;
 
 import org.springframework.dao.DataAccessException;
 
 
 
 
 
 
 
 
 public class DatabaseException
   extends DataAccessException
 {
   private static final String EXCEPTION_MSG = "DB access exception";
   
   public DatabaseException(String msg) { super(msg); }
 
 
 
   
   public DatabaseException(Throwable cause) { super("DB access exception", cause); }
 }


