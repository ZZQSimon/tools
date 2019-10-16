package cn.com.easyerp.module.interfaces.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Data {
    Map<String, String> attrs;

    public Data() {
        this.attrs = new HashMap<>();
        this.head = new Head();
        this.entries = new ArrayList<>();
    }

    Head head;
    List<Entry> entries;

    public Data(HashMap<String, String> attr, Head head, List<Entry> bodies) {
        this.attrs = attr;
        this.head = head;
        this.entries = bodies;
    }

    public Map<String, String> getAttrs() {
        return this.attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    public void addAttr(String key, String value) {
        this.attrs.put(key, value);
    }

    public Head getHead() {
        return this.head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public List<Entry> getEntries() {
        return this.entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }

    public void addEntry(Entry body) {
        this.entries.add(body);
    }
}
