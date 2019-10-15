package cn.com.easyerp.core.authGroup;

public class TableAction {
    private String action_id;
    private String table_id;
    private String system_type;
    private String action_name_international;

    public String getOperate_name() {
        return this.operate_name;
    }

    private int is_using;
    private String memo;
    private String report_id;
    private String operate_name;

    public void setOperate_name(String operate_name) {
        this.operate_name = operate_name;
    }

    public String getAction_id() {
        return this.action_id;
    }

    public void setAction_id(String action_id) {
        this.action_id = action_id;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getSystem_type() {
        return this.system_type;
    }

    public void setSystem_type(String system_type) {
        this.system_type = system_type;
    }

    public String getAction_name_international() {
        return this.action_name_international;
    }

    public void setAction_name_international(String action_name_international) {
        this.action_name_international = action_name_international;
    }

    public int getIs_using() {
        return this.is_using;
    }

    public void setIs_using(int is_using) {
        this.is_using = is_using;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getReport_id() {
        return this.report_id;
    }

    public void setReport_id(String report_id) {
        this.report_id = report_id;
    }
}
