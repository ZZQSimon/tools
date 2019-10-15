package cn.com.easyerp.core.companyCalendar.appointment;

import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class AppointmentMobileRequestModel extends FormRequestModelBase<AppointmentMobileModel> {
    private AppointmentDescribe appointment;
    private String type;
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

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }
}
