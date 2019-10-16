package cn.com.easyerp.email;

import java.beans.Transient;
import java.util.Calendar;
import java.util.Date;

public class UserModel {
    private Long id;
    private String name;
    private String password;
    private String email;
    private int status = 0;

    private String validateCode;

    private Date registerTime;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getValidateCode() {
        return this.validateCode;
    }

    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }

    public Date getRegisterTime() {
        return this.registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    @Transient
    public Date getLastActivateTime() {
        Calendar cl = Calendar.getInstance();
        cl.setTime(this.registerTime);
        cl.add(5, 2);

        return cl.getTime();
    }
}
