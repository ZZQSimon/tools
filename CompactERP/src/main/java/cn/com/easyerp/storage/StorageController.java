package cn.com.easyerp.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/storage" })
public class StorageController {
    @Autowired
    private StorageService service;

    @ResponseBody
    @RequestMapping(value = { "/upload.do" }, method = { RequestMethod.POST })
    public ActionResult upload(HttpServletRequest req, AuthDetails user) throws Exception {
        this.dataService.setRootPath(req.getServletContext().getRealPath("/"));
        return new ActionResult(true, this.service.process(req, user));
    }

    @Autowired
    private DataService dataService;

    private String filePath(FieldModelBase field) throws IOException {
        return filePath(field, true);
    }

    private String filePath(FieldModelBase field, boolean absolute) throws IOException {
        String path = (String) field.getValue();
        return absolute ? this.service.absolutePath(path) : path;
    }

    @SuppressWarnings("rawtypes")
    @ResponseBody
    @RequestMapping({ "/request.do" })
    public ActionResult download(@RequestBody WidgetRequestModelBase request, AuthDetails user) throws IOException {
        FieldModelBase field = ViewService.fetchFieldModel(request.getId());
        File file = new File(filePath(field));
        String fname = file.getName();
        String str = fname.substring(fname.indexOf(".") + 1);
        String name = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + user.getId() + "." + str;
        try {
            IOUtils.copy(new FileInputStream(file),
                    new FileOutputStream(new File(this.dataService.getRootPath() + "/tmp/" + name)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // String viewPath = this.service.getStorageFilePath((String) field.getValue());
        return this.service.createDownload(file.getName(), "/tmp/" + name, new FileInputStream(file),
                (int) file.length());
    }

    @ResponseBody
    @RequestMapping({ "/eventDownload.do" })
    public ActionResult eventDownload(@RequestBody FieldModelBase request) throws IOException {
        String filePath = request.getValue().toString();
        File file = new File(this.service.absolutePath(filePath));
        return this.service.createDownload(file.getName(), new FileInputStream(file), (int) file.length());
    }

    @ResponseBody
    @RequestMapping({ "/preview.do" })
    public void preview(@RequestParam("id") String id, HttpServletResponse resp) throws IOException {
        FileFieldModel field = (FileFieldModel) ViewService.fetchFieldModel(id);
        ServletOutputStream servletOutputStream = resp.getOutputStream();
        InputStream is = (field.getUuid() == null) ? new FileInputStream(filePath(field))
                : this.service.getUploadFile(field.getUuid(), false).getInputStream();
        IOUtils.copy(is, servletOutputStream);
    }

    @ResponseBody
    @RequestMapping({ "/thumbnail.do" })
    public void thumbnail(@RequestParam("id") String id, HttpServletResponse resp) throws IOException {
        ServletOutputStream servletOutputStream = resp.getOutputStream();
        FileFieldModel field = (FileFieldModel) ViewService.fetchFieldModel(id);
        if (field.getUuid() == null) {
            String path = filePath(field, false);
            if ("".equals(path))
                return;
            IOUtils.copy(this.service.thumbnail(path), servletOutputStream);
        } else {
            InputStream is = this.service.getUploadFile(field.getUuid(), false).getInputStream();
            this.service.thumbnail(is, servletOutputStream);
        }
    }

    @ResponseBody
    @RequestMapping({ "/download.do" })
    public void get(HttpServletRequest req, HttpServletResponse resp, @RequestParam("id") String id)
            throws IOException {
        this.service.download(req, resp, id);
    }
}