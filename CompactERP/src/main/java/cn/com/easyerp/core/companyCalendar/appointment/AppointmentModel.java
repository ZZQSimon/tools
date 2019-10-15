package cn.com.easyerp.core.companyCalendar.appointment;

import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("appointment")
public class AppointmentModel extends FormModelBase {
    private AppointmentDescribe appointment;
    private Map<String, Object> appointmentData;

    public AppointmentDescribe getAppointment() {
        return this.appointment;
    }

    public void setAppointment(AppointmentDescribe appointment) {
        this.appointment = appointment;
    }

    public Map<String, Object> getAppointmentData() {
        return this.appointmentData;
    }

    public void setAppointmentData(Map<String, Object> appointmentData) {
        this.appointmentData = appointmentData;
    }

    protected AppointmentModel(ActionType action, String parent) {
        super(action, parent);
    }

    public String getTitle() {
        return "appointment";
    }
}
