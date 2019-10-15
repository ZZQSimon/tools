package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.List;

public class TableTabDescribe {
    private String id;
    private List<TableGroupDescribe> groups;

    public TableTabDescribe(String id) {
        this.id = id;
        this.groups = new ArrayList<>();
    }

    public void addGroup(TableGroupDescribe group) {
        this.groups.add(group);
    }

    public String getId() {
        return this.id;
    }

    public List<TableGroupDescribe> getGroups() {
        return this.groups;
    }
}
