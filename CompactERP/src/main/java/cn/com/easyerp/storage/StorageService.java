package cn.com.easyerp.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.logger.Loggable;
import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.report.Excel2Pdf;
import net.coobird.thumbnailator.Thumbnails;

@Service
public class StorageService {
    class DownloadInfo {
        private String filename;
        private InputStream stream;
        private int size;

        public DownloadInfo(String filename, InputStream stream, int size) {
            this.stream = stream;
            this.filename = filename;
            this.size = size;
        }

        public String getFilename() {
            return this.filename;
        }

        public InputStream getStream() {
            return this.stream;
        }

        public int getSize() {
            return this.size;
        }
    }

    public static final String STORAGE_ROOT = "/storage/";
    public static final String TEMPLATE_ROOT = "/template/";
    public static final String THUMBNAIL_ROOT = "/thumbnail/";
    private static final Pattern WINDOWS_FILE_ILLEGAL_CHARS_PATTERN = Pattern.compile("[\\\\/:\\*\\?\"<>|]");

    public static final String TMP_ROOT = "/tmp";

    public static final String DOWNLOAD_URL = "/storage/download.do?id=";
    public static final int DEFAULT_BUFFER_SIZE = 10240;
    private ConcurrentHashMap<String, FileItem> uploadMap = new ConcurrentHashMap<>();
    private static final String SESSION_DOWNLOAD_KEY = "SESSION_DOWNLOAD_KEY";
    private static final String UPLOAD_ROLLBACK_KEY = "STORAGE_UPLOAD_ROLLBACK_KEY";

    @Loggable
    private Logger logger;
    @Autowired
    private DataService dataService;

    private void writeDownloadInfo(HttpServletRequest request, HttpServletResponse resp, String name, int size)
            throws IOException {
        String fname, agent = request.getHeader("USER-AGENT");
        if (agent != null) {
            if (agent.contains("Firefox")) {
                fname = "=?UTF-8?B?" + new String(Base64.encodeBase64(name.getBytes("UTF-8"))) + "?=";
            } else if (agent.contains("Chrome")) {
                fname = new String(name.getBytes(), "ISO8859-1");
            } else {
                fname = StringUtils.replace(URLEncoder.encode(name, "utf-8"), "+", "%20");
            }
        } else {
            fname = URLEncoder.encode(name, "UTF-8");
        }

        resp.reset();
        resp.setBufferSize(10240);
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Length", String.valueOf(size));
        resp.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", new Object[] { fname }));
    }

