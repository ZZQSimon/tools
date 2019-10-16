package cn.com.easyerp.core.timedTask.entity;

public class TimeTaskBusinessTimeDescribe {
    private String task_id;
    private String business_time_id;
    private String table;
    private String column;
    private int lead;
    private String lead_type;
    private String filter_sql;
    private int is_using;

    public int getIs_using() {
        return this.is_using;
    }

    public void setIs_using(int is_using) {
        this.is_using = is_using;
    }

    public String getTask_id() {
        return this.task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getBusiness_time_id() {
        return this.business_time_id;
    }

    public void setBusiness_time_id(String business_time_id) {
        this.business_time_id = business_time_id;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public int getLead() {
        return this.lead;
    }

    public void setLead(int lead) {
        this.lead = lead;
    }

    public String getLead_type() {
        return this.lead_type;
    }

    public void setLead_type(String lead_type) {
        this.lead_type = lead_type;
    }

    public String getFilter_sql() {
        return this.filter_sql;
    }

    public void setFilter_sql(String filter_sql) {
        this.filter_sql = filter_sql;
    }
}
