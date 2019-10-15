package cn.com.easyerp.core.companyCalendar.appointment;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.dao.AppointmentDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.enums.ActionType;

@Controller
@RequestMapping({ "/appointment" })
public class AppointmentController extends FormViewControllerBase {
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private AppointmentDao appointmentDao;

    @RequestMapping({ "/dayAppointment.view" })
    public ModelAndView dayAppointment(@RequestBody AppointmentRequestModel request) {
        AppointmentModel form;
        if (request.isReadonly()) {
            form = new AppointmentModel(ActionType.view, request.getParent());
        } else {
            form = new AppointmentModel(ActionType.edit, request.getParent());
        }
        form.setAppointment(this.appointmentDao.selectAppointmentById(request.getAppointment().getId()));
        form.getAppointment().setUser(this.systemDao.getUserById(form.getAppointment().getUser_id(),
                this.dxRoutingDataSource.getDomainKey()));
        form.setAppointmentData(this.appointmentService.getAppointmentData(request.getAppointment()));
        return buildModelAndView(form);
    }

    @Autowired
    private SystemDao systemDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;

    @RequestMapping({ "/dayAppointmentCreate.view" })
    public ModelAndView dayAppointmentCreate(@RequestBody AppointmentRequestModel request, AuthDetails user) {
        AppointmentModel form = new AppointmentModel(ActionType.create, request.getParent());
        AppointmentDescribe appointment = request.getAppointment();
        appointment.setUser_id(user.getId());
        appointment.setUser(user);
        form.setAppointmentData(this.appointmentService.getAppointmentData(request.getAppointment()));
        form.setAppointment(appointment);

        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/appointmentEdit.do" })
    public ActionResult appointmentEdit(@RequestBody AppointmentRequestModel request, AuthDetails user) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            this.appointmentService.appointmentAuth(this.appointmentService.getOwner(request.getAppointment()));
        } catch (Exception e) {
            resultMap.put("status", Boolean.valueOf(false));
            resultMap.put("msg", "not your appointment");
            return new ActionResult(true, resultMap);
        }
        this.appointmentService.edit(request.getAppointment(), user);
        resultMap.put("status", Boolean.valueOf(true));
        resultMap.put("msg", "success");
        return new ActionResult(true, resultMap);
    }

    @ResponseBody
    @RequestMapping({ "/appointmentCreate.do" })
    public ActionResult appointmentCreate(@RequestBody AppointmentRequestModel request, AuthDetails user) {
        return this.appointmentService.create(request.getAppointment(), user);
    }

    @RequestMapping({ "/mobile/dayAppointment.view" })
    public ModelAndView mobiledayAppointment(@RequestBody AppointmentMobileRequestModel request) {
        AppointmentMobileModel form = null;
        if (request.isReadonly()) {
            form = new AppointmentMobileModel(ActionType.view, request.getParent());
        } else {
            form = new AppointmentMobileModel(ActionType.edit, request.getParent());
        }

        return buildModelAndView(form);
    }
}