    public String put(FileItem fi) {
        String uuid = UUID.randomUUID().toString();
        this.uploadMap.put(uuid, fi);
        return uuid;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void rollback() {
        Map<String, FileItem> map = (Map) Common.getRequestObject(UPLOAD_ROLLBACK_KEY);
        if (map == null)
            return;
        this.uploadMap.putAll(map);
    }

    @SuppressWarnings({ "unchecked" })
    public String process(HttpServletRequest req) {
        if (!ServletFileUpload.isMultipartContent(req)) {
            throw new ApplicationException(this.dataService.getMessageText("request not supported", new Object[0]));
        }
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setRepository(new File(path(this.dataService.getSystemParam().getUpload_root() + "/tmp")));
            ServletFileUpload sfu = new ServletFileUpload(factory);
            List<FileItem> items = sfu.parseRequest(req);
            FileItem file = null;
            String id = null;
            for (Object item : items) {
                FileItem fi = (FileItem) item;
                if (!fi.isFormField()) {
                    file = fi;
                    continue;
                }
                if ("id".equals(fi.getFieldName()))
                    id = fi.getString();
            }
            if (file == null)
                return null;
            if (id != null) {
                FileFieldModel field = (FileFieldModel) ViewService.fetchFieldModel(id);
                String uuid = put(file);
                field.setUuid(uuid);
                return file.getName();
            }
            return put(file);
        } catch (FileUploadException e) {
            throw new ApplicationException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public String process(HttpServletRequest req, AuthDetails user) throws Exception {
        if (!ServletFileUpload.isMultipartContent(req)) {
            throw new ApplicationException(this.dataService.getMessageText("request not supported", new Object[0]));
        }
        try {
            DiskFileItemFactory factory = new DiskFileItemFactory();

            File tmpfile = new File(path(this.dataService.getRootPath() + "/tmp"));
            if (!tmpfile.exists())
                tmpfile.mkdirs();
            factory.setRepository(tmpfile);
            ServletFileUpload sfu = new ServletFileUpload(factory);
            List items = sfu.parseRequest(req);
            FileItem file = null;
            String id = null;
            String name = "";
            for (Object item : items) {
                FileItem fi = (FileItem) item;
                if (!fi.isFormField()) {
                    file = fi;
                    String fname = fi.getName();
                    String str = fname.substring(fname.indexOf(".") + 1);
                    name = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + user.getId() + "." + str;
                    File f = new File(this.dataService.getRootPath() + getTmpFilePath(name));
                    InputStream in = fi.getInputStream();
                    OutputStream os = new FileOutputStream(f);
                    byte[] b = new byte[1024];
                    int len = 0;
                    while ((len = in.read(b)) != -1) {
                        os.write(b, 0, len);
                    }
                    os.flush();
                    os.close();
                    in.close();
                    continue;
                }
                if ("id".equals(fi.getFieldName()))
                    id = fi.getString();
            }
            if (file == null)
                return null;
            if (id != null && !"".equals(id)) {
                FileFieldModel field = (FileFieldModel) ViewService.fetchFieldModel(id);
                String uuid = put(file);
                field.setUuid(uuid);
                return "/tmp/" + name + "|" + file.getName();
            }
            return put(file);
        } catch (FileUploadException e) {
            throw new ApplicationException(e);
        }
    }

    public String name(String uuid) {
        return ((FileItem) this.uploadMap.get(uuid)).getName();
    }

    private String makeSavedFile(String path, String name, InputStream is) throws IOException {
        String base, suffix;
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        int index = name.lastIndexOf('.');
        if (index >= 0) {
            base = name.substring(0, index);
            suffix = name.substring(index);
        } else {
            base = name;
            suffix = "";
        }
        String tmp = base;
        for (int i = 0;; tmp = base + "(" + ++i + ")") {
            File newFile = new File(path + tmp + suffix);
            if (!newFile.exists()) {
                if (".jpg".equals(suffix) || ".png".equals(suffix) || ".bmp".equals(suffix) || ".jpeg".equals(suffix)
                        || ".gif".equals(suffix)) {
                    Thumbnails.of(new InputStream[] { is }).scale(1.0D).outputQuality(0.25F).toFile(newFile);
                } else {
                    IOUtils.copy(is, new FileOutputStream(newFile));
                }
                return tmp + suffix;
            }
        }
    }

    public FileItem getUploadFile(String uuid) {
        return getUploadFile(uuid, true);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, FileItem> getRollbackMap() {
        Map<String, FileItem> map = (Map) Common.getRequestObject(UPLOAD_ROLLBACK_KEY);
        if (map == null)
            Common.setRequestObject(UPLOAD_ROLLBACK_KEY, map = new HashMap<String, FileItem>());
        return map;
    }

    public FileItem getUploadFile(String uuid, boolean remove) {
        if (uuid == null)
            return null;
        FileItem fi = remove ? (FileItem) this.uploadMap.remove(uuid) : (FileItem) this.uploadMap.get(uuid);
        if (fi == null) {
            fi = (FileItem) getRollbackMap().get(uuid);
            if (fi == null)
                throw new ApplicationException("can not get uploaded file with uuid: " + uuid);
        } else if (remove) {
            getRollbackMap().put(uuid, fi);
        }
        return fi;
    }

    public String saveUploadField(ColumnDescribe desc, String uuid) throws IOException {
        String valuePath = desc.getTable_id() + "/" + desc.getColumn_name() + "/";
        String pathname = absolutePath(valuePath);
        mkdirs(pathname);
        FileItem fi = getUploadFile(uuid);
        try {
            return valuePath + makeSavedFile(pathname, fi.getName(), fi.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApplicationException(e);
        }
    }

    private String path(String str) {
        return str.replaceAll("\\\\", "/");
    }

    public String escape(String name) {
        return WINDOWS_FILE_ILLEGAL_CHARS_PATTERN.matcher(name).replaceAll("_");
    }

    public File tmp(String prefix, String suffix) {
        String path = path(this.dataService.getRootPath() + "/tmp");
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            prefix = "prefix";
            return File.createTempFile(prefix, suffix, file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ApplicationException(
                    String.format("failed create tmp file: prefix=\"%s\", suffix=\"%s\", path=\"%s\"",
                            new Object[] { prefix, suffix, path }));
        }
    }

    public String getTmpFilePath(String prefix) {
        String path = path("/tmp/" + prefix);
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        return path;
    }

    public String getStorageFilePath(String prefix) {
        String path = path("/storage//" + prefix);
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        return path;
    }

    public File template(String id, String name, AuthDetails user) throws IOException {
        String path = path(this.dataService.getSystemParam().getUpload_root() + "/template/" + user.getLanguage_id()
                + "/" + id + "/" + name);
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        return new File(path);
    }

    public String templatePath(String id, String name) throws IOException {
        String path = path(this.dataService.getSystemParam().getUpload_root() + "/tmp" + "/" + id + name);
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        return path;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionResult createDownload(String filename, InputStream stream, int size) {
        ConcurrentHashMap<String, DownloadInfo> downloadCache = (ConcurrentHashMap) Common
                .getSessionObject(SESSION_DOWNLOAD_KEY);
        if (downloadCache == null) {
            Common.putSessionObject(SESSION_DOWNLOAD_KEY,
                    downloadCache = new ConcurrentHashMap<String, DownloadInfo>());
        }
        DownloadInfo di = new DownloadInfo(filename, stream, size);
        String uuid = UUID.randomUUID().toString();
        downloadCache.put(uuid, di);
        return new ActionResult(true, "/storage/download.do?id=" + uuid);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionResult createDownload(String filename, String viewPath, InputStream stream, int size) {
        if (viewPath.indexOf(".\\") != -1 || viewPath.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        ConcurrentHashMap<String, DownloadInfo> downloadCache = (ConcurrentHashMap) Common
                .getSessionObject(SESSION_DOWNLOAD_KEY);
        if (downloadCache == null) {
            Common.putSessionObject(SESSION_DOWNLOAD_KEY,
                    downloadCache = new ConcurrentHashMap<String, DownloadInfo>());
        }
        DownloadInfo di = new DownloadInfo(filename, stream, size);
        String uuid = UUID.randomUUID().toString();
        downloadCache.put(uuid, di);
        Map<String, String> urlParam = new HashMap<String, String>();
        urlParam.put("viewUrl", viewPath);
        urlParam.put("downloadUrl", "/storage/download.do?id=" + uuid);
        return new ActionResult(true, urlParam);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void download(HttpServletRequest req, HttpServletResponse resp, @RequestParam("id") String id)
            throws IOException {
        ConcurrentHashMap<String, DownloadInfo> downloadCache = (ConcurrentHashMap) Common
                .getSessionObject(SESSION_DOWNLOAD_KEY);
        DownloadInfo di = (DownloadInfo) downloadCache.remove(id);
        writeDownloadInfo(req, resp, di.getFilename(), di.getSize());
        ServletOutputStream servletOutputStream = resp.getOutputStream();
        IOUtils.copy(di.getStream(), servletOutputStream);
    }

    public String absolutePath(String path) throws IOException {
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        return absolutePath(path, "/storage/");
    }

    private String absolutePath(String path, String root) throws IOException {
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        String storageRoot = this.dataService.getSystemParam().getUpload_root().replace("\\", "/") + root;
        return storageRoot + path;
    }

    public byte[] loadFileToByteArray(String path) {
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        try {
            InputStream is = loadFile(path);
            if (is == null)
                return null;
            return IOUtils.toByteArray(is);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public InputStream loadFile(String path) throws IOException {
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        try {
            String filePath = absolutePath(path);
            File file = new File(filePath);
            if (!file.exists() || !file.isFile())
                return null;
            return new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new ApplicationException(e);
        }
    }

    private void mkdirs(String path) {
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            throw new ApplicationException("failed create path: \"" + path + "\"");
        }
    }

    private String folderPath(File file) {
        return (new File(file.getParent())).getAbsolutePath();
    }

    public void thumbnail(InputStream is, OutputStream os) {
        int thumbnail_height = this.dataService.getSystemParam().getThumbnail_height();
        int thumbnail_width = this.dataService.getSystemParam().getThumbnail_width();
        if (thumbnail_width == 0) {
            thumbnail_width = thumbnail_height;
        }
        try {
            Thumbnails.of(new InputStream[] { is }).size(thumbnail_width, thumbnail_height).toOutputStream(os);
        } catch (IOException e) {
            this.logger.error(e.getMessage());
            throw new ApplicationException(e);
        }
    }

    public InputStream thumbnail(String path) {
        if (path.indexOf(".\\") != -1 || path.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        try {
            int thumbnail_height = this.dataService.getSystemParam().getThumbnail_height();
            int thumbnail_width = this.dataService.getSystemParam().getThumbnail_width();
            if (thumbnail_width == 0)
                thumbnail_width = thumbnail_height;
            File thumbnail = new File(absolutePath(path, "/thumbnail/"));
            if (!thumbnail.exists()) {
                mkdirs(folderPath(thumbnail));
                String filePath = absolutePath(path);
                Thumbnails.of(new String[] { filePath }).size(thumbnail_width, thumbnail_height).toFile(thumbnail);
            }
            return new FileInputStream(thumbnail);
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    public File convertXlsToPdf1(File xls) {
        String cmd, LOCmd[] = new String[7];
        if (SystemUtils.IS_OS_WINDOWS) {
            cmd = this.dataService.getSystemParam().getPdf_lib_path() + "\\program\\soffice";
        } else {
            cmd = this.dataService.getSystemParam().getPdf_lib_path() + "/soffice";
        }
        LOCmd[0] = cmd.replace('\\', '/');
        LOCmd[1] = "--headless";
        LOCmd[2] = "--convert-to";
        LOCmd[3] = "pdf";
        LOCmd[4] = "--outdir";
        try {
            String path = xls.getAbsolutePath();
            LOCmd[5] = folderPath(xls);
            LOCmd[6] = path;
            Process p = Runtime.getRuntime().exec(LOCmd);
            p.waitFor();
            return new File(path.substring(0, path.length() - 3) + "pdf");
        } catch (IOException | InterruptedException e) {
            throw new ApplicationException(e);
        }
    }

    public File convertXlsToPdf(File xls) {
        String excelPath = xls.getAbsolutePath();
        String pdfPath = excelPath.substring(0, excelPath.length() - 3) + "pdf";
        return Excel2Pdf.excel2pdf(excelPath, pdfPath);
    }
}