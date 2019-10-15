package cn.com.easyerp.core.data;

import java.util.Map;

public class FormulaRequestModel {
    private String id;
    private String dataId;
    private String formula;
    private String column;
    private String table;
    private Map<String, Object> withNoIdData;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataId() {
        return this.dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, Object> getWithNoIdData() {
        return this.withNoIdData;
    }

    public void setWithNoIdData(Map<String, Object> withNoIdData) {
        this.withNoIdData = withNoIdData;
    }
}
