package cn.com.easyerp.core.master;

public class DataSourceDescModel {
    private String name;
    private boolean active;
    private int seq;
    private String driver;
    private String url;
    private String username;
    private String password;
    private boolean default_account;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDriver() {
        return this.driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDefault_account() {
        return this.default_account;
    }

    public void setDefault_account(boolean default_account) {
        this.default_account = default_account;
    }
}
