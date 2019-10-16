package cn.com.easyerp.core.widget.grid;

import java.util.Date;

import cn.com.easyerp.core.cache.ColumnDescribe;

public class UserColumn {
    private String user_id;
    private String table_name;
    private String column_name;
    private Integer seq;
    private String memo;
    private ColumnDescribe columnDescribe;
    private String cre_user;
    private Date cre_date;
    private String upd_user;
    private Date upd_date;

    public ColumnDescribe getColumnDescribe() {
        return this.columnDescribe;
    }

    public void setColumnDescribe(ColumnDescribe columnDescribe) {
        this.columnDescribe = columnDescribe;
    }

    public String getUser_id() {
        return this.user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTable_name() {
        return this.table_name;
    }

    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public Integer getSeq() {
        return this.seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCre_user() {
        return this.cre_user;
    }

    public void setCre_user(String cre_user) {
        this.cre_user = cre_user;
    }

    public Date getCre_date() {
        return this.cre_date;
    }

    public void setCre_date(Date cre_date) {
        this.cre_date = cre_date;
    }

    public String getUpd_user() {
        return this.upd_user;
    }

    public void setUpd_user(String upd_user) {
        this.upd_user = upd_user;
    }

    public Date getUpd_date() {
        return this.upd_date;
    }

    public void setUpd_date(Date upd_date) {
        this.upd_date = upd_date;
    }
}
