package cn.com.easyerp.core.widget.grid;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.widget.FieldModelBase;

public class GridModel extends StdGridModel {
    private Map<String, Object> parentKey;
    private boolean inMemory = false;
    private boolean listing = false;
    private boolean hasSum = true;
    private boolean autoLoad = false;
    private List<Map<String, Object>> childDatas;
    private Set<String> noAuthColumns = null;

    private Map<String, List<UserColumn>> userColumns;

    private boolean strict = false;
    private boolean isRefChild;
    private String refChildColumn;

    public GridModel(String table, Map<String, Object> parentKey) {
        this(table, parentKey, false);
    }

    public GridModel(String table, Map<String, Object> parentKey, boolean inMemory) {
        super(table);
        this.parentKey = parentKey;
        this.inMemory = inMemory;
    }

    public GridModel(String table, Map<String, Object> parentKey, boolean inMemory, boolean isRefChild,
            String refChildColumn) {
        super(table);
        this.parentKey = parentKey;
        this.inMemory = inMemory;
        this.isRefChild = isRefChild;
        this.refChildColumn = refChildColumn;
    }

    public Map<String, List<UserColumn>> getUserColumns() {
        return this.userColumns;
    }

    public void setUserColumns(Map<String, List<UserColumn>> userColumns) {
        this.userColumns = userColumns;
    }

    @SuppressWarnings({  "rawtypes" })
    public List<ColumnDescribe> getUserColumnByTableName(String tableName) {
        if (this.userColumns == null)
            return null;
        if (this.userColumns.get(tableName) == null) {
            return null;
        }
        List<ColumnDescribe> columns = new ArrayList<ColumnDescribe>();
        for (int i = 0; i < ((List) this.userColumns.get(tableName)).size(); i++)
            columns.add(((UserColumn) ((List) this.userColumns.get(tableName)).get(i)).getColumnDescribe());
        return columns;
    }

    public boolean isInMemory() {
        return this.inMemory;
    }

    public boolean isListing() {
        return this.listing;
    }

    public void setListing(boolean listing) {
        this.listing = listing;
    }

    public boolean isHasSum() {
        return this.hasSum;
    }

    public void setHasSum(boolean hasSum) {
        this.hasSum = hasSum;
    }

    public boolean isAutoLoad() {
        return this.autoLoad;
    }

    public void setAutoLoad(boolean autoLoad) {
        this.autoLoad = autoLoad;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public Set<String> getNoAuthColumns() {
        return this.noAuthColumns;
    }

    public void setNoAuthColumns(Set<String> noAuthColumns) {
        this.noAuthColumns = noAuthColumns;
    }

    public RecordModel newRecord(List<FieldModelBase> fields) {
        return new RecordModel(fields, RecordModel.Status.inserted);
    }

    public List<Map<String, Object>> getChildDatas() {
        return this.childDatas;
    }

    public void setChildDatas(List<Map<String, Object>> childDatas) {
        this.childDatas = childDatas;
    }

    @JsonIgnore
    public Map<String, Object> getParentKey() {
        return this.parentKey;
    }

    public boolean isRefChild() {
        return this.isRefChild;
    }

    public void setRefChild(boolean isRefChild) {
        this.isRefChild = isRefChild;
    }

    public String getRefChildColumn() {
        return this.refChildColumn;
    }

    public void setRefChildColumn(String refChildColumn) {
        this.refChildColumn = refChildColumn;
    }
}
