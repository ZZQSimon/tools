package cn.com.easyerp.core.ftp;
 
 import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
 
public class FtpService {
   private String ftpHost;
   private int ftpPort;
   private String ftpUserName;
   private String ftpPassword;
   private FTPClient ftpClient;
   
   public FtpService(String ftpHost, int ftpPort, String ftpUserName, String ftpPassword) {
     this.ftpHost = ftpHost;
     this.ftpPort = ftpPort;
     this.ftpUserName = ftpUserName;
     this.ftpPassword = ftpPassword;
     this.ftpClient = new FTPClient();
   }
   
   private FTPClient login() {
     try {
       this.ftpClient.connect(this.ftpHost, this.ftpPort);
       
       if (!this.ftpClient.login(this.ftpUserName, this.ftpPassword)) {
         this.ftpClient.disconnect();
       }
     } catch (SocketException e) {
       e.printStackTrace();
     } catch (IOException e) {
       e.printStackTrace();
     } 
     return this.ftpClient;
   }
   
   public void upload(String ftpPath, Map<String, InputStream> ins) {
     try {
       this.ftpClient = login();
       this.ftpClient.enterLocalPassiveMode();
       this.ftpClient.setFileType(2);
       String remoteFileName = ftpPath;
       if (ftpPath.contains("/"))
       {
         remoteFileName = ftpPath.substring(ftpPath.lastIndexOf("/") + 1);
       }
       this.ftpClient.changeWorkingDirectory(remoteFileName);
       for (Map.Entry<String, InputStream> in : ins.entrySet()) {
         this.ftpClient.storeFile((String)in.getKey(), (InputStream)in.getValue());
         ((InputStream)in.getValue()).close();
       } 
     } catch (Exception e) {
       e.printStackTrace();
     } finally {
       try {
         for (Map.Entry<String, InputStream> in : ins.entrySet()) {
           ((InputStream)in.getValue()).close();
         }
         this.ftpClient.disconnect();
       } catch (IOException e) {
         e.printStackTrace();
       } finally {
         try {
           this.ftpClient.disconnect();
         } catch (IOException e) {
           e.printStackTrace();
         } 
       } 
     } 
   }
 }


