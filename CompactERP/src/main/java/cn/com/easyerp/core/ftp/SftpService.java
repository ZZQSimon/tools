 package cn.com.easyerp.core.ftp;
 
 import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
 
 
 
 
 public class SftpService
 {
   private ChannelSftp sftp = null;
   
   private Session sshSession = null;
   
   private String username;
   
   private String password;
   
   private String host;
   
   private int port;
   
   public SftpService(String username, String password, String host, int port) {
     this.username = username;
     this.password = password;
     this.host = host;
     this.port = port;
   }
 
 
 
 
 
 
   
   public ChannelSftp connect() {
     JSch jsch = new JSch();
     try {
       jsch.getSession(this.username, this.host, this.port);
       this.sshSession = jsch.getSession(this.username, this.host, this.port);
       this.sshSession.setPassword(this.password);
       
       Properties properties = new Properties();
       properties.put("StrictHostKeyChecking", "no");
       this.sshSession.setConfig(properties);
       this.sshSession.connect();
       
       Channel channel = this.sshSession.openChannel("sftp");
       channel.connect();
       
       this.sftp = (ChannelSftp)channel;
     }
     catch (JSchException e) {
       e.printStackTrace();
     } 
     return this.sftp;
   }
 
 
 
 
 
 
 
   
   public void uploadFile(String ftpPath, Map<String, InputStream> ins) {
     connect();
     try {
       this.sftp.cd(ftpPath);
     } catch (SftpException e) {
       try {
         this.sftp.mkdir(ftpPath);
         this.sftp.cd(ftpPath);
       } catch (SftpException e1) {
         e1.printStackTrace();
       } 
     } 
 
     
     try {
       for (Map.Entry<String, InputStream> in : ins.entrySet()) {
         this.sftp.put((InputStream)in.getValue(), (String)in.getKey());
       }
     }
     catch (SftpException e) {
       e.printStackTrace();
     } finally {
       for (Map.Entry<String, InputStream> in : ins.entrySet()) {
         if (in != null) {
           try {
             ((InputStream)in.getValue()).close();
           }
           catch (IOException e) {
             e.printStackTrace();
           } 
         }
       } 
     } 
     disconnect();
   }
 
 
 
 
   
   public void disconnect() {
     if (this.sftp != null && 
       this.sftp.isConnected()) {
       this.sftp.disconnect();
       this.sftp = null;
     } 
     
     if (this.sshSession != null && 
       this.sshSession.isConnected()) {
       this.sshSession.disconnect();
       this.sshSession = null;
     } 
   }
 }


