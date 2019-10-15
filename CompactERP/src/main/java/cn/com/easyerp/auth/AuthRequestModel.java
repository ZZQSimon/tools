package cn.com.easyerp.auth;

import java.util.List;
import java.util.Map;

public class AuthRequestModel {
    class AuthEntry {
        private String id;
        private List<String> owners;

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<String> getOwners() {
            return this.owners;
        }

        public void setOwners(List<String> owners) {
            this.owners = owners;
        }
    }

    static enum Type {
        operation;
    }

    private Type type;
    private Map<String, Object> noAuthRows;
    private List<AuthEntry> entries;

    public Map<String, Object> getNoAuthRows() {
        return this.noAuthRows;
    }

    public void setNoAuthRows(Map<String, Object> noAuthRows) {
        this.noAuthRows = noAuthRows;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<AuthEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(List<AuthEntry> entries) {
        this.entries = entries;
    }
}