package cn.com.easyerp.core.cache;

public class OrderByDescribe {
    private String table_id;
    private String column_name;
    private int seq;
    private String order_rule;

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getOrder_rule() {
        return this.order_rule;
    }

    public void setOrder_rule(String order_rule) {
        this.order_rule = order_rule;
    }
}
