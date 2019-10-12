package cn.com.easyerp.framework.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.csvreader.CsvWriter;

import cn.com.easyerp.framework.config.Constants;

/**
 * 文件操作Util类 <br>
 * 本类使用了try-with-resources 故需要最低使用JDK1.8 <br>
 * 
 * @author Simon Zhang
 */
public class FileUtils {

    public static final String CHARSET_UTF8 = "UTF8";
    public static final String CHARSET_IBM290 = "IBM290";
    public static final String CHARSET_JISAUTODETECT = "x-JISAutoDetect";
    public static final String CHARSET_SHIFTJIS = "Shift-JIS";

    public static final String UNDER_LINE = "_";

    /**
     * ASCII->EBCDIC
     * 
     * @throws UnsupportedEncodingException
     */
    public static String ASCIIToEBCDIC(String ascii) throws UnsupportedEncodingException {
        return stringCharsetConversion(ascii, CHARSET_IBM290);
    }

    /**
     * String EBCDIC->ASCII
     * 
     * @throws UnsupportedEncodingException
     */
    public static String EBCDICToASCII(String ebcdic) throws UnsupportedEncodingException {
        return stringCharsetConversion(ebcdic, CHARSET_UTF8);
    }

    /**
     * 字符串字符集转换
     * 
     * @param sourceString
     *            源字符串
     * @param fromCharsetName
     *            源文件字符集
     * @param toCharsetName
     *            目标文件字符集
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String stringCharsetConversion(String sourceString, String toCharsetName)
            throws UnsupportedEncodingException {
        if (EmptyUtil.isEmpty(toCharsetName)) {
            throw new java.lang.IllegalArgumentException("toCharsetName is blank");
        }
        byte[] tobytes = sourceString.getBytes(toCharsetName);
        return new String(tobytes, toCharsetName);
    }

    /**
     * 读取目录下所有文件的文件名
     * 
     * @param directory
     *            文件夹
     * @return
     */
    public static ArrayList<String> listFileNameList(String directory) {
        File localpath = new File(directory);
        ArrayList<String> nameList = new ArrayList<String>();
        if (localpath.isDirectory()) {
            File[] tempList = localpath.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                File file = tempList[i];
                if (file.isDirectory()) { // 文件夹，跳过
                    continue;
                }
                if (file.isFile()) {
                    nameList.add(file.getName());
                    continue;
                }
            }
        }
        return nameList;
    }

    /**
     * 列出目录下符合规则的文件名称
     * 
     * @param directory
     *            文件夹
     * @param formatPrefix
     *            文件名前缀
     * @param formatSuffix
     *            文件名后缀
     * @return
     */
    public static List<String> listLegalFileNames(String directory, String formatPrefix, String formatSuffix) {
        File localpath = new File(directory);
        ArrayList<String> nameList = new ArrayList<String>();
        if (localpath.isDirectory()) {
            File[] tempList = localpath.listFiles();
            for (int i = 0; i < tempList.length; i++) {
                File file = tempList[i];
                if (file.isDirectory()) { // 文件夹，跳过
                    continue;
                }
                if (file.isFile()) {
                    if (isNameLegal(file.getName(), formatPrefix, formatSuffix)) {
                        nameList.add(file.getName());
                    } else {
                        continue;
                    }
                }
            }
        }
        return nameList;
    }

    /**
     * 检测文件名是否符合规则
     * 
     * @param filename
     *            文件名
     * @param formatPrefix
     *            文件名合法前缀
     * @param formatSuffix
     *            文件名合法后缀
     * @return
     */
    public static boolean isNameLegal(String filename, String formatPrefix, String formatSuffix) {
        // 规范化前缀后缀
        formatPrefix = EmptyUtil.isEmpty(formatPrefix) ? "" : formatPrefix.trim();
        formatSuffix = EmptyUtil.isEmpty(formatSuffix) ? "" : formatSuffix.trim();
        if (formatPrefix.length() > 0 && formatSuffix.length() > 0) {
            // 前后缀都生效
            if (filename.startsWith(formatPrefix) && filename.endsWith(formatSuffix)) {
                return true;
            } else {
                return false;
            }
        } else if (formatPrefix.length() > 0 && "".equals(formatSuffix)) {
            // 前缀成效
            if (filename.startsWith(formatPrefix)) {
                return true;
            } else {
                return false;
            }
        } else if (formatSuffix.length() > 0 && "".equals(formatPrefix)) {
            // 后缀生效
            if (filename.endsWith(formatSuffix)) {
                return true;
            } else {
                return false;
            }
        } else {
            // 都不生效，直接返回ture
            return true;
        }
    }

    /**
     * 检测文件名是否不符合规则
     * 
     * @param filename
     *            文件名
     * @param formatPrefix
     *            文件名合法前缀
     * @param formatSuffix
     *            文件名合法后缀
     * @return 符合规则 返回false
     */
    public static boolean isNameIllegal(String filename, String formatPrefix, String formatSuffix) {
        return !isNameLegal(filename, formatPrefix, formatSuffix);
    }

    /**
     * 删除本地文件
     * 
     * @param filePath
     * @return
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        if (!file.isFile()) {
            return false;
        }
        return file.delete();
    }

    /**
     * 取得XML文件文本
     * 
     * @param xmlFile
     * @return
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DocumentException
     */
    public static String getXmlContent(File xmlFile) throws FileNotFoundException, IOException, DocumentException {
        try (FileInputStream fis = new FileInputStream(xmlFile.getAbsolutePath());) {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(fis);
            return document.asXML();
        }
    }

    /**
     * 取得指定文件名的文件中文本内容
     * 
     * @param fileName
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static String getFileContent(String fileName) throws FileNotFoundException, IOException {
        return getFileContent(fileName, null);
    }

    /**
     * 根据指定字符集取得指定文件名的文件中文本内容
     * 
     * @param sourceFileName
     * @param charset
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getFileContent(String sourceFileName, String charset)
            throws FileNotFoundException, IOException {
        try (FileInputStream in = new FileInputStream(new File(sourceFileName));) {
            int tempint, i = 0;
            byte[] tempbytes = new byte[in.available()];
            while ((tempint = in.read()) != -1) {
                tempbytes[i++] = (byte) tempint;
            }
            if (EmptyUtil.isEmpty(charset)) {
                return new String(tempbytes);
            }
            return new String(tempbytes, charset);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 根据指定字符集取得jar包内指定文件名的文件中文本内容
     * 
     * @param sourceFileName
     * @param charset
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String getFileContentInJar(Class<?> clazz, String sourceFileName, String charset)
            throws FileNotFoundException, IOException {
        try (InputStream in = clazz.getClassLoader().getResourceAsStream(sourceFileName);) {
            int tempint, i = 0;
            byte[] tempbytes = new byte[in.available()];
            while ((tempint = in.read()) != -1) {
                tempbytes[i++] = (byte) tempint;
            }
            if (EmptyUtil.isEmpty(charset)) {
                return new String(tempbytes);
            }
            return new String(tempbytes, charset);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Writes, or overwrites, the contents of the specified file
     * 
     * @param file
     * @param content
     * @param fileEncoding
     * @throws IOException
     */
    public static void writeFile(File file, String content, String fileEncoding) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file, false)) {
            OutputStreamWriter osw;
            if (EmptyUtil.isEmpty(fileEncoding)) {
                osw = new OutputStreamWriter(fos);
            } else {
                osw = new OutputStreamWriter(fos, fileEncoding);
            }
            try (BufferedWriter bw = new BufferedWriter(osw)) {
                bw.write(content);
            }
        }
    }

    /**
     * 修正路径，使其以文件夹分隔符结尾
     * 
     * @param path
     * @return
     */
    public static String fixPathEndWithSeparator(String path) {
        if (!path.endsWith("\\")) {
            if (!path.endsWith("/")) {
                return path + File.separator;
            }
        }
        return path;
    }

    /**
     * 使用 javacsv2.0 写csv文件
     * 
     * @param filePath
     *            文件路径
     * @param headers
     *            csv文件表头
     * @param contentList
     *            csv文件内容列表集合
     * @throws IOException
     *             写文件时候可能会抛出文件未找到灯IO异常
     */
    public static void writeCSV(String filePath, String[] headers, List<String[]> contentList) throws IOException {
        // 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
        CsvWriter csvWriter = new CsvWriter(filePath, Constants.CSV_SPLIT, Charset.forName("UTF-8"));
        // 写表头
        if (EmptyUtil.isNotEmpty(headers)) {
            csvWriter.writeRecord(headers);
        }
        // 写内容
        if (EmptyUtil.isNotEmpty(contentList)) {
            for (String[] content : contentList) {
                if (EmptyUtil.isNotEmpty(content)) {
                    csvWriter.writeRecord(content);
                }
            }
        }
        csvWriter.close();
    }

    /**
     * 文件文本字符集转换
     * 
     * @param fromfile
     *            源文件名称
     * @param tofile
     *            目标文件名称
     * @param fromCharsetName
     *            源文件字符集
     * @param toCharsetName
     *            目标文件字符集
     * @throws IOException
     */
    public static void fileCharsetConversion(String fromfile, String tofile, String fromCharsetName,
            String toCharsetName) throws IOException {
        if (EmptyUtil.isEmpty(fromCharsetName)) {
            throw new java.lang.IllegalArgumentException("fromCharsetName is empty");
        }
        if (EmptyUtil.isEmpty(toCharsetName)) {
            throw new java.lang.IllegalArgumentException("toCharsetName is empty");
        }

        String fromStr = getFileContent(fromfile, fromCharsetName);
        byte[] tobytes = fromStr.getBytes(toCharsetName);

        try (FileOutputStream out = new FileOutputStream(new File(tofile));) {
            out.write(tobytes);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * 删除文本中的换行符‘\n’和'\r'
     * 
     * @param source
     * @return
     */
    public static String replaceNewLine(String source) {
        if (EmptyUtil.isEmpty(source)) {
            return "";
        }
        return source.replaceAll(Constants.newliner, "").replaceAll(Constants.newlinen, "");
    }

    /**
     * 创建路径
     * 
     * @param path
     * @throws IOException
     */
    public static void mkdirs(String path) {
        File dest = new File(path);
        boolean exists = dest.exists();
        if (!exists) {
            dest.mkdirs();
        }
    }

    /**
     * 取得纯文件名
     * 
     * @param filename
     * @return
     */
    public static String getSingleFileName(String filename) {

        int lastFileSeparator = filename.lastIndexOf(File.separator);
        if (lastFileSeparator > 0) {
            return (filename.substring(lastFileSeparator + 1));
        }

        int lastPie = filename.lastIndexOf("/");
        if (lastPie > 0) {
            return (filename.substring(lastPie + 1));
        }

        int lastNa = filename.lastIndexOf("\\");
        if (lastNa > 0) {
            return (filename.substring(lastNa + 1));
        }

        return filename;
    }

    /**
     * 根据日期生成备份文件路径
     * 
     * @param root
     * @return
     */
    public static String genBackupPath(String root) {
        return MessageUtil.formmatString(root, DateUtil.toStrDT_(new Date()));
    }

    /**
     * 根据日期生成备份错误文件路径
     * 
     * @param root
     * @return
     */
    public static String genErrorBackupPath(String root) {
        return MessageUtil.formmatString(root, DateUtil.toStrDT_(new Date()) + "-error");
    }
}
