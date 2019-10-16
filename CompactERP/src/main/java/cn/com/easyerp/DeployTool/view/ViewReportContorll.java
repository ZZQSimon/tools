package cn.com.easyerp.DeployTool.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.ViewReportDao;
import cn.com.easyerp.DeployTool.service.ViewReportDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/viewReport" })
public class ViewReportContorll extends FormViewControllerBase {
    @RequestMapping({ "/viewReport.view" })
    public ModelAndView dictionary(@RequestBody ViewReportRequestModel request) {
        ViewReportModel form = new ViewReportModel(request.getParent());
        return buildModelAndView(form);
    }

    @Autowired
    private ViewReportDao viewReportDao;

    @ResponseBody
    @RequestMapping({ "/initViewReport.do" })
    public ActionResult initViewReport() {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        List<ViewReportDetails> viewReportList = this.viewReportDao.selectViewReport();
        dataMap.put("total", Integer.valueOf(viewReportList.size()));
        dataMap.put("rows", viewReportList);
        return new ActionResult(true, dataMap);
    }

    @ResponseBody
    @RequestMapping({ "/saveViewReport.do" })
    public ActionResult saveViewReport(@RequestBody ViewReportRequestModel request) {
        List<ViewReportDetails> insert = request.getInsert();
        List<ViewReportDetails> deleted = request.getDeleted();
        List<ViewReportDetails> update = request.getUpdate();
        if (insert.size() > 0) {
            for (ViewReportDetails viewReportDetails : insert) {
                this.viewReportDao.addViewReport(viewReportDetails);
                getReportName(viewReportDetails);
                viewReportDetails.setInternational_id(viewReportDetails.getId());
                this.viewReportDao.addInternational(viewReportDetails);
            }
        }
        if (deleted.size() > 0) {
            for (ViewReportDetails viewReportDetails : deleted) {
                this.viewReportDao.deleteViewReport(viewReportDetails.getId());
            }
        }
        if (update.size() > 0) {
            for (ViewReportDetails viewReportDetails : update) {
                this.viewReportDao.deleteViewReport(viewReportDetails.getId());
                this.viewReportDao.addViewReport(viewReportDetails);
                getReportName(viewReportDetails);
                this.viewReportDao.addInternational(viewReportDetails);
            }
        }
        return new ActionResult(true, "保存成功！");
    }

    public void getReportName(ViewReportDetails viewReportDetails) {
        String language = AuthService.getCurrentUser().getLanguage_id();
        if (language.equals("cn")) {
            viewReportDetails.setCn(viewReportDetails.getReport_name());
        }
        if (language.equals("en")) {
            viewReportDetails.setEn(viewReportDetails.getReport_name());
        }
        if (language.equals("jp")) {
            viewReportDetails.setJp(viewReportDetails.getReport_name());
        }
        if (language.equals("other1")) {
            viewReportDetails.setOther1(viewReportDetails.getReport_name());
        }
        if (language.equals("other2"))
            viewReportDetails.setOther2(viewReportDetails.getReport_name());
    }
}
