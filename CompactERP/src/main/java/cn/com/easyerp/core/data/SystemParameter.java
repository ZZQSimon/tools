package cn.com.easyerp.core.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SystemParameter {
    private String default_language;
    private String home_currency;
    private String upload_root;
    private String pdf_lib_path;
    private String mail_host;
    private String mail_user;
    private String mail_pwd;
    private String mail_addr;
    private int max_list_lines;
    private int input_selector_lines;
    private boolean mobile_login;
    private int thumbnail_height;
    private int thumbnail_width;
    private String encrypt_str;
    private String app_id;
    private String secret;
    private String agent_id;
    private String server_address;
    private String sms_key;
    private String sms_secret;
    private int pwd_strong_level;
    private String ftp_host;
    private int ftp_port;
    private String ftp_user;
    private String ftp_pwd;

    public String getDefault_language() {
        return this.default_language;
    }

    public void setDefault_language(String default_language) {
        this.default_language = default_language;
    }

    @JsonIgnore
    public String getHome_currency() {
        return this.home_currency;
    }

    public void setHome_currency(String home_currency) {
        this.home_currency = home_currency;
    }

    @JsonIgnore
    public String getUpload_root() {
        return this.upload_root;
    }

    public void setUpload_root(String upload_root) {
        this.upload_root = upload_root;
    }

    @JsonIgnore
    public String getPdf_lib_path() {
        return this.pdf_lib_path;
    }

    public void setPdf_lib_path(String pdf_lib_path) {
        this.pdf_lib_path = pdf_lib_path;
    }

    @JsonIgnore
    public String getMail_host() {
        return this.mail_host;
    }

    public void setMail_host(String mail_host) {
        this.mail_host = mail_host;
    }

    @JsonIgnore
    public String getMail_user() {
        return this.mail_user;
    }

    public void setMail_user(String mail_user) {
        this.mail_user = mail_user;
    }

    @JsonIgnore
    public String getMail_pwd() {
        return this.mail_pwd;
    }

    public void setMail_pwd(String mail_pwd) {
        this.mail_pwd = mail_pwd;
    }

    @JsonIgnore
    public String getMail_addr() {
        return this.mail_addr;
    }

    public void setMail_addr(String mail_addr) {
        this.mail_addr = mail_addr;
    }

    @JsonIgnore
    public int getMax_list_lines() {
        return this.max_list_lines;
    }

    public void setMax_list_lines(int max_list_lines) {
        this.max_list_lines = max_list_lines;
    }

    public int getInput_selector_lines() {
        return this.input_selector_lines;
    }

    public void setInput_selector_lines(int input_selector_lines) {
        this.input_selector_lines = input_selector_lines;
    }

    public boolean isMobile_login() {
        return this.mobile_login;
    }

    public void setMobile_login(boolean mobile_login) {
        this.mobile_login = mobile_login;
    }

    @JsonIgnore
    public int getThumbnail_height() {
        return this.thumbnail_height;
    }

    public void setThumbnail_height(int thumbnail_height) {
        this.thumbnail_height = thumbnail_height;
    }

    @JsonIgnore
    public int getThumbnail_width() {
        return this.thumbnail_width;
    }

    public void setThumbnail_width(int thumbnail_width) {
        this.thumbnail_width = thumbnail_width;
    }

    @JsonIgnore
    public String getEncrypt_str() {
        return this.encrypt_str;
    }

    public void setEncrypt_str(String encrypt_str) {
        this.encrypt_str = encrypt_str;
    }

    @JsonIgnore
    public String getApp_id() {
        return this.app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    @JsonIgnore
    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @JsonIgnore
    public String getAgent_id() {
        return this.agent_id;
    }

    public void setAgent_id(String agent_id) {
        this.agent_id = agent_id;
    }

    @JsonIgnore
    public String getServer_address() {
        return this.server_address;
    }

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    @JsonIgnore
    public String getFtp_host() {
        return this.ftp_host;
    }

    public void setFtp_host(String ftp_host) {
        this.ftp_host = ftp_host;
    }

    @JsonIgnore
    public int getFtp_port() {
        return this.ftp_port;
    }

    public void setFtp_port(int ftp_port) {
        this.ftp_port = ftp_port;
    }

    @JsonIgnore
    public String getFtp_user() {
        return this.ftp_user;
    }

    public void setFtp_user(String ftp_user) {
        this.ftp_user = ftp_user;
    }

    @JsonIgnore
    public String getFtp_pwd() {
        return this.ftp_pwd;
    }

    public void setFtp_pwd(String ftp_pwd) {
        this.ftp_pwd = ftp_pwd;
    }

    @JsonIgnore
    public String getSms_key() {
        return this.sms_key;
    }

    public void setSms_key(String sms_key) {
        this.sms_key = sms_key;
    }

    @JsonIgnore
    public String getSms_secret() {
        return this.sms_secret;
    }

    public void setSms_secret(String sms_secret) {
        this.sms_secret = sms_secret;
    }

    @JsonIgnore
    public int getPwd_strong_level() {
        return this.pwd_strong_level;
    }

    public void setPwd_strong_level(int pwd_strong_level) {
        this.pwd_strong_level = pwd_strong_level;
    }
}
