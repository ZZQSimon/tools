package cn.com.easyerp.core.companyCalendar;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.data.CalendarJson;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.storage.StorageService;

@Controller
@RequestMapping({ "/calendar" })
public class PCCalendarController extends FormViewControllerBase {
    public static final String CALENDAR_ID = "0000000001";
    public static final String EVENT_TABLE = "p_calendar_event";
    public static final String EVENT_TABLE_COLUMN = "file";
    @Autowired
    private CalendarDao calendarDao;
    @Autowired
    private DxRoutingDataSource dataSource;
    @Autowired
    private DataService dataService;
    @Autowired
    private PCCalendarService pcCalendarService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private CacheService cacheService;

    @RequestMapping({ "/calendar.view" })
    public ModelAndView map(@RequestBody final CalendarRequestModel request) {
        final CalendarModel form = new CalendarModel(request.getParent());
        form.setType(request.getType());
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sourceTable", request.getSourceTable());
        paramMap.put("begin_time", request.getBegin_time());
        paramMap.put("end_time", request.getEnd_time());
        paramMap.put("group", request.getGroup());
        paramMap.put("color", request.getColor());
        paramMap.put("filter", request.getFilter());
        form.setParam(paramMap);
        if ("3".equals(request.getType())) {
            form.setTable(request.getSourceTable());
        }
        return this.buildModelAndView((FormModelBase) form);
    }

    @RequestMapping({ "/mobile_calendar.view" })
    public ModelAndView mobile_calendar(@RequestBody final CalendarRequestModel request) {
        final MobileCalendarModel form = new MobileCalendarModel(request.getParent());
        form.setType(request.getType());
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sourceTable", request.getSourceTable());
        paramMap.put("begin_time", request.getBegin_time());
        paramMap.put("end_time", request.getEnd_time());
        paramMap.put("group", request.getGroup());
        paramMap.put("color", request.getColor());
        paramMap.put("filter", request.getFilter());
        form.setParam(paramMap);
        return this.buildModelAndView((FormModelBase) form);
    }

    @RequestMapping({ "/eventDetail.view" })
    public ModelAndView eventDetail(@RequestBody final CalendarRequestModel request) {
        final EventDetailModel form = new EventDetailModel(request.getParent());
        final CalendarEvent calendarEvent = this.calendarDao.selectCalendarEventById(request.getCalendar_id(),
                request.getP_calendar_event_id());
        final List<CalendarEventShare> calendarEventShares = (List<CalendarEventShare>) this.calendarDao
                .selectCalendarEventShareById(request.getP_calendar_event_id());
        if (calendarEventShares != null && calendarEventShares.size() != 0) {
            for (final CalendarEventShare calendarEventShare : calendarEventShares) {
                calendarEvent.addCalendarEventShare(calendarEventShare);
            }
        }
        form.setType("view");
        form.setCalendarEvent(calendarEvent);
        return this.buildModelAndView((FormModelBase) form);
    }

    @RequestMapping({ "/eventCreate.view" })
    public ModelAndView eventCreate(@RequestBody final CalendarRequestModel request) {
        final EventDetailModel form = new EventDetailModel(request.getParent());
        form.setType("create");
        return this.buildModelAndView((FormModelBase) form);
    }

    @RequestMapping({ "/eventUser.view" })
    public ModelAndView eventUser(@RequestBody final CalendarRequestModel request) {
        final EventUserModel form = new EventUserModel(request.getParent());
        return this.buildModelAndView((FormModelBase) form);
    }

    @ResponseBody
    @RequestMapping({ "/selectEventUser.do" })
    public ActionResult selectEventUser(@RequestBody final CalendarRequestModel request, final AuthDetails user) {
        return new ActionResult(true, (Object) this.dataService.buildUserTree());
    }

    @ResponseBody
    @RequestMapping({ "/selectAppointmentSource.do" })
    public ActionResult selectAppointmentSource(@RequestBody final CalendarRequestModel request,
            final AuthDetails user) {
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sourceTable", request.getSourceTable());
        paramMap.put("begin_time", request.getBegin_time());
        paramMap.put("end_time", request.getEnd_time());
        paramMap.put("group", request.getGroup());
        paramMap.put("color", request.getColor());
        paramMap.put("filter", request.getFilter());
        final List<TreeNode> treeNodes = this.pcCalendarService.buildSourceGroupTree(paramMap);
        return new ActionResult(true, (Object) treeNodes);
    }

