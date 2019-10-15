package cn.com.easyerp.core.approve;

public class FlowLine {
    private String table_id;
    private String connection_id;
    private String page_source_id;
    private String page_source_place;
    private String page_target_id;
    private String page_target_place;
    private String source_text;
    private String target_text;
    private String memo;

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getPage_source_place() {
        return this.page_source_place;
    }

    public void setPage_source_place(String page_source_place) {
        this.page_source_place = page_source_place;
    }

    public String getPage_target_place() {
        return this.page_target_place;
    }

    public void setPage_target_place(String page_target_place) {
        this.page_target_place = page_target_place;
    }

    public String getConnection_id() {
        return this.connection_id;
    }

    public void setConnection_id(String connection_id) {
        this.connection_id = connection_id;
    }

    public String getPage_source_id() {
        return this.page_source_id;
    }

    public void setPage_source_id(String page_source_id) {
        this.page_source_id = page_source_id;
    }

    public String getPage_target_id() {
        return this.page_target_id;
    }

    public void setPage_target_id(String page_target_id) {
        this.page_target_id = page_target_id;
    }

    public String getSource_text() {
        return this.source_text;
    }

    public void setSource_text(String source_text) {
        this.source_text = source_text;
    }

    public String getTarget_text() {
        return this.target_text;
    }

    public void setTarget_text(String target_text) {
        this.target_text = target_text;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
