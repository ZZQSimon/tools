 package cn.com.easyerp.DeployTool.service;
 
 import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.DeployTool.dao.InternationalDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.storage.StorageService;
 
 
 
 
 
 
 
 
 @Service
 public class InternationalService
 {
   public static final String TMP_ROOT = "/tmp";
   @Autowired
   private DataService dataService;
   @Autowired
   private InternationalDao internationalDao;
   @Autowired
   private StorageService storageService;
   
   public boolean process(HttpServletRequest req) {
     if (!ServletFileUpload.isMultipartContent(req))
       throw new ApplicationException(this.dataService.getMessageText("request not supported", new Object[0])); 
     try {
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
         return false; 
       return getCsvContext(file);
     } catch (FileUploadException e) {
       throw new ApplicationException(e);
     } 
   }
 
   
   public boolean getCsvContext(FileItem fi) {
     Map<String, String> rec = null;
     try {
       InputStream is = fi.getInputStream();
       Reader in = new InputStreamReader(new BOMInputStream(is));
       CSVParser parse = CSVFormat.DEFAULT.withDelimiter(',').withQuote('"').withHeader(new String[0]).parse(in);
       List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
       International il8n = new International();
       for (CSVRecord record : parse) {
         rec = record.toMap();
         il8n.setInternational_id((String)rec.get("internatiional_id"));
         il8n.setType(Integer.valueOf((String)rec.get("type")).intValue());
         il8n.setCn((String)rec.get("cn"));
         il8n.setEn((String)rec.get("en"));
         il8n.setJp((String)rec.get("jp"));
         il8n.setOther1((String)rec.get("other1"));
         il8n.setOther2((String)rec.get("other2"));
         
         int cnt = this.internationalDao.isExistsi18n(il8n.getInternational_id());
         if (cnt != 0) {
           this.internationalDao.I18nUpdate(il8n); continue;
         } 
         this.internationalDao.saveRetrievalInter(il8n);
       }
     
     } catch (IOException e) {
       e.printStackTrace();
       return false;
     } 
     return true;
   }
 
 
   
   private String path(String str) { return str.replaceAll("\\\\", "/"); }
 
   
   public ActionResult export(String csvFileName, List<String> columnsNames, String params) throws IOException {
     List<String> values = new ArrayList<String>();
     List<DatabaseDataMap> keys = this.internationalDao.selectI18nWithCondition(params);
 
     
     File csvFile = this.storageService.tmp("export", "csv");
     FileOutputStream fos = new FileOutputStream(csvFile);
     OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
     fos.write(new byte[] { -17, -69, -65 });
     BufferedWriter buffer = new BufferedWriter(osw, 'Ѐ');
     
     buffer.write(Common.join(columnsNames, ",", "\""));
     buffer.newLine();
     
     for (DatabaseDataMap record : keys) {
       for (String key : columnsNames) {
         Object value = record.get(key);
         values.add((value == null) ? "" : value.toString());
       } 
       
       buffer.write(Common.join(values, ",", "\""));
       buffer.newLine();
       values.clear();
     } 
     
     buffer.flush();
     buffer.close();
     FileInputStream fileName = new FileInputStream(csvFile);
     int fileSize = fileName.available();
     return this.storageService.createDownload(csvFileName, fileName, fileSize);
   }
 }


