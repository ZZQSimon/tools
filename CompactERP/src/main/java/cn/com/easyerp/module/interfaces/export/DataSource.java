package cn.com.easyerp.module.interfaces.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSource {
    List<Data> dataSet;

    public DataSource() {
        this.dataSet = new ArrayList<>();
        this.attr = new HashMap<>();
    }

    Map<String, String> attr;

    public DataSource(List<Data> dataSet, Map<String, String> attr) {
        this.dataSet = dataSet;
        this.attr = attr;
    }

    public List<Data> getDataSet() {
        return this.dataSet;
    }

    public void setDataSet(List<Data> dataSet) {
        this.dataSet = dataSet;
    }

    public void addData(Data data) {
        this.dataSet.add(data);
    }

    public Map<String, String> getAttr() {
        return this.attr;
    }

    public void setAttr(Map<String, String> attr) {
        this.attr = attr;
    }

    public void addAttr(String key, String value) {
        this.attr.put(key, value);
    }
}
