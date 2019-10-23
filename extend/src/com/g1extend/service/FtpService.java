package com.g1extend.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.util.Map;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.stereotype.Service;

@Service
public class FtpService {
    private static String ftpHost = "192.168.1.114";
    private static String ftpPassword = "raypp123";
    private static String ftpUserName = "administrator";
    private static int ftpPort = 21;

    public static FTPClient getFTPClient() {
        FTPClient ftpClient = null;
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(ftpHost, ftpPort);

            if (!ftpClient.login(ftpUserName, ftpPassword)) {
                ftpClient.disconnect();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ftpClient;
    }

    public void upload(String ftpPath, Map<String, InputStream> ins) {
        FTPClient ftpClient = null;
        try {
            ftpClient = getFTPClient();

            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(2);

            String remoteFileName = ftpPath;
            if (ftpPath.contains("/")) {
                remoteFileName = ftpPath.substring(ftpPath.lastIndexOf("/") + 1);
            }
            for (Map.Entry<String, InputStream> in : ins.entrySet()) {
                ftpClient.storeFile((String) in.getKey(), (InputStream) in.getValue());
                ((InputStream) in.getValue()).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean write(String fileName, String fileContext, String writeTempFielPath) {
        try {
            File f = new File(String.valueOf(writeTempFielPath) + "/" + fileName);
            if (!f.exists()) {
                f.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
            bw.write(fileContext.replaceAll("\n", "\r\n"));
            bw.flush();
            bw.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

/*
 * Location: E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend
 * 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\FtpService.class Java
 * compiler version: 8 (52.0) JD-Core Version: 1.0.6
 */