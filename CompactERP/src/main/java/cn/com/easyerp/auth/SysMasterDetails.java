package cn.com.easyerp.auth;

public class SysMasterDetails {
    private String name;
    private int active;
    private int seq;
    private String driver;
    private String language_id = "cn";
    private String url;
    private String username;
    private String password;
    private int default_account;
    private String companyName;
    private String companyName_abbreviation;
    private String logo;

    public String getCompanyName_abbreviation() {
        return this.companyName_abbreviation;
    }

    public void setCompanyName_abbreviation(String companyName_abbreviation) {
        this.companyName_abbreviation = companyName_abbreviation;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getActive() {
        return this.active;
    }

    public void setActive(int active) {
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

    public int getDefault_account() {
        return this.default_account;
    }

    public void setDefault_account(int default_account) {
        this.default_account = default_account;
    }

    public String getCompanyName() {
        return this.companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLanguage_id() {
        return this.language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }
}
