package cn.com.easyerp.core.companyCalendar;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("mobile_calendar")
public class MobileCalendarModel extends FormModelBase {
    private String type;
    private Map<String, Object> param;
    private List<Map<String, Object>> eventList;
    private List<Map<String, Object>> eventTags;
    private Map<String, Object> commonRule;
    private List<Map<String, Object>> specialRule;

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

    protected MobileCalendarModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "mobile_calendar";
    }

    public List<Map<String, Object>> getEventList() {
        return this.eventList;
    }

    public void setEventList(List<Map<String, Object>> eventList) {
        this.eventList = eventList;
    }

    public List<Map<String, Object>> getEventTags() {
        return this.eventTags;
    }

    public void setEventTags(List<Map<String, Object>> eventTags) {
        this.eventTags = eventTags;
    }

    public Map<String, Object> getCommonRule() {
        return this.commonRule;
    }

    public void setCommonRule(Map<String, Object> commonRule) {
        this.commonRule = commonRule;
    }

    public List<Map<String, Object>> getSpecialRule() {
        return this.specialRule;
    }

    public void setSpecialRule(List<Map<String, Object>> specialRule) {
        this.specialRule = specialRule;
    }
}
