package cn.com.easyerp.core.cache;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class TableCheckRuleDescribe {
    private String table_id;
    private String column_name;
    private String url_id;
    private boolean is_error;
    private int seq;
    private String formula;
    private String error_msg_id;
    private String error_msg_param;
    private String type;
    private String memo;
    private int check_level;
    private int create_submit;
    private int edit_submit;
    private int input_blur;
    private String module;
    private I18nDescribe msgI18n;
    private String _selected;

    public String get_selected() {
        return this._selected;
    }

    public void set_selected(String _selected) {
        this._selected = _selected;
    }

    public I18nDescribe getMsgI18n() {
        return this.msgI18n;
    }

    public void setMsgI18n(I18nDescribe msgI18n) {
        this.msgI18n = msgI18n;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @JsonIgnore
    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getColumn_name() {
        return this.column_name;
    }

    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    public String getUrl_id() {
        return this.url_id;
    }

    public void setUrl_id(String url_id) {
        this.url_id = url_id;
    }

    public boolean isIs_error() {
        return this.is_error;
    }

    public void setIs_error(boolean is_error) {
        this.is_error = is_error;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getError_msg_id() {
        return this.error_msg_id;
    }

    public void setError_msg_id(String error_msg_id) {
        this.error_msg_id = error_msg_id;
    }

    public String getError_msg_param() {
        return this.error_msg_param;
    }

    public void setError_msg_param(String error_msg_param) {
        this.error_msg_param = error_msg_param;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public int getCheck_level() {
        return this.check_level;
    }

    public void setCheck_level(int check_level) {
        this.check_level = check_level;
        if (check_level == 1) {
            this.is_error = true;
        }
    }

    public int getCreate_submit() {
        return this.create_submit;
    }

    public void setCreate_submit(int create_submit) {
        this.create_submit = create_submit;
    }

    public int getEdit_submit() {
        return this.edit_submit;
    }

    public void setEdit_submit(int edit_submit) {
        this.edit_submit = edit_submit;
    }

    public int getInput_blur() {
        return this.input_blur;
    }

    public void setInput_blur(int input_blur) {
        this.input_blur = input_blur;
    }
}
