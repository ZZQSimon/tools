package cn.com.easyerp.auth.externalForm;

import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;

@Controller
@RequestMapping({ "/externalForm" })
public class ExternalFormControll extends FormViewControllerBase {
    public static final String OUT_PAGE_URL = "externalForm/getExternalFormData.view";
    // @Autowired
    // private ExternalFormDao externalFormDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private DataService dataService;

    @ResponseBody
    @RequestMapping({ "/getEncryptStr.do" })
    public ActionResult getExternalFormUrl(@RequestBody ExternalFormRequestModel request) {
        ExternalFormModel form = new ExternalFormModel(request.getParent());
        String strDES = "";
        try {
            strDES = this.dataService.des.encrypt(request.getJsondata());
        } catch (Exception e) {
            return new ActionResult(true, "encrypt lose");
        }
        form.setEncryptStr(strDES);
        return new ActionResult(true, form);
    }

    @RequestMapping({ "/externalForm.view" })
    public ModelAndView externalForm(String data, HttpSession session, HttpServletRequest request, String domain) {
        String contextPath = request.getContextPath();

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + contextPath + "/";

        ExternalFormPointModel form = new ExternalFormPointModel("");
        this.dxRoutingDataSource.setDomainKey(domain);
        String codeStr = this.dataService.getCodeStr(data);
        if (null == codeStr) {
            form.setErrorMsg("Link has failed");
            return buildModelAndView(form);
        }
        String url = basePath + "externalForm/getExternalFormData.view" + "?data=" + codeStr;
        try {
            url = basePath + "externalForm/getExternalFormData.view" + "?data=" + URLEncoder.encode(codeStr, "utf-8");
        } catch (Exception e) {
        }

        form.setUrl(url);
        return buildModelAndView(form);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping({ "/getExternalFormData.view" })
    public ModelAndView getExternalFormData(String data, HttpSession session, HttpServletResponse response) {
        response.setHeader("X-Frame-Options", "SAMEORIGIN");
        ExternalFormModel form = new ExternalFormModel("");
        Map<String, Object> map = Common.paseEventParam(data);
        String database = map.get("database").toString();
        session.setAttribute("tableId", map.get("tableId").toString());
        session.setAttribute("conditionMap", (Map) map.get("dataId"));

        this.dxRoutingDataSource.setActiveDomainKey(database);

        form.setTableId(map.get("tableId").toString());
        form.setParam((Map) map.get("dataId"));
        return buildModelAndView(form);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @ResponseBody
    @RequestMapping({ "/selectExternalFormData.do" })
    public ActionResult selectExternalFormData(HttpSession session) {
        ExternalFormModel form = new ExternalFormModel("");
        form.setTableId(session.getAttribute("tableId").toString());
        form.setParam((Map) session.getAttribute("conditionMap"));
        return new ActionResult(true, form);
    }
}
