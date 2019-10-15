package cn.com.easyerp.core.companyCalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.dao.CheckWorkDao;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/checkWork" })
public class CheckWorkController extends FormViewControllerBase {
    @RequestMapping({ "/checkWork.view" })
    public ModelAndView map(@RequestBody CheckWorkRequestModel request) {
        CheckWorkModel form = new CheckWorkModel(request.getParent());
        return buildModelAndView(form);
    }

    @Autowired
    private CheckWorkDao checkWorkDao;
    @Autowired
    private AttendanceService attendanceService;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss");

    @ResponseBody
    @RequestMapping({ "/selectCheckWork.do" })
    public ActionResult selectAttendances(AuthDetails auth, @RequestBody CheckWorkRequestModel request)
            throws Exception {
        String result = "success";
        List<AttendanceJson> list = null;
        try {
            String beginDate = sdf.format(sdf.parse(request.getBeginDate()));
            String endDate = getIntervalDate(sdf.parse(request.getEndDate()), -1);
            System.err.println("-------------" + endDate);
            String user = auth.getId();
            list = this.attendanceService.selectAttendances(user, beginDate, endDate);
        } catch (Exception e) {
            e.printStackTrace();
            result = "处理数据出错";
        }

        if (!"success".equals(result)) {
            return new ActionResult(false, result);
        }
        return new ActionResult(true, list);
    }

    private static String getIntervalDate(Date fromDate, int interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(5, interval);
        return sdf.format(Long.valueOf(calendar.getTimeInMillis()));
    }
}
