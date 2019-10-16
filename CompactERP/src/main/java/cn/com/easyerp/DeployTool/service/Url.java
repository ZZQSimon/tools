package cn.com.easyerp.DeployTool.service;

public class Url {
    private String id;
    private String name;
    private String url;

    public String getId() {
        return this.id;
    }

    private String param;
    private String memo;
    private String router_url;
    private String module;

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getRouter_url() {
        return this.router_url;
    }

    public void setRouter_url(String router_url) {
        this.router_url = router_url;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
