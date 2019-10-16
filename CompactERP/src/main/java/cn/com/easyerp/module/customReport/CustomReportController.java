package cn.com.easyerp.module.customReport;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/customReport" })
public class CustomReportController extends FormViewControllerBase {
    public static String FILTER_TABLE = "";
    public static final String FILTER_BUTTON_LABEL = "Report";
    public static final String DOWNLOAD_URL = "/customReport/download.do?id=";
    @Autowired
    private CustomReportService customReportService;
    @Autowired
    private PrintExcelService printExcelService;

    @RequestMapping({ "/customReport.view" })
    public ModelAndView customReport(@RequestBody CustomReportRequestModel request) {
        FilterModel filter = new FilterModel(request.getFilterTable(), "Report", null, null, null);

        CustomReportModel form = new CustomReportModel(request.getParent(), request.getFilterTable(), filter);
        FILTER_TABLE = request.getFilterTable();
        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/search.do" })
    public ActionResult search(@RequestBody CustomReportRequestModel param, HttpSession session) {
        List<String> allMonth = new ArrayList<String>();
        List<CustomData> customDatas = this.customReportService.search(param, allMonth);
        ActionResult result = this.printExcelService.excelPrint(customDatas, allMonth);
        String uuid = UUID.randomUUID().toString();
        session.setAttribute(uuid, result.getData());
        result.setData("/customReport/download.do?id=" + uuid);
        return result;
    }

    @RequestMapping({ "/download.do" })
    public void download(HttpServletRequest request, HttpServletResponse response, HttpSession session,
            @RequestParam("id") String id) {
        String filePath = session.getAttribute(id).toString();
        if (filePath.indexOf(".\\") != -1 || filePath.indexOf("./") != -1) {
            throw new ApplicationException("url is illegal");
        }
        try {
            InputStream fis = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();

            response.reset();

            response.addHeader("Content-Disposition", "attachment;filename=rate.xls");
            response.setContentType("application/octet-stream");
            ServletOutputStream servletOutputStream = response.getOutputStream();
            servletOutputStream.write(buffer);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (Exception e) {
        }
    }
}
