package cn.com.easyerp.core.authGroup;

public class Table {
    private String id;
    private String name;

    public String getModule() {
        return this.module;
    }

    private String module;
    private int is_approve_state;

    public void setModule(String module) {
        this.module = module;
    }

    public int getIs_approve_state() {
        return this.is_approve_state;
    }

    public void setIs_approve_state(int is_approve_state) {
        this.is_approve_state = is_approve_state;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
