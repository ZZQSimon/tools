package cn.com.easyerp.core.data;

import java.util.Date;

public class CodeStrDescribe {
    private String UUID;
    private String str;
    private int valid_time;
    private String type;
    private Date cre_date;

    public String getUUID() {
        return this.UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getStr() {
        return this.str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public int getValid_time() {
        return this.valid_time;
    }

    public void setValid_time(int valid_time) {
        this.valid_time = valid_time;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCre_date() {
        return this.cre_date;
    }

    public void setCre_date(Date cre_date) {
        this.cre_date = cre_date;
    }
}
