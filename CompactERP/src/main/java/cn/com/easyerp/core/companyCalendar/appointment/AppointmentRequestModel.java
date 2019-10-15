package cn.com.easyerp.core.companyCalendar.appointment;

import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class AppointmentRequestModel extends FormRequestModelBase<AppointmentModel> {
    private AppointmentDescribe appointment;
    private Map<String, Object> param;
    private boolean readonly = true;

    public AppointmentDescribe getAppointment() {
        return this.appointment;
    }

    public void setAppointment(AppointmentDescribe appointment) {
        this.appointment = appointment;
    }

    public boolean isReadonly() {
        return this.readonly;
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }
}
