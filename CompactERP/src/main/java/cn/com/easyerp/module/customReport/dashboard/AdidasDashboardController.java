package cn.com.easyerp.module.customReport.dashboard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.module.customReport.CustomReportRequestModel;
import cn.com.easyerp.module.dao.AdidasDashboardDao;

@Controller
@RequestMapping({ "/adidasDashboard" })
public class AdidasDashboardController extends FormViewControllerBase {
    @Autowired
    private AdidasDashboardDao adidasDashboardDao;
    @Autowired
    private AdidasDashboardService adidasDashboardService;
    public static final String FILTER_BUTTON_LABEL = "search";

    @RequestMapping({ "/adidasDashboard.view" })
    public ModelAndView dashboard(@RequestBody CustomReportRequestModel request) {
        FilterModel filter = new FilterModel(request.getFilterTable(), "search", null, null, null);

        AdidasDashboardModel form = new AdidasDashboardModel(request.getParent(), request.getFilterTable(), filter);
        form.setType(request.getType());
        return buildModelAndView(form);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @ResponseBody
    @RequestMapping({ "/search.do" })
    public ActionResult search(@RequestBody CustomReportRequestModel request) {
        if ("1".equals(request.getType())) {
            FilterRequestModel filter = request.getFilter();
            return this.adidasDashboardService.diversitySelect(filter);
        }
        FilterRequestModel model = request.getFilter();
        Map<String, FilterDescribe> map = model.getFilters();
        if (null == map || map.size() == 0) {
            return new ActionResult(false, "no selected");
        }
        List<String> region = null;
        List<String> store_type = null;
        List<String> employee_type = null;
        List<String> grade = null;
        String store = null;
        Date begin_date = null;
        Date end_date = null;
        String begin_dates = "";
        String end_dates = "";
        if (null != map.get("region")) {
            region = getFilterData((FilterDescribe) map.get("region"));
        }
        if (null != map.get("store_type")) {
            store_type = getFilterData((FilterDescribe) map.get("store_type"));
        }
        if (null != map.get("employee_type")) {
            employee_type = getFilterData((FilterDescribe) map.get("employee_type"));
        }
        if (null != map.get("grade")) {
            grade = getFilterData((FilterDescribe) map.get("grade"));
        }
        if (null != map.get("store")) {
            FilterDescribe describe = (FilterDescribe) map.get("store");
            List<Object> dList = (List) describe.getValue();
            for (Object filterDescribe : dList) {
                store = filterDescribe.toString();
            }
        }
        if (null != map.get("period")) {
            FilterDescribe describe = (FilterDescribe) map.get("period");
            Long begin = (Long) describe.getFrom();
            Long end = (Long) describe.getTo();
            begin_date = new Date(begin.longValue());
            end_date = new Date(end.longValue());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            begin_dates = sdf.format(begin_date);
            end_dates = sdf.format(end_date);
        }
        List<EmployeeType> maps = this.adidasDashboardDao.countBycontion(region, store_type, employee_type, grade,
                store, begin_dates, end_dates);
        return new ActionResult(true, maps);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<String> getFilterData(FilterDescribe fd) {
        List<String> datas = new ArrayList<>();
        List<Object> dList = (List) fd.getValue();
        if (dList.size() == 0 || null == dList) {
            return null;
        }

        for (Object filterDescribe : dList) {
            datas.add(filterDescribe.toString());
        }

        return datas;
    }
}
