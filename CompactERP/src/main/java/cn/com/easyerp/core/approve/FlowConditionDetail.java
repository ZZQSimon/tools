package cn.com.easyerp.core.approve;

public class FlowConditionDetail {
    private String table_id;
    private String connection_id;
    private String condition_detail_id;

    public String getTable_id() {
        return this.table_id;
    }

    private String column_name;
    private String symbol;
    private String value;
    private String name;

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnection_id() {
        return this.connection_id;
    }

    public void setConnection_id(String connection_id) {
        this.connection_id = connection_id;
    }

    public String getCondition_detail_id() {
        return this.condition_detail_id;
    }

    public void setCondition_detail_id(String condition_detail_id) {
        this.condition_detail_id = condition_detail_id;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
