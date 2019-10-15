 package cn.com.easyerp.core.exception;
 
 import javax.sql.DataSource;

import org.mybatis.spring.MyBatisExceptionTranslator;
import org.springframework.dao.DataAccessException;
 
 
 
 
 
 
 
 public class PersistenceExceptionTranslator
   extends MyBatisExceptionTranslator
 {
   public PersistenceExceptionTranslator(DataSource dataSource) { super(dataSource, true); }
 
 
 
 
   
   public DataAccessException translateExceptionIfPossible(RuntimeException e) { return new DatabaseException(super.translateExceptionIfPossible(e)); }
 }


