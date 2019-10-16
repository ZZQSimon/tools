package cn.com.easyerp.core.widget.inputSelect;

import com.fasterxml.jackson.core.type.TypeReference;

public class RefTreeDescribe {
    public static TypeReference<RefTreeDescribe> jsonRef;
    private String root;
    private String valid;

    public String getRoot() {
        return this.root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getValid() {
        return this.valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }
}
