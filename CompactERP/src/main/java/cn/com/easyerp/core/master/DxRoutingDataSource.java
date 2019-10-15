 package cn.com.easyerp.core.master;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.schedule.DxTaskScheduler;
import cn.com.easyerp.framework.common.Common;
 
public class DxRoutingDataSource extends AbstractRoutingDataSource	{
   public static final String DATA_SOURCE_SESSION_KEY = "DATA_SOURCE_SESSION_KEY";
   public static final String DEFAULT_DATA_SOURCE_DRIVER = "com.mysql.jdbc.Driver";
   public static String DX_DEFAULT_DATASOURCE = "PRDHRIDB";
   private String defaultDataSourceDriver = "com.mysql.jdbc.Driver";
   private int active_flag = 1;
   
   private List<String> names;
   
   @Autowired
   private MasterDao masterDao;
   
   private String activeDomainKey;
   
   @PostConstruct
   private void init() {
     Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();
     List<DataSourceDescModel> list = this.masterDao.selectDataSourceDesc(this.active_flag);
     this.names = new ArrayList<>();
     for (DataSourceDescModel desc : list) {
       if (!dataSourceMap.containsKey(desc.getName())) {
         dataSourceMap.put(desc.getName(), setupDataSource(desc));
         if (desc.isDefault_account())
           this.activeDomainKey = desc.getName(); 
       } 
       this.names.add(desc.getName());
     } 
     
     if (this.activeDomainKey == null) {
       this.activeDomainKey = DX_DEFAULT_DATASOURCE;
     }
     setTargetDataSources(dataSourceMap);
   }
   
   public void reloadDataSource() {
     init();
     afterPropertiesSet();
   }
 
   
   public void setDefaultDataSourceDriver(String defaultDataSourceDriver) { this.defaultDataSourceDriver = defaultDataSourceDriver; }
 
 
   
   public DataSource setupDataSource(DataSourceDescModel desc) {
     BasicDataSource ds = new BasicDataSource();
     String driver = desc.getDriver();
     if (driver == null)
       driver = this.defaultDataSourceDriver; 
     ds.setDriverClassName(driver);
     ds.setUsername(desc.getUsername());
     ds.setPassword(desc.getPassword());
     ds.setUrl(desc.getUrl());
     ds.setMaxActive(1000);
     ds.setInitialSize(50);
     ds.setMaxIdle(50);
     ds.setRemoveAbandoned(true);
     ds.setRemoveAbandonedTimeout(180);
     ds.setMaxWait(1000L);
     ds.setValidationQuery("select 1");
     ds.setTestOnBorrow(true);
     return ds;
   }
 
 
 
   
   protected Object determineCurrentLookupKey() { return getDomainKey(); }
 
 
 
   
   public List<String> getList() { return this.names; }
 
 
 
   
   public String getActiveDomainKey() { return this.activeDomainKey; }
 
 
 
   
   public void setActiveDomainKey(String activeDomainKey) { this.activeDomainKey = activeDomainKey; }
 
 
   
   public String getDomainKey() {
     Object key;
     try {
       key = Common.getSessionObject("DATA_SOURCE_SESSION_KEY");
     } catch (ApplicationException e) {
       if (IllegalStateException.class.isInstance(e.getCause())) {
         key = DxTaskScheduler.getDomain();
       } else {
         throw e;
       } 
     }  if (Common.isBlank((String)key))
       key = this.activeDomainKey; 
     return (String)key;
   }
 
 
 
 
 
 
   
   public void setDomainKey(String key) {
     if (!this.names.contains(key))
       key = this.activeDomainKey; 
     Common.putSessionObject("DATA_SOURCE_SESSION_KEY", key);
   }
 
 
   
   public void setActive_flag(int active_flag) { this.active_flag = active_flag; }
 }


