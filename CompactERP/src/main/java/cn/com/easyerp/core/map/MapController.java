package cn.com.easyerp.core.map;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.dao.AttendanceDao;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/map" })
public class MapController extends FormViewControllerBase {
    @Autowired
    private AttendanceDao attendanceDao;
    // @Autowired
    // private TableDeployDao tableDeployDao;
    private static SimpleDateFormat sdfAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @SuppressWarnings({ "rawtypes", "unused" })
    private String getBasic(List<Map<String, String>> maps, String str) {
        for (int i = 0; i < maps.size(); i++) {
            if (str.equals(((Map) maps.get(i)).get("id"))) {
                return (String) ((Map) maps.get(i)).get("lan");
            }
        }
        return "";
    }

    @SuppressWarnings("unused")
    private String getType(String type) {
        String t = "";
        switch (type) {
        case "c_":
            t = "deploy_sys_table";
            break;
        case "m_":
            t = "deploy_base_table";
            break;
        case "t_":
            t = "deploy_professional_table";
            break;
        case "v_":
            t = "deploy_view_table";
            break;
        case "a_":
            t = "deploy_api_table";
            break;
        }

        return t;
    }

    @RequestMapping({ "/map.view" })
    public ModelAndView map(@RequestBody MapRequestModel request) {
        MapModel form = new MapModel(request.getParent());
        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/setAttendanceArea.do" })
    public ActionResult setAttendanceArea(AuthDetails auth, @RequestBody MapRequestModel request) {
        String result = "success";
        String attendanceId = "";
        try {
            String user_id = auth.getId();
            List<String> areas = this.attendanceDao.selectAttendanceAreaByArea(request.getAttendanceArea());

            if (areas != null && areas.size() > 0) {
                result = "考情地点重复";
            } else {

                String date = sdfAll.format(new Date());
                UUID uuid = UUID.randomUUID();
                attendanceId = uuid.toString();
                this.attendanceDao.saveAttendanceArea(attendanceId, request.getAttendanceName(),
                        request.getAttendanceArea(), request.getAttendanceLon(), request.getAttendanceLat(), user_id,
                        date);
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = "保存数据出错";
        }
        if (!"success".equals(result)) {
            return new ActionResult(false, result);
        }
        return new ActionResult(true, attendanceId);
    }

    @ResponseBody
    @RequestMapping({ "/updateAttendanceTime.do" })
    public ActionResult updateAttendanceTime(@RequestBody MapRequestModel request) {
        String result = "success";
        try {
            this.attendanceDao.updateAttendanceTime(request.getBeginTime(), request.getEndTime(),
                    request.getAttendanceId());
        } catch (Exception e) {

            e.printStackTrace();
            result = "保存数据出错";
        }
        if (!"success".equals(result)) {
            return new ActionResult(false, result);
        }
        return new ActionResult(true, result);
    }

    @ResponseBody
    @RequestMapping({ "/selectAttendanceArea.do" })
    public ActionResult selectAttendanceArea() {
        String result = "success";
        List<AttendanceArea> list = null;
        try {
            list = this.attendanceDao.selectAttendanceArea();
        } catch (Exception e) {

            e.printStackTrace();
            result = "获取数据出错";
        }
        if (!"success".equals(result)) {
            return new ActionResult(false, result);
        }
        return new ActionResult(true, list);
    }

    @ResponseBody
    @RequestMapping({ "/removeAttendanceArea.do" })
    public ActionResult removeAttendanceArea(@RequestBody MapRequestModel request) {
        String result = "success";
        try {
            this.attendanceDao.removeAttendanceArea(request.getAreas());
        } catch (Exception e) {

            e.printStackTrace();
            result = "处理数据出错";
        }

        if (!"success".equals(result)) {
            return new ActionResult(false, result);
        }
        return new ActionResult(true, result);
    }
}
