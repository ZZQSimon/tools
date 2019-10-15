 package cn.com.easyerp.DeployTool.service;
 
 import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.easyerp.DeployTool.dao.ImportDeployDao;
import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.DeployTool.view.ImportDeployModel;
import cn.com.easyerp.DeployTool.view.ImportDeployRequestModel;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 
 
 
 
 
 @Service
 public class ImportDeployService
 {
   public static final String TMP_ROOT = "/tmp";
   @Autowired
   private ImportDeployDao importDeployDao;
   @Autowired
   private DataService dataService;
   @Autowired
   private TableDeployDao tableDeployDao;
   
   public ImportDeployModel getImportDeploy(String tableName) { return this.importDeployDao.getImportDeploy(tableName); }
 
 
   
   public Set<String> process(HttpServletRequest req) throws FileUploadException {
     if (!ServletFileUpload.isMultipartContent(req))
       throw new ApplicationException(this.dataService.getMessageText("request not supported", new Object[0])); 
     DiskFileItemFactory factory = new DiskFileItemFactory();
     factory.setRepository(new File(path(this.dataService.getSystemParam().getUpload_root() + "/tmp")));
     ServletFileUpload sfu = new ServletFileUpload(factory);
     List items = sfu.parseRequest(req);
     
     FileItem file = null;
     for (Object item : items) {
       FileItem fi = (FileItem)item;
       if (!fi.isFormField()) {
         file = fi;
       }
     } 
     if (file == null)
       return null; 
     String suffix = file.getName().substring(file.getName().lastIndexOf('.') + 1);
     return "csv".equals(suffix) ? getCsvHead(file) : getExcelHead(file, suffix);
   }
 
 
   
   public Set<String> getCsvHead(FileItem fi) {
     Set<String> rec = null;
     try {
       InputStream is = fi.getInputStream();
       Reader in = new InputStreamReader(new BOMInputStream(is), Charset.forName("GB2312"));
       CSVParser parse = CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withHeader(new String[0]).parse(in);
       List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
       if (parse != null) {
         rec = parse.getHeaderMap().keySet();
       
       }
     }
     catch (IOException e) {
       e.printStackTrace();
     } 
     return rec;
   }
 
   
   public Set<String> getExcelHead(FileItem fi, String suffix) {
     Set<String> rec = null;
     try {
       InputStream is = fi.getInputStream();
       //workbook = null;
       HSSFWorkbook hSSFWorkbook = new HSSFWorkbook(is);
       //HSSFWorkbook hSSFWorkbook = "xls".equals(suffix) ? new HSSFWorkbook(is) : new XSSFWorkbook(is);
       Sheet sheet = null;
       int start = 0;
       if (hSSFWorkbook.getNumberOfSheets() != 0) {
         sheet = hSSFWorkbook.getSheetAt(0);
         Map data = new HashMap<>();
         Row row = sheet.getRow(start);
         if (row != null) {
           for (int k = 0; k < row.getLastCellNum(); k++) {
             data.put(row.getCell(k).toString(), row.getCell(k).toString());
           }
         }
         rec = data.keySet();
       } 
     } catch (IOException e) {
       e.printStackTrace();
     } 
     return rec;
   }
 
   
   private String path(String str) { return str.replaceAll("\\\\", "/"); }
 
   
   public void saveImportDeploy(ImportDeployRequestModel request) {
     ImportDeployModel importDeploy = request.getImportDeploy();
     String tableName = importDeploy.getTable_id();
     int cnt = this.importDeployDao.isExistsImport(tableName);
     String templateName = tableName + "_template";
     List<String> i18nIds = this.importDeployDao.getI18nsByTable(templateName);
     if (cnt != 0) {
       
       if (i18nIds.size() != 0 && i18nIds != null) {
         this.importDeployDao.delI18nWithList(i18nIds);
       }
 
       
       this.importDeployDao.delTable(templateName);
       
       this.importDeployDao.delTableI18n(templateName);
       
       this.importDeployDao.delColumns(templateName);
       
       this.importDeployDao.dropTable(templateName);
       this.importDeployDao.updTableIsImport(tableName, "0");
     } 
     if (importDeploy.getBatch_id() == null || "".equals(importDeploy.getBatch_id())) {
       String batch_id = this.tableDeployDao.getId("c_batch", "").replaceFirst("^0*", "");
       importDeploy.setBatch_id(batch_id);
       this.importDeployDao.saveImportDeploy(importDeploy);
     } else {
       this.importDeployDao.updateImportDeploy(importDeploy);
     } 
     this.importDeployDao.updTableIsImport(tableName, "1");
   }
 
   
   public void createImportTemplate(ImportDeployRequestModel request) throws JsonParseException, JsonMappingException, IOException {
     String tableName = request.getImportDeploy().getTable_id();
     String nowTableName = tableName + "_template";
     TableDescribe table = this.importDeployDao.getTableDesctribeById(tableName);
     
     ObjectMapper om = new ObjectMapper();
     String column_mapper = request.getImportDeploy().getColumn_mapper();
     
     Map<String, Object> map = (Map)om.readValue(column_mapper, Map.class);
     Map<String, String> mapper = (Map)map.get("mapper");
 
     
     I18nDescribe il8nTable = this.importDeployDao.getI18NByid(tableName);
     if (il8nTable != null) {
       il8nTable.setInternational_id(nowTableName);
       il8nTable.setCn(il8nTable.getCn() + "导入模板");
       this.importDeployDao.insertI18N(il8nTable);
     } 
     
     table.setId(nowTableName);
     table.setInternational_id(nowTableName);
     this.importDeployDao.insertTable(table);
 
     
     List<ColumnDescribe> tmpColumns = new ArrayList<ColumnDescribe>();
     List<ColumnDescribe> newColumns = new ArrayList<ColumnDescribe>();
     List<ColumnDescribe> oldcolumns = this.importDeployDao.selectColumnDescribeByTable(tableName);
 
     
     ColumnDescribe key = new ColumnDescribe();
     key.setInternational_id(getI18nID());
     key.setTable_id(nowTableName);
     key.setColumn_name("_id");
     key.setVirtual(false);
     key.setData_type(13);
     key.setMax_len(Integer.valueOf(20));
     newColumns.add(key);
     
     for (ColumnDescribe column : oldcolumns) {
       column.setTable_id(nowTableName);
       column.setVirtual(false);
       if (13 == column.getData_type()) {
         column.setData_type(1);
       }
       column.setInternational_id(getI18nID());
       newColumns.add(column);
       String nowColumnName = column.getColumn_name();
       ColumnDescribe mapperColumn = new ColumnDescribe();
       if (mapper.containsKey(nowColumnName)) {
         mapperColumn.setTable_id(column.getTable_id());
         mapperColumn.setColumn_name((String)mapper.get(nowColumnName));
         mapperColumn.setVirtual(column.isVirtual());
         mapperColumn.setData_type(column.getData_type());
         mapperColumn.setMax_len(column.getMax_len());
         mapperColumn.setIs_multiple(column.getIs_multiple());
         mapperColumn.setHidden(column.isHidden());
         mapperColumn.setSeq(column.getSeq());
         mapperColumn.setCell_cnt(column.getCell_cnt());
         mapperColumn.setInternational_id(getI18nID());
         mapperColumn.setIs_id_column(column.getIs_id_column());
         tmpColumns.add(mapperColumn);
       } 
     } 
     newColumns.addAll(tmpColumns);
     
     ColumnDescribe errCode = new ColumnDescribe();
     errCode.setTable_id(nowTableName);
     errCode.setColumn_name("err_code");
     errCode.setInternational_id(getI18nID());
     errCode.setVirtual(false);
     errCode.setData_type(2);
     newColumns.add(errCode);
     ColumnDescribe errParams = new ColumnDescribe();
     errParams.setTable_id(nowTableName);
     errParams.setColumn_name("err_params");
     errParams.setInternational_id(getI18nID());
     errParams.setVirtual(false);
     errParams.setData_type(1);
     errParams.setMax_len(Integer.valueOf(500));
     newColumns.add(errParams);
 
     
     for (int i = 0; i < newColumns.size(); i++) {
       ColumnDescribe temp = (ColumnDescribe)newColumns.get(i);
       temp.setSeq(i + 1);
       this.importDeployDao.addColumn(temp);
       this.importDeployDao.addI18n(temp.getInternational_id(), temp.getColumn_name());
     } 
 
     
     table.setColumns(newColumns);
     this.importDeployDao.createTable(buildCreateTableSQL(table));
 
     
     String sql = createImportProcedure(mapper, tableName);
     this.importDeployDao.createImportProcedure(sql);
   }
 
   
   private String createImportProcedure(Map<String, String> mapper, String tableName) {
     String proName = ("import_" + tableName).toLowerCase();
     String updateSql = buildUpdateSql(mapper, tableName);
     String sql = " DROP PROCEDURE IF EXISTS " + proName + ";\n" + " CREATE PROCEDURE " + proName + "(IN uuid varchar(50),IN user_id varchar(20))\n" + " BEGIN\n" + " DECLARE buildSql varchar(5000);\n" + updateSql + " SET  @buildSql=buildSql;\n" + " PREPARE stmt FROM @buildSql;\n" + " EXECUTE stmt;\n" + " DEALLOCATE PREPARE stmt;\n" + "END\n";
 
 
 
 
 
 
 
 
 
     
     this.importDeployDao.createImportProcedure(sql);
     return sql;
   }
   
   private String buildUpdateSql(Map<String, String> mapper, String tableName) {
     String update = " SET buildSql=CONCAT('update " + tableName + "_',user_id,' set ";
     
     for (Map.Entry<String, String> entry : mapper.entrySet()) {
       update = update + "`" + (String)entry.getKey() + "`=`" + (String)entry.getValue() + "`,";
     }
     return update + "`err_code`=-1');\n";
   }
   
   private String buildCreateTableSQL(TableDescribe table) {
     if (table.getId() == null || "".equals(table.getId()))
       return null; 
     String sql = "CREATE TABLE if not exists `" + table.getId() + "` (";
     List<ColumnDescribe> columns = table.getColumns();
     List<String> idColumns = new ArrayList<String>();
     for (int i = 0; i < columns.size(); i++) {
       String columnSQL = buildColumnSQL((ColumnDescribe)columns.get(i));
       if (columnSQL != null) {
         sql = sql + columnSQL + ",";
       }
     } 
     sql = sql.substring(0, sql.length() - 1);
     return sql + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
   } private String buildColumnSQL(ColumnDescribe column) {
     int MaxLen;
     String data_format;
     if (column.getColumn_name() == null || "".equals(column.getColumn_name()))
       return null; 
     switch (column.getData_type()) {
       case 6:
         return "`" + column.getColumn_name() + "` varchar(" + ((column.getMax_len() == null) ? "20" : column.getMax_len()) + ")";
       case 1:
       case 7:
       case 8:
       case 9:
       case 10:
       case 13:
       case 14:
       case 15:
         return "`" + column.getColumn_name() + "` varchar(" + ((column.getMax_len() == null) ? "50" : column.getMax_len()) + ")";
       case 2:
       case 5:
         return "`" + column.getColumn_name() + "` int";
       case 3:
         data_format = (column.getData_format() == null) ? "" : column.getData_format();
         try {
           Integer.parseInt(data_format);
         } catch (Exception e) {
           data_format = "2";
         } 
         MaxLen = (column.getMax_len() == null) ? 10 : column.getMax_len().intValue();
         return "`" + column.getColumn_name() + "` decimal(" + MaxLen + "," + data_format + ")";
       case 4:
         return "`" + column.getColumn_name() + "` DATE";
       case 11:
         return "`" + column.getColumn_name() + "` TIME";
       case 12:
         return "`" + column.getColumn_name() + "` DATETIME";
     } 
     return null;
   }
 
 
   
   private String getI18nID() { return Common.generateShortUuid(); }
 
 
   
   public String getTableIdByBatchId(String batchId) { return this.importDeployDao.getTableIdByBatchId(batchId); }
 
 
   
   public void callSysImportApi(String sysApiName, String userId) { this.importDeployDao.callSysImportApi(sysApiName, userId); }
 }


