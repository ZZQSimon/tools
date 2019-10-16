package cn.com.easyerp.core.mail.adidas;

import java.util.Date;

public class CodeStr {
    private String UUID;
    private String str;

    public String getUUID() {
        return this.UUID;
    }

    private int valid_time;
    private Date cre_date;
    private String type;

    public void setUUID(String uUID) {
        this.UUID = uUID;
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

    public Date getCre_date() {
        return this.cre_date;
    }

    public void setCre_date(Date cre_date) {
        this.cre_date = cre_date;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
