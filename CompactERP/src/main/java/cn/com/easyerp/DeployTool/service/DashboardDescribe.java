package cn.com.easyerp.DeployTool.service;

public class DashboardDescribe {
    public static final String DASHBOARD_TYPE_NOTICE = "1";
    public static final String DASHBOARD_TYPE_APPROVE = "2";
    public static final String DASHBOARD_TYPE_CALENDAR = "3";
    public static final String DASHBOARD_TYPE_PERCENT = "4";
    public static final String DASHBOARD_TYPE_PROGRESS = "5";
    public static final String DASHBOARD_TYPE_CHART = "6";
    public static final String DASHBOARD_TYPE_LIST = "7";
    public static final String DASHBOARD_TYPE_MENU = "9";
    public static final String SUBSCRIBE_TYPE_FORCE = "1";
    public static final String SUBSCRIBE_TYPE_DEFAULT = "2";
    public static final String SUBSCRIBE_TYPE_HIDDEN = "3";
    private String dashboard_id;
    private String dashboard_name;
    private String dashboard_title;
    private int dashboard_size;
    private String dashboard_type;
    private String dashboard_param;
    private String subscribe_type;
    private String dashboard_richtext;
    private String subscribe_id;

    public DashboardDescribe() {
        this.dashboard_id = getUUID();
    }

    public String getDashboard_id() {
        return this.dashboard_id;
    }

    public void setDashboard_id(String dashboard_id) {
        this.dashboard_id = dashboard_id;
    }

    public String getDashboard_name() {
        return this.dashboard_name;
    }

    public void setDashboard_name(String dashboard_name) {
        this.dashboard_name = dashboard_name;
    }

    public String getDashboard_title() {
        return this.dashboard_title;
    }

    public void setDashboard_title(String dashboard_title) {
        this.dashboard_title = dashboard_title;
    }

    public int getDashboard_size() {
        return this.dashboard_size;
    }

    public void setDashboard_size(int dashboard_size) {
        this.dashboard_size = dashboard_size;
    }

    public String getDashboard_type() {
        return this.dashboard_type;
    }

    public void setDashboard_type(String dashboard_type) {
        this.dashboard_type = dashboard_type;
    }

    public String getDashboard_param() {
        return this.dashboard_param;
    }

    public void setDashboard_param(String dashboard_param) {
        this.dashboard_param = dashboard_param;
    }

    public String getSubscribe_type() {
        return this.subscribe_type;
    }

    public void setSubscribe_type(String subscribe_type) {
        this.subscribe_type = subscribe_type;
    }

    public String getDashboard_richtext() {
        return this.dashboard_richtext;
    }

    public void setDashboard_richtext(String dashboard_richtext) {
        this.dashboard_richtext = dashboard_richtext;
    }

    public static String getUUID() {
        return Long.toHexString(System.currentTimeMillis());
    }

    public String getSubscribe_id() {
        return this.subscribe_id;
    }

    public void setSubscribe_id(String subscribe_id) {
        this.subscribe_id = subscribe_id;
    }
}