    @ResponseBody
    @RequestMapping({ "/selectAppointmentEvent.do" })
    public ActionResult selectAppointmentEvent(@RequestBody final CalendarRequestModel request,
            final AuthDetails user) {
        final List<Map<String, Object>> appointments = (List<Map<String, Object>>) this.calendarDao
                .selectAppointmentEvent(request.getAppointmentParam());
        if (null == appointments) {
            return new ActionResult(true, (Object) null);
        }
        final List<Map<String, Object>> appointmentsResult = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < appointments.size(); ++i) {
            final Map<String, Object> appointment = new HashMap<String, Object>();
            final Map<String, Object> appointmentUser = new HashMap<String, Object>();
            for (final Map.Entry<String, Object> entry : appointments.get(i).entrySet()) {
                final String[] userColumns = entry.getKey().split("\\$");
                if (userColumns.length == 2) {
                    appointmentUser.put(userColumns[1], entry.getValue());
                } else {
                    appointment.put(entry.getKey(), entry.getValue());
                }
            }
            appointment.put("user", appointmentUser);
            appointmentsResult.add(appointment);
        }
        return new ActionResult(true, (Object) appointmentsResult);
    }

    @ResponseBody
    @RequestMapping({ "/selectCalendar.do" })
    public ActionResult selectCalendar(@RequestBody final CalendarRequestModel request, final AuthDetails user) {
        final CalendarJson calendar = new CalendarJson();
        final Map<String, Object> rule = (Map<String, Object>) this.calendarDao.selectRule("0000000001");
        final List<Object> weekdays = new ArrayList<Object>();
        weekdays.add(rule.get("monday"));
        weekdays.add(rule.get("tuesday"));
        weekdays.add(rule.get("wednesday"));
        weekdays.add(rule.get("thursday"));
        weekdays.add(rule.get("friday"));
        weekdays.add(rule.get("saturday"));
        weekdays.add(rule.get("sunday"));
        final List<String> workdays = (List<String>) this.calendarDao.selectDates("0000000001", 1, (String) null);
        final List<String> holidays = (List<String>) this.calendarDao.selectDates("0000000001", 0, (String) null);
        final Map<String, Object> map = (Map<String, Object>) this.calendarDao.selectSync("0000000001");
        int sync = 0;
        if (map != null && map.size() > 0 && map.get("is_sync") != null) {
            sync = (int) map.get("is_sync");
        }
        calendar.setWeekdays((List) weekdays);
        calendar.setHolidays((List) holidays);
        calendar.setWorkdays((List) workdays);
        calendar.setIsSync(sync);
        if ("2".equals(request.getType())) {
            final List<CalendarEvent> calendarEvents = (List<CalendarEvent>) this.calendarDao
                    .selectCalendarEvent("0000000001", user.getId(), request.getEventDate());
            final List<PCCalendarModel> pcCalendarModels = this.pcCalendarService.buildCalendarEvents(calendarEvents);
            calendar.setPcCalendarModels((List) pcCalendarModels);
        }
        if ("3".equals(request.getType())) {
        }
        return new ActionResult(true, (Object) calendar);
    }

    @ResponseBody
    @RequestMapping({ "/selectTimeCalendar.do" })
    public ActionResult selectTimeCalendar(@RequestBody final CalendarRequestModel request, final AuthDetails user) {
        final CalendarJson calendar = new CalendarJson();
        final List<CalendarEvent> calendarEvents = (List<CalendarEvent>) this.calendarDao
                .selectCalendarEvent("0000000001", user.getId(), request.getEventDate());
        final List<PCCalendarModel> pcCalendarModels = this.pcCalendarService.buildCalendarEvents(calendarEvents);
        calendar.setPcCalendarModels((List) pcCalendarModels);
        return new ActionResult(true, (Object) calendar);
    }

    @ResponseBody
    @RequestMapping({ "/syncInternationalHoliday" })
    public ActionResult syncInternationalHoliday() {
        final String result = "success";
        CalendarJson calendar = null;
        final String old = this.dataSource.getDomainKey();
        this.dataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);
        final Date date = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        final String dateNowStr = sdf.format(date);
        final Map<String, Object> weeks = (Map<String, Object>) this.calendarDao.selectRule("0000000001");
        final List<String> workdays = (List<String>) this.calendarDao.selectDates("0000000001", 1, dateNowStr);
        final List<String> holidays = (List<String>) this.calendarDao.selectDates("0000000001", 0, dateNowStr);
        calendar = new CalendarJson();
        final List<Object> weekdays = new ArrayList<Object>();
        weekdays.add(weeks.get("monday"));
        weekdays.add(weeks.get("tuesday"));
        weekdays.add(weeks.get("wednesday"));
        weekdays.add(weeks.get("thursday"));
        weekdays.add(weeks.get("friday"));
        weekdays.add(weeks.get("saturday"));
        weekdays.add(weeks.get("sunday"));
        calendar.setWeekdays((List) weekdays);
        calendar.setHolidays((List) holidays);
        calendar.setWorkdays((List) workdays);
        this.dataSource.setDomainKey(old);
        this.calendarDao.deleteSet(dateNowStr);
        if (weeks != null && weeks.size() > 0) {
            this.calendarDao.updateRule("0000000001", (int) weeks.get("monday"), (int) weeks.get("tuesday"),
                    (int) weeks.get("wednesday"), (int) weeks.get("thursday"), (int) weeks.get("friday"),
                    (int) weeks.get("saturday"), (int) weeks.get("sunday"));
        }
        if (holidays != null && holidays.size() > 0) {
            this.calendarDao.saveHoliday("0000000001", (List) holidays);
        }
        if (workdays != null && workdays.size() > 0) {
            this.calendarDao.saveWorkday("0000000001", (List) workdays);
        }
        return new ActionResult(true, (Object) calendar);
    }

    @ResponseBody
    @RequestMapping({ "/saveCalendar.do" })
    public ActionResult saveCalendar(@RequestBody final cn.com.easyerp.calendar.CalendarModel request) {
        String result = "success";
        try {
            final List<Integer> weeks = (List<Integer>) request.getWeekdays();
            if (request.getWeekdays() != null && request.getWeekdays().size() > 0) {
                this.calendarDao.updateRule("0000000001", (int) weeks.get(0), (int) weeks.get(1), (int) weeks.get(2),
                        (int) weeks.get(3), (int) weeks.get(4), (int) weeks.get(5), (int) weeks.get(6));
            }
            this.calendarDao.deleteSet((String) null);
            if (request.getHolidays() != null && request.getHolidays().size() > 0) {
                this.calendarDao.saveHoliday("0000000001", request.getHolidays());
            }
            if (request.getWorkdays() != null && request.getWorkdays().size() > 0) {
                this.calendarDao.saveWorkday("0000000001", request.getWorkdays());
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = "\u5904\u7406\u6570\u636e\u51fa\u9519";
        }
        if (!"success".equals(result)) {
            return new ActionResult(false, (Object) result);
        }
        return new ActionResult(true, (Object) result);
    }

    @ResponseBody
    @Transactional
    @RequestMapping({ "/saveCalendarEvent.do" })
    public ActionResult saveCalendarEvent(@RequestBody final CalendarRequestModel request) {
        if (request.getCalendarEvent() == null) {
            return new ActionResult(false, (Object) "no save data");
        }
        final CalendarEvent calendarEvent = request.getCalendarEvent();
        final TableDescribe eventTable = this.cacheService.getTableDesc("p_calendar_event");
        final ColumnDescribe eventColumn = eventTable.getColumn("file");
        String fileName = "";
        if (calendarEvent.getUploadFile() != null) {
            try {
                fileName = this.storageService.saveUploadField(eventColumn, calendarEvent.getUploadFile());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            calendarEvent.setFile(fileName);
        }
        this.calendarDao.updateCalendarEvents(calendarEvent);
        if (calendarEvent.getUsers() == null) {
            return new ActionResult(true, (Object) "");
        }
        if (calendarEvent.getUsers().get("deleteUsers") != null) {
            this.calendarDao.deleteCalendarEventShare(calendarEvent, (Map) calendarEvent.getUsers().get("deleteUsers"));
        }
        if (calendarEvent.getUsers().get("addUsers") != null) {
            final Map<String, Object> addUsers = calendarEvent.getUsers().get("addUsers");
            String maxId = this.calendarDao.getMaxId(calendarEvent.getP_calendar_event_id());
            for (final Map.Entry<String, Object> user : addUsers.entrySet()) {
                String nextId = "00000";
                if (maxId == null) {
                    nextId = this.pcCalendarService.getNextId("00000");
                } else {
                    nextId = (maxId = this.pcCalendarService.getNextId(maxId));
                }
                final CalendarEventShare calendarEventShare = new CalendarEventShare();
                calendarEventShare.setP_calendar_event_id(calendarEvent.getP_calendar_event_id());
                calendarEventShare.setP_calendar_event_share_id(nextId);
                calendarEventShare.setUser_id(user.getKey());
                this.calendarDao.addCalendarEventShare(calendarEventShare);
            }
        }
        return new ActionResult(true, (Object) "");
    }

    @ResponseBody
    @Transactional
    @RequestMapping({ "/createCalendarEvent.do" })
    public ActionResult createCalendarEvent(@RequestBody final CalendarRequestModel request) {
        if (request.getCalendarEvent() == null) {
            return new ActionResult(false, (Object) "no save data");
        }
        final CalendarEvent calendarEvent = request.getCalendarEvent();
        final TableDescribe eventTable = this.cacheService.getTableDesc("p_calendar_event");
        final ColumnDescribe eventColumn = eventTable.getColumn("file");
        String fileName = "";
        if (calendarEvent.getUploadFile() != null) {
            try {
                fileName = this.storageService.saveUploadField(eventColumn, calendarEvent.getUploadFile());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            calendarEvent.setFile(fileName);
        }
        calendarEvent.setCalendar_id("0000000001");
        final String maxId = this.calendarDao.getCalendarEventMaxId();
        String nextId = "0000000000";
        if (maxId == null) {
            nextId = this.pcCalendarService.getNextId(nextId);
        } else {
            nextId = this.pcCalendarService.getNextId(maxId);
        }
        calendarEvent.setP_calendar_event_id(nextId);
        calendarEvent.setOwner(AuthService.getCurrentUserId());
        this.calendarDao.addCalendarEvents(calendarEvent);
        if (calendarEvent.getUsers() == null) {
            return new ActionResult(true, (Object) "");
        }
        if (calendarEvent.getUsers().get("addUsers") != null) {
            final Map<String, Object> addUsers = calendarEvent.getUsers().get("addUsers");
            String maxShareId = "00000";
            for (final Map.Entry<String, Object> user : addUsers.entrySet()) {
                String nextShareId = "00000";
                nextShareId = (maxShareId = this.pcCalendarService.getNextId(maxShareId));
                final CalendarEventShare calendarEventShare = new CalendarEventShare();
                calendarEventShare.setP_calendar_event_id(calendarEvent.getP_calendar_event_id());
                calendarEventShare.setP_calendar_event_share_id(nextShareId);
                calendarEventShare.setUser_id(user.getKey());
                this.calendarDao.addCalendarEventShare(calendarEventShare);
            }
        }
        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/saveCalendarEvents.do" })
    public ActionResult saveCalendarEvents(@RequestBody final CalendarRequestModel request) {
        if (request.getCalendarEvents() == null || request.getCalendarEvents().size() == 0) {
            return null;
        }
        final Map<String, CalendarEvent> calendarEvents = request.getCalendarEvents();
        for (final Map.Entry<String, CalendarEvent> calendarEvent : calendarEvents.entrySet()) {
            this.calendarDao.updateCalendarEvents((CalendarEvent) calendarEvent.getValue());
        }
        return new ActionResult(true, (Object) "success");
    }

    @ResponseBody
    @RequestMapping({ "/initEventTag.do" })
    public ActionResult initEventTag(@RequestBody final MobileCalendarRequestModel request, final AuthDetails user) {
        final MobileCalendarModel form = new MobileCalendarModel("");
        form.setEventTags(this.pcCalendarService.getEventTag(request, user));
        return new ActionResult(true, (Object) form);
    }

    @ResponseBody
    @RequestMapping({ "/getRestRule.do" })
    public ActionResult getRestRule(@RequestBody final MobileCalendarRequestModel request, final AuthDetails user) {
        final MobileCalendarModel form = new MobileCalendarModel("");
        form.setCommonRule(this.pcCalendarService.getCommonRule(request));
        form.setSpecialRule(this.pcCalendarService.getSpecialRule(request));
        return new ActionResult(true, (Object) form);
    }
}
