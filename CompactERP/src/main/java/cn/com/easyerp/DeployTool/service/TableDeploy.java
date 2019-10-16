package cn.com.easyerp.DeployTool.service;

public class TableDeploy {
    private String id;
    private String type;

    public String getId() {
        return this.id;
    }

    private String parent_id;
    private String lan;

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParent_id() {
        return this.parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getLan() {
        return this.lan;
    }

    public void setLan(String lan) {
        this.lan = lan;
    }
}
