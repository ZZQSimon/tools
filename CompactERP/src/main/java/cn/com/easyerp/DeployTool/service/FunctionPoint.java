package cn.com.easyerp.DeployTool.service;

public class FunctionPoint {
    private String id;
    private String name;
    private String table_id;
    private String url_id;
    private String param;
    private String module;
    private String url_name;

    public String getUrl_name() {
        return this.url_name;
    }

    public void setUrl_name(String url_name) {
        this.url_name = url_name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getUrl_id() {
        return this.url_id;
    }

    public void setUrl_id(String url_id) {
        this.url_id = url_id;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
