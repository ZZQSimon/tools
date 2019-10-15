package cn.com.easyerp.calendar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.enums.ActionType;

@Controller
@RequestMapping({ "/calendar" })
public class CalendarController extends FormViewControllerBase {
    @Autowired
    private DataService dataService;
    @Autowired
    private CalendarDao calendarDao;

    @SuppressWarnings("rawtypes")
    @RequestMapping({ "/edit.view" })
    public ModelAndView SaveTreeData(@RequestBody CalendarModel valueData) {
        Map<String, Object> record = this.calendarDao.selectCalendar(valueData.getCalendar_id());
        Map<String, Object> ruleData = this.calendarDao.selectRule(record.get("calendar_rule_id").toString());
        List<Map<String, Object>> setData = this.calendarDao.selectSet(record.get("id").toString());

        CalendarDetailFormModel form = new CalendarDetailFormModel(ActionType.view, valueData.getParent(),
                "sys_calendar_rule", ruleData, setData, valueData.getCalendar_id());
        return buildModelAndView(form);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/saveSet.do" })
    public ActionResult SelectTreeData(@RequestBody Map<String, Object> param) {
        String calendarId = param.get("calendar_id").toString();
        int count = this.calendarDao.selectSetcount(calendarId, param.get("date").toString());
        List<ColumnValue> key = new ArrayList<ColumnValue>();
        key.add(new ColumnValue("calendar_id", calendarId));
        key.add(new ColumnValue("date", param.get("date").toString()));
        if (count > 0) {
            this.dataService.deleteRecord("p_calendar_set", key);
        } else {
            this.dataService.insertRecord("p_calendar_set", param);
        }

        Map<String, Object> record = this.calendarDao.selectCalendar(calendarId);
        List<Map<String, Object>> setData = this.calendarDao.selectSet(record.get("id").toString());
        return new ActionResult(true, setData);
    }
}
