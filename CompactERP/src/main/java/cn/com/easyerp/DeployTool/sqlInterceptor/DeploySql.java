package cn.com.easyerp.DeployTool.sqlInterceptor;

import java.util.Date;

public class DeploySql {
    private String sql;
    private String db;
    private String user;
    private Date date;
    private int is_update_table;

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getDb() {
        return this.db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDate() {
        return this.date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getIs_update_table() {
        return this.is_update_table;
    }

    public void setIs_update_table(int is_update_table) {
        this.is_update_table = is_update_table;
    }
}
