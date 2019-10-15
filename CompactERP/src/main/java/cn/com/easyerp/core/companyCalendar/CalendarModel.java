package cn.com.easyerp.core.companyCalendar;

import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("pc_calendar")
public class CalendarModel extends FormModelBase {
    private String type;
    private Map<String, Object> param;
    private String table;

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

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    protected CalendarModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "pc_calendar";
    }
}
