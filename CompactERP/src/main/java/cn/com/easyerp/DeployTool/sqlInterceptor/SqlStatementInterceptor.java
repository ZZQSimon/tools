 package cn.com.easyerp.DeployTool.sqlInterceptor;
 
 import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;

import cn.com.easyerp.DeployTool.dao.DeploySqlDao;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ApplicationContextProvider;
import cn.com.easyerp.core.master.DxRoutingDataSource;
 
 @Intercepts({@Signature(method = "update", type = org.apache.ibatis.executor.Executor.class, args = {MappedStatement.class, Object.class})})
 public class SqlStatementInterceptor implements Interceptor {
   @Autowired
   private DxRoutingDataSource dxRoutingDataSource;
   @Autowired
   private ApplicationContextProvider applicationContext;
   private DeploySqlDao deploySqlDao;
   private Properties properties;
   
   public Object intercept(Invocation arg0) throws Throwable {
     MappedStatement mappedStatement = (MappedStatement)arg0.getArgs()[0];
     Object parameter = null;
     if (arg0.getArgs().length > 1) {
       parameter = arg0.getArgs()[1];
     }
     String sqlId = mappedStatement.getId();
     BoundSql boundSql = mappedStatement.getBoundSql(parameter);
     Configuration configuration = mappedStatement.getConfiguration();
     String databaseId = this.dxRoutingDataSource.getDomainKey();
     String currentUserId = AuthService.getCurrentUserId();
     
     Object returnValue = arg0.proceed();
     
     String sql = getSql(configuration, boundSql);
     int sqlPackage = sqlId.indexOf("cn.com.easyerp.DeployTool.dao");
     this.deploySqlDao = (DeploySqlDao)ContextLoader.getCurrentWebApplicationContext().getBean("deploySqlDao");
     if (sqlPackage != -1 && !"cn.com.easyerp.DeployTool.dao.DeploySqlDao.deploySql".equals(sqlId)) {
       DeploySql deploySql = new DeploySql();
       deploySql.setSql(sql);
       deploySql.setDb(databaseId);
       deploySql.setUser(currentUserId);
       deploySql.setDate(new Date());
       this.deploySqlDao.deploySql(deploySql);
     } 
     return returnValue;
   }
 
   
   public static String getSql(Configuration configuration, BoundSql boundSql) { return showSql(configuration, boundSql); }
 
 
   
   public static String showSql(Configuration configuration, BoundSql boundSql) {
     Object parameterObject = boundSql.getParameterObject();
     List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
     String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
     if (parameterMappings.size() > 0 && parameterObject != null) {
       TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
       if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
         String param = getParameterValue(parameterObject);
         param = Matcher.quoteReplacement(param);
         sql = sql.replaceFirst("\\?", param);
       } else {
         
         MetaObject metaObject = configuration.newMetaObject(parameterObject);
         for (ParameterMapping parameterMapping : parameterMappings) {
           String propertyName = parameterMapping.getProperty();
           if (metaObject.hasGetter(propertyName)) {
             Object obj = metaObject.getValue(propertyName);
             String param = getParameterValue(obj);
             param = Matcher.quoteReplacement(param);
             sql = sql.replaceFirst("\\?", param); continue;
           }  if (boundSql.hasAdditionalParameter(propertyName)) {
             Object obj = boundSql.getAdditionalParameter(propertyName);
             String param = getParameterValue(obj);
             param = Matcher.quoteReplacement(param);
             sql = sql.replaceFirst("\\?", param);
           } 
         } 
       } 
     } 
     return sql;
   }
   
   private static String getParameterValue(Object obj) {
     String value = null;
     if (obj instanceof String) {
       value = "'" + obj.toString() + "'";
     } else if (obj instanceof Date) {
       DateFormat formatter = DateFormat.getDateTimeInstance(2, 2, Locale.CHINA);
       value = "'" + formatter.format(new Date()) + "'";
     }
     else if (obj != null) {
       value = obj.toString();
     } else {
       value = "null";
     } 
 
     
     return value;
   }
 
 
 
   
   public Object plugin(Object arg0) { return Plugin.wrap(arg0, this); }
 
 
 
 
   
   public void setProperties(Properties arg0) { this.properties = arg0; }
 }


