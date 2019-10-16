package cn.com.easyerp.DeployTool.service;

public class Passage {
    private String id;
    private String name;
    private int type;
    private String main_table;
    private String group_cols;
    private String disp_cols;

    public String getFrontUpdated() {
        return this.frontUpdated;
    }

    private String filter_tables;
    private String filter_sql;
    private int has_append;
    private String frontInsert;
    private String frontDeleted;
    private String frontUpdated;

    public void setFrontUpdated(String frontUpdated) {
        this.frontUpdated = frontUpdated;
    }

    public String getFrontInsert() {
        return this.frontInsert;
    }

    public void setFrontInsert(String frontInsert) {
        this.frontInsert = frontInsert;
    }

    public String getFrontDeleted() {
        return this.frontDeleted;
    }

    public void setFrontDeleted(String frontDeleted) {
        this.frontDeleted = frontDeleted;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMain_table() {
        return this.main_table;
    }

    public void setMain_table(String main_table) {
        this.main_table = main_table;
    }

    public String getGroup_cols() {
        return this.group_cols;
    }

    public void setGroup_cols(String group_cols) {
        this.group_cols = group_cols;
    }

    public String getDisp_cols() {
        return this.disp_cols;
    }

    public void setDisp_cols(String disp_cols) {
        this.disp_cols = disp_cols;
    }

    public String getFilter_tables() {
        return this.filter_tables;
    }

    public void setFilter_tables(String filter_tables) {
        this.filter_tables = filter_tables;
    }

    public String getFilter_sql() {
        return this.filter_sql;
    }

    public void setFilter_sql(String filter_sql) {
        this.filter_sql = filter_sql;
    }

    public int getHas_append() {
        return this.has_append;
    }

    public void setHas_append(int has_append) {
        this.has_append = has_append;
    }
}
