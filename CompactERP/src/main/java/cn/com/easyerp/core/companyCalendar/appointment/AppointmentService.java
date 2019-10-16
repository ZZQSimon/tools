package cn.com.easyerp.core.companyCalendar.appointment;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.AppointmentDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.framework.common.ActionResult;

@Service
public class AppointmentService {
    private static final String APPOINTMENT_TABLE_NAME = "c_appointment";
    @Autowired
    private AppointmentDao appointmentDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SystemDao systemDao;

    public Map<String, Object> getAppointmentData(AppointmentDescribe appointment) {
        TableDescribe tableDesc = this.cacheService.getTableDesc(appointment.getTable());
        if (null == tableDesc || null == tableDesc.getIdColumns() || 0 == tableDesc.getIdColumns().length)
            return null;
        Map<String, Object> idColumn = new HashMap<String, Object>();
        idColumn.put(tableDesc.getIdColumns()[0], appointment.getData_id());
        return this.appointmentDao.selectAppointmentData(tableDesc.getId(), idColumn);
    }

    public ActionResult edit(AppointmentDescribe appointment, AuthDetails user) {
        Map<String, Object> data = buildParam(appointment, user, false);
        int updateAppointment = this.appointmentDao.updateAppointment(data, appointment);
        if (updateAppointment != 1)
            throw new ApplicationException("Update failed");
        return new ActionResult(true, "success");
    }

    public void appointmentAuth(String owner) {
        String currentUserId = AuthService.getCurrentUserId();
        if (!currentUserId.equals(owner)) {
            throw new ApplicationException("not your appointment");
        }
    }

    public String getOwner(AppointmentDescribe appointment) {
        return (appointment.getOwner() == null || "".equals(appointment.getOwner())) ? appointment.getCre_user()
                : appointment.getOwner();
    }

    public ActionResult create(AppointmentDescribe appointment, AuthDetails user) {
        Map<String, Object> data = buildParam(appointment, user, true);
        this.appointmentDao.createAppointment(data, APPOINTMENT_TABLE_NAME);
        return new ActionResult(true, "success");
    }

    private Map<String, Object> buildParam(AppointmentDescribe appointment, AuthDetails user, boolean isCreate) {
        Map<String, Object> data = new HashMap<String, Object>();
        Date date = new Date();
        if (isCreate) {
            String appointmentId = this.systemDao.getId(APPOINTMENT_TABLE_NAME, "");
            data.put("id", appointmentId);
            data.put("cre_user", user.getId());
            data.put("cre_date", date);
        }
        data.put("user_id", appointment.getUser_id());
        data.put("table", appointment.getTable());
        data.put("data_id", appointment.getData_id());
        data.put("memo", appointment.getMemo());
        data.put("begin_time", appointment.getBegin_time());
        data.put("end_time", appointment.getEnd_time());
        data.put("upd_user", user.getId());
        data.put("upd_date", date);
        data.put("owner", appointment.getOwner());
        data.put("status", appointment.getStatus());
        data.put("color", (appointment.getColor() == null) ? "blue" : appointment.getColor());
        return data;
    }
}
