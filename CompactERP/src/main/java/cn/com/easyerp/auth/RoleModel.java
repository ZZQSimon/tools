package cn.com.easyerp.auth;

import org.springframework.security.access.ConfigAttribute;

public class RoleModel implements ConfigAttribute {
    private static final long serialVersionUID = -609865167251202809L;
    private String id;
    private String name;

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

    public String getAttribute() {
        return this.id;
    }
}
