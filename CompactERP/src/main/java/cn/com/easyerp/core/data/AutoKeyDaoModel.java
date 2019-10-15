package cn.com.easyerp.core.data;

public class AutoKeyDaoModel {
    private String table_name;
    private String parent_no;
    private Integer if_update;
    private Integer current_no;
    private String next_id;
    private Integer next_no;

    public AutoKeyDaoModel(String table_name, String parent_no, Integer if_update, Integer current_no) {
        this.table_name = table_name;
        this.parent_no = parent_no;
        this.if_update = if_update;
        this.current_no = current_no;
    }

    public String getTable_name() {
        return this.table_name;
    }

    public String getParent_no() {
        return this.parent_no;
    }

    public Integer getIf_update() {
        return this.if_update;
    }

    public Integer getCurrent_no() {
        return this.current_no;
    }

    public String getNext_id() {
        return this.next_id;
    }

    public void setNext_id(String next_id) {
        this.next_id = next_id;
    }

    public Integer getNext_no() {
        return this.next_no;
    }

    public void setNext_no(Integer next_no) {
        this.next_no = next_no;
    }
}
