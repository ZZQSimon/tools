package cn.com.easyerp.auth;

import java.util.ArrayList;
import java.util.List;

public class AuthConfigDescribe {
    public static enum Target {
        MENU, TABLE, COLUMN, REPORT, ACTION, ACTION_LINK;
    }

    public static enum Type {
        R, W, C, U, D;
    }

    private Target target;
    private String target_id;
    private Type type;
    private String auth_id;
    private List<AuthEntryModel> entries;

    public Target getTarget() {
        return this.target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public String getTarget_id() {
        return this.target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getAuth_id() {
        return this.auth_id;
    }

    public void setAuth_id(String auth_id) {
        this.auth_id = auth_id;
    }

    public List<AuthEntryModel> getEntries() {
        return this.entries;
    }

    public void setEntries(List<AuthEntryModel> entries) {
        this.entries = entries;
    }

    public void addEntries(List<AuthEntryModel> entries) {
        if (entries == null)
            return;
        if (this.entries == null)
            this.entries = new ArrayList<>();
        this.entries.addAll(entries);
    }
}