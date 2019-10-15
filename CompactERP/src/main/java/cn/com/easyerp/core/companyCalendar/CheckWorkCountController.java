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

import com.alibaba.fastjson.JSONArray;

import cn.com.easyerp.core.dao.CheckWorkDao;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/checkWorkCount" })
public class CheckWorkCountController extends FormViewControllerBase {
    @Autowired
    private CheckWorkDao checkWorkDao;
    @Autowired
    private CheckWorkService checkWorkService;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @RequestMapping({ "/checkWorkCount.view" })
    public ModelAndView map(@RequestBody CheckWorkCountRequestModel request) {
        CheckWorkCountModel form = new CheckWorkCountModel(request.getParent());
        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/selectAttendanceCount.do" })
    public ActionResult selectAttendanceCount(@RequestBody CheckWorkCountRequestModel request) {
        String result = "success";
        CheckWorkResult cResult = null;

        String sEcho = null;

        Integer iDisplayStart = null;
        Integer iDisplayLength = null;

        try {
            Date firstDay = sdf.parse(request.getYear() + "-" + request.getMonth() + "-" + "01");
            Date lastDay = getLastDayOfMonth(Integer.valueOf(request.getYear()).intValue(),
                    Integer.valueOf(request.getMonth()).intValue());
            int pre = getWeekOfDate(firstDay) - 1;

            String beginDate = sdf.format(firstDay);
            if (pre > 0) {
                beginDate = getIntervalDate(firstDay, -pre);
            }
            int last = 7 - getWeekOfDate(lastDay);
            String endDate = sdf.format(lastDay);
            if (last > 0) {
                endDate = getIntervalDate(lastDay, last);
            }

            cResult = new CheckWorkResult();
            if (request.getAodata() == null || "".equals(request.getAodata())) {
                List<Integer> plan = this.checkWorkService.selectWorkPlanByDate(beginDate, endDate);

                int end = Integer.valueOf(sdf.format(lastDay).split("-")[2]).intValue();
                for (int j = 0; j < pre; j++) {
                    plan.remove(0);
                }
                for (int j = 0; j < last; j++) {
                    plan.remove(end);
                }

                cResult.setPlan(plan);
            } else {
                String aoData = request.getAodata();

                JSONArray ja = JSONArray.parseArray(aoData);

                for (int i = 0; i < ja.size(); i++) {
                    if (ja.getJSONObject(i).getString("name").equals("sEcho")) {
                        sEcho = ja.getJSONObject(i).getString("value");
                    } else if (ja.getJSONObject(i).getString("name").equals("iDisplayStart")) {
                        iDisplayStart = Integer.valueOf(ja.getJSONObject(i).getString("value"));
                    } else if (ja.getJSONObject(i).getString("name").equals("iDisplayLength")) {
                        iDisplayLength = Integer.valueOf(ja.getJSONObject(i).getString("value"));
                    }
                }
                List<String> users = this.checkWorkDao.selectAttendanceCountUser(
                        request.getYear() + "-" + request.getMonth(), request.getName(), iDisplayStart, iDisplayLength);
                List<Integer> plan = this.checkWorkService.selectWorkPlanByDate(beginDate, endDate);

                List<CheckWorkJson> list = this.checkWorkService.selectAttendances(users, beginDate, endDate);
                int end = Integer.valueOf(sdf.format(lastDay).split("-")[2]).intValue();

                for (int i = 0; i < list.size(); i++) {
                    List<AttendanceJson> attendances = ((CheckWorkJson) list.get(i)).getList();
                    for (int j = 0; j < pre; j++) {
                        attendances.remove(0);
                        if (i == 0) {
                            plan.remove(0);
                        }
                    }

                    for (int j = 0; j < last; j++) {
                        attendances.remove(end);
                        if (i == 0) {
                            plan.remove(end);
                        }
                    }
                }

                cResult.setList(list);
                cResult.setPlan(plan);
                cResult.setsEcho(sEcho);

                int total = this.checkWorkDao
                        .selectAttendanceCountUserCount(request.getYear() + "-" + request.getMonth(), request.getName())
                        .size();
                cResult.setiTotalRecords(Integer.valueOf(total));
                cResult.setiTotalDisplayRecords(Integer.valueOf(total));
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = "处理数据出错";
        }

        if (!"success".equals(result)) {
            return new ActionResult(false, result);
        }
        return new ActionResult(true, cResult);
    }

    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(7) - 1;
        if (w == 0)
            w = 7;
        return w;
    }

    private static Date getLastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, year);
        calendar.set(2, month - 1);

        calendar.set(5, calendar.getActualMaximum(5));
        return calendar.getTime();
    }

    public static String getIntervalDate(Date fromDate, int interval) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fromDate);
        calendar.add(5, interval);
        return sdf.format(Long.valueOf(calendar.getTimeInMillis()));
    }
}
