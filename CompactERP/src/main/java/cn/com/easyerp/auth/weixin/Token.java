package cn.com.easyerp.auth.weixin;

import java.util.Date;

public class Token {
    private String token;
    private Date date;
    private static final long EXPIRES_IN = 7000000L;

    public Token(String token, Date date) {
        this.token = token;
        this.date = date;
    }

    public String getToken() {
        if (this.date == null)
            return null;
        Date nowDate = new Date();
        if (nowDate.getTime() - this.date.getTime() > EXPIRES_IN) {
            return null;
        }
        return this.token;
    }
}
