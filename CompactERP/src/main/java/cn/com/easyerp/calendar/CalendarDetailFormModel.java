package cn.com.easyerp.calendar;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("calendarDetail")
public class CalendarDetailFormModel extends TableBasedFormModel {
    private Map<String, Object> ruleData;
    private List<Map<String, Object>> setData;
    private String calendar_id;

    public CalendarDetailFormModel(ActionType action, String parent, String table, Map<String, Object> ruleData,
            List<Map<String, Object>> setData, String calendar_id) {
        super(action, parent, table);
        this.ruleData = ruleData;
        this.setData = setData;
        this.calendar_id = calendar_id;
    }

    public String getCalendar_id() {
        return this.calendar_id;
    }

    public List<Map<String, Object>> getSetData() {
        return this.setData;
    }

    public Map<String, Object> getRuleData() {
        return this.ruleData;
    }

    public String getTitle() {
        DataService dataService = Common.getDataService();
        return dataService.getTableLabel("p_calendar_set");
    }
}
