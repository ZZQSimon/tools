package cn.com.easyerp.core.cache;

public class ActionPrerequistieDescribe {
    private String table_action_id;
    private Integer seq;
    private String check_condition;
    private String level;
    private String violate_msg_international_id;
    private String violate_msg_param;
    private Integer is_using;
    private I18nDescribe violate_msg_I18N;
    private String _selected;

    public String get_selected() {
        return this._selected;
    }

    public void set_selected(String _selected) {
        this._selected = _selected;
    }

    public I18nDescribe getViolate_msg_I18N() {
        return this.violate_msg_I18N;
    }

    public void setViolate_msg_I18N(I18nDescribe violate_msg_I18N) {
        this.violate_msg_I18N = violate_msg_I18N;
    }

    public String getTable_action_id() {
        return this.table_action_id;
    }

    public void setTable_action_id(String table_action_id) {
        this.table_action_id = table_action_id;
    }

    public Integer getSeq() {
        return this.seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public String getCheck_condition() {
        return this.check_condition;
    }

    public void setCheck_condition(String check_condition) {
        this.check_condition = check_condition;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getViolate_msg_international_id() {
        return this.violate_msg_international_id;
    }

    public void setViolate_msg_international_id(String violate_msg_international_id) {
        this.violate_msg_international_id = violate_msg_international_id;
    }

    public String getViolate_msg_param() {
        return this.violate_msg_param;
    }

    public void setViolate_msg_param(String violate_msg_param) {
        this.violate_msg_param = violate_msg_param;
    }

    public Integer getIs_using() {
        return this.is_using;
    }

    public void setIs_using(Integer is_using) {
        this.is_using = is_using;
    }
}
