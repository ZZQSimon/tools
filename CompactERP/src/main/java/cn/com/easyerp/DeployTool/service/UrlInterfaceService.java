 package cn.com.easyerp.DeployTool.service;
 import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.DeployTool.dao.UrlInterfaceDao;
import cn.com.easyerp.DeployTool.view.UrlInterfaceRequestModel;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableCheckRuleDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
 
 @Service
 public class UrlInterfaceService {
   @Autowired
   private UrlInterfaceDao urlInterfaceDao;
   @Autowired
   private TableDeployService tableDeployService;
   @Autowired
   private TableDeployDao tableDeployDao;
   
   @Transactional
   public void CRUDUrlInterface(UrlInterfaceRequestModel request) {
     if (request.getEditUrlInterfaceMap() != null && request
       .getEditUrlInterfaceMap().size() != 0) {
       for (Map.Entry<String, UrlInterfaceDescribe> entry : request.getEditUrlInterfaceMap().entrySet()) {
         editUrlInterface((UrlInterfaceDescribe)entry.getValue());
       }
     }
     if (request.getAddUrlInterfaceMap() != null && request
       .getAddUrlInterfaceMap().size() != 0) {
       for (Map.Entry<String, UrlInterfaceDescribe> entry : request.getAddUrlInterfaceMap().entrySet()) {
         addUrlInterface((UrlInterfaceDescribe)entry.getValue());
       }
     }
     if (request.getDeleteUrlInterfaceMap() != null && request
       .getDeleteUrlInterfaceMap().size() != 0) {
       for (Map.Entry<String, UrlInterfaceDescribe> entry : request.getDeleteUrlInterfaceMap().entrySet()) {
         deleteUrlInterface((UrlInterfaceDescribe)entry.getValue());
       }
     }
   }
   
   private void editUrlInterface(UrlInterfaceDescribe urlInterface) {
     if (urlInterface == null)
       return; 
     if ("".endsWith(urlInterface.getName()) || urlInterface.getName() == null) {
       I18nDescribe urlInterfaceNameI18N = urlInterface.getI18n();
       String[] uuids = UUID.randomUUID().toString().split("-");
       String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
       urlInterfaceNameI18N.setInternational_id(uuid);
       urlInterface.setName(uuid);
       this.tableDeployDao.addI18N(urlInterfaceNameI18N);
     } 
     this.urlInterfaceDao.updateUrl(urlInterface);
     if (urlInterface.getDeleteColumns() != null && urlInterface.getDeleteColumns().size() != 0) {
       for (ColumnDeploy columnDeploy : urlInterface.getDeleteColumns())
       {
         this.urlInterfaceDao.deleteColumn(columnDeploy);
       }
     }
     if (urlInterface.getUrlParam() != null && urlInterface.getUrlParam().size() != 0) {
       Map<String, ColumnDescribe> urlParam = urlInterface.getUrlParam();
       for (Map.Entry<String, ColumnDescribe> entry : urlParam.entrySet()) {
         ColumnDescribe column = (ColumnDescribe)entry.getValue();
         column.setTable_id(urlInterface.getUrl());
         if ("".equals(column.getI18n().getInternational_id()) || column.getI18n().getInternational_id() == null) {
           I18nDescribe columnI18N = column.getI18n();
           String[] uuids = UUID.randomUUID().toString().split("-");
           String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
           columnI18N.setInternational_id(uuid);
           column.setInternational_id(uuid);
           this.tableDeployDao.addI18N(columnI18N);
         } 
         if (column.getColumnChange() == 1) {
           
           this.urlInterfaceDao.updateColumn(column); continue;
         }  if (column.getColumnChange() == 2) {
           String columnI18N = this.tableDeployService.I18NToDB(column.getI18n());
           column.setInternational_id(columnI18N);
           this.urlInterfaceDao.insertColumn(column);
         } 
       } 
     } 
     List<TableCheckRuleDescribe> urlChecks = urlInterface.getUrlCheck();
     this.urlInterfaceDao.deleteUrlCheckByUrlId(urlInterface.getId());
     if (urlChecks != null && urlChecks.size() != 0) {
       int i = 1;
       for (TableCheckRuleDescribe urlCheck : urlChecks) {
         if ("".equals(urlCheck.getError_msg_id()) || urlCheck.getError_msg_id() == null) {
           String[] uuids = UUID.randomUUID().toString().split("-");
           String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
           I18nDescribe urlChecksI18N = urlCheck.getMsgI18n();
           urlChecksI18N.setInternational_id(uuid);
           urlCheck.setError_msg_id(uuid);
           this.tableDeployDao.addI18N(urlChecksI18N);
         } 
         if (!"".equals(urlCheck.getType()) && urlCheck.getType() != null) {
           urlCheck.setUrl_id(urlInterface.getId());
           urlCheck.setSeq(i++);
           if (urlCheck.getTable_id() == null)
             urlCheck.setTable_id(""); 
           this.urlInterfaceDao.insertUrlCheck(urlCheck);
         } 
       } 
     } 
     updateProd(urlInterface, "edit");
   }
   private void addUrlInterface(UrlInterfaceDescribe urlInterface) {
     String i18NToDB = this.tableDeployService.I18NToDB(urlInterface.getI18n());
     
     String url_id = urlInterface.getUrl();
     urlInterface.setId(url_id);
     urlInterface.setName(i18NToDB);
     this.urlInterfaceDao.insertUrl(urlInterface);
     
     if (urlInterface.getType() == 2 || urlInterface.getType() == 3) {
       TableDescribe table = new TableDescribe();
       table.setId(urlInterface.getId());
       table.setTable_type(2);
       table.setInternational_id(i18NToDB);
       if (urlInterface.getcRUDColumns() == null) {
         table.setDetail_disp_cols(0);
       } else {
         table.setDetail_disp_cols(urlInterface.getcRUDColumns().size());
       } 
       this.urlInterfaceDao.insertTable(table);
     } 
     if (urlInterface.getType() == 4 || urlInterface.getType() == 5) {
       TableDescribe table = new TableDescribe();
       table.setId(urlInterface.getId());
       table.setTable_type(2);
       table.setInternational_id(i18NToDB);
       table.setDetail_disp_cols(urlInterface.getcRUDColumns().size());
       this.urlInterfaceDao.insertTable(table);
     } 
     
     Map<String, ColumnDeploy> columns = urlInterface.getcRUDColumns();
     if (columns != null && columns.size() != 0) {
       for (Map.Entry<String, ColumnDeploy> entry : columns.entrySet()) {
         ColumnDeploy columnDeploy = (ColumnDeploy)entry.getValue();
         String columnI18N = this.tableDeployService.I18NToDB(columnDeploy.getI18n());
         columnDeploy.setInternational_id(columnI18N);
         columnDeploy.setUrl_id(url_id);
         if (urlInterface.getType() == 4 || urlInterface.getType() == 5) {
           columnDeploy.setTable_id(urlInterface.getId());
         } else {
           columnDeploy.setTable_id(urlInterface.getId());
         } 
         this.urlInterfaceDao.insertColumn(columnDeploy);
       } 
     }
     
     List<TableCheckRuleDescribe> urlChecks = urlInterface.getUrlCheck();
     if (urlChecks != null && urlChecks.size() != 0) {
       int i = 1;
       for (TableCheckRuleDescribe urlCheck : urlChecks) {
         if ("".equals(urlCheck.getError_msg_id()) || urlCheck.getError_msg_id() == null) {
           String[] uuids = UUID.randomUUID().toString().split("-");
           String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
           I18nDescribe urlChecksI18N = urlCheck.getMsgI18n();
           urlChecksI18N.setInternational_id(uuid);
           urlCheck.setError_msg_id(uuid);
           this.tableDeployDao.addI18N(urlChecksI18N);
         } 
         if (!"".equals(urlCheck.getType()) && urlCheck.getType() != null) {
           urlCheck.setUrl_id(url_id);
           urlCheck.setSeq(i++);
           if (urlCheck.getTable_id() == null)
             urlCheck.setTable_id(""); 
           this.urlInterfaceDao.insertUrlCheck(urlCheck);
         } 
       } 
     } 
     updateProd(urlInterface, "add");
   }
   private void deleteUrlInterface(UrlInterfaceDescribe urlInterface) {
     if (urlInterface == null)
       return; 
     String id = urlInterface.getId();
     if (id == null) {
       return;
     }
     
     this.urlInterfaceDao.deleteColumnByUrlId(id);
     if (urlInterface.getType() == 2 || urlInterface.getType() == 3) {
       this.urlInterfaceDao.daleteTableByUrlId(urlInterface.getUrl());
     } else {
       this.urlInterfaceDao.daleteTableByUrlId(id);
     } 
     
     this.urlInterfaceDao.deleteUrlCheckByUrlId(id);
     
     this.urlInterfaceDao.deleteUrl(id);
     updateProd(urlInterface, "delete");
   }
   private void updateProd(UrlInterfaceDescribe urlInterface, String type) {
     switch (type) {
       case "add":
         createProd(urlInterface);
         break;
       case "edit":
         if (urlInterface.getProdSql() != null && !"".equals(urlInterface.getProdSql())) {
           deleteProd(urlInterface);
           createProd(urlInterface);
         } 
         break;
       case "delete":
         deleteProd(urlInterface);
         break;
     } 
   }
   private void deleteProd(UrlInterfaceDescribe urlInterface) {
     if (urlInterface.getUrl() != null && !"".equals(urlInterface.getUrl())) {
       String sql = "DROP PROCEDURE IF EXISTS `" + urlInterface.getUrl() + "`";
       this.tableDeployDao.execSQL(sql);
     } 
   }
   private void createProd(UrlInterfaceDescribe urlInterface) {
     if (urlInterface.getType() != 3)
       return; 
     if (urlInterface.getUrl() == null || "".equals(urlInterface.getUrl()))
       return; 
     String sql = "CREATE PROCEDURE `" + urlInterface.getUrl() + "` (UUID VARCHAR(50), ";
     Map<String, ColumnDescribe> urlParam = urlInterface.getUrlParam();
     List<ColumnDescribe> orderColumns = orderColumn(urlParam);
     if (orderColumns == null)
       return; 
     for (int i = 0; i < orderColumns.size(); i++) {
       sql = sql + this.tableDeployService.buildColumnSQL((ColumnDescribe)orderColumns.get(i)) + ",";
     }
     if (urlInterface.getProdSql() == null) {
       urlInterface.setProdSql("");
     }
     else if (!"".equals(urlInterface.getProdSql()) && 
       !";".equals(String.valueOf(urlInterface.getProdSql().trim().charAt(urlInterface.getProdSql().trim().length() - 1)))) {
       urlInterface.setProdSql(urlInterface.getProdSql() + " ; ");
     } 
 
     
     sql = sql.substring(0, sql.length() - 1);
     sql = sql + ")BEGIN ";
     sql = sql + urlInterface.getProdSql();
     sql = sql + "END";
     this.tableDeployDao.execSQL(sql);
   }
   
   private List<ColumnDescribe> orderColumn(final Map<String, ColumnDescribe> urlParam) {
       if (urlParam == null || urlParam.size() == 0) {
           return null;
       }
       final List<ColumnDescribe> orderData = new ArrayList<ColumnDescribe>();
       for (final Map.Entry<String, ColumnDescribe> entry : urlParam.entrySet()) {
           orderData.add(entry.getValue());
       }
       Collections.sort(orderData, new Comparator<ColumnDescribe>() {
           @Override
           public int compare(final ColumnDescribe o1, final ColumnDescribe o2) {
               return o1.getSeq() - o2.getSeq();
           }
       });
       return orderData;
   }
 }


