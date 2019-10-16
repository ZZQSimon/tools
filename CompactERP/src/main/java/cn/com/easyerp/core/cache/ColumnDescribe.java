package cn.com.easyerp.core.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.core.cache.style.ColumnViewStyle;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.widget.inputSelect.RefTreeDescribe;
import cn.com.easyerp.framework.common.Common;

public class ColumnDescribe {
    // private static TypeReference<ColumnViewStyle> viewStyleJsonRef = new
    // TypeReference<ColumnViewStyle>() {
    // };
    private static TypeReference<Map<String, FilterDescribe>> refFilterJsonRef = new TypeReference<Map<String, FilterDescribe>>() {

    };
    private String table_id;
    private String column_name;
    private String url_id;
    private boolean virtual;
    private String tab_name;
    private String group_name;
    private int data_type;
    private String data_format;
    private Integer min_len;
    private Integer max_len;
    private Integer is_condition;
    private String dic_id;
    private String ref_table_name;
    private String ref_table_cols;
    private String ref_table_sql;
    private String ref_table_filter;
    private boolean ref_table_display;
    private String ref_table_tree;
    private boolean ref_table_new;
    private String formula;
    private String default_value;
    private String prefix;
    private String suffix;
    private Integer sum_flag;
    private String complex_id;
    private String complex_param;
    private boolean ro_insert;
    private boolean ro_update;
    private String read_only_condition;
    private String read_only_clear;
    private boolean hidden;
    private boolean wrap;
    private int is_multiple;
    private String view_style;
    private String link_json;
    private int cell_cnt;
    private int seq;
    private String memo;
    private int is_id_column;
    private int is_auth;
    private int isCalendarEvent;
    private int is_encrypt;
    private String calendarEventDefaultColor;
    private String international_id;
    private String quick_filter;
    private String module;
    private String level;
    private String nesting_column_name;
    private I18nDescribe i18n;
    private Map<String, String> ref_mapping;
    private Map<String, String> complex_mapping;
    private List<String> auto_gen_children;
    private FormatDesc format;
    private ColumnViewStyle viewStyle;
    private Map<String, FilterDescribe> refFilter;
    private RefTreeDescribe refTree;
    private ColumnLink link;
    private int mobile_column;
    private int columnChange;

    public int getIs_auth() {
        return this.is_auth;
    }

    public void setIs_auth(int is_auth) {
        this.is_auth = is_auth;
    }

    public int getColumnChange() {
        return this.columnChange;
    }

    public void setColumnChange(int columnChange) {
        this.columnChange = columnChange;
    }

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

    public boolean isVirtual() {
        return this.virtual;
    }

    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    public String getTab_name() {
        return this.tab_name;
    }

    public void setTab_name(String tab_name) {
        this.tab_name = tab_name;
    }

    public String getGroup_name() {
        return this.group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getData_type() {
        return this.data_type;
    }

    public void setData_type(int data_type) {
        this.data_type = data_type;
    }

    public String getData_format() {
        return this.data_format;
    }

    public void setData_format(String data_format) {
        this.data_format = data_format;
    }

    public Integer getMin_len() {
        return this.min_len;
    }

    public void setMin_len(Integer min_len) {
        this.min_len = min_len;
    }

    public Integer getMax_len() {
        return this.max_len;
    }

    public void setMax_len(Integer max_len) {
        this.max_len = max_len;
    }

    public Integer getIs_condition() {
        if (this.is_condition == null)
            return Integer.valueOf(0);
        return this.is_condition;
    }

    public void setIs_condition(Integer is_condition) {
        this.is_condition = is_condition;
    }

    public String getDic_id() {
        return this.dic_id;
    }

    public void setDic_id(String dic_id) {
        this.dic_id = dic_id;
    }

    public String getRef_table_name() {
        return this.ref_table_name;
    }

    public void setRef_table_name(String ref_table_name) {
        this.ref_table_name = ref_table_name;
    }

    public String getRef_table_cols() {
        return this.ref_table_cols;
    }

    public void setRef_table_cols(String ref_table_cols) {
        this.ref_table_cols = ref_table_cols;
    }

    public String getRef_table_sql() {
        return this.ref_table_sql;
    }

    public void setRef_table_sql(String ref_table_sql) {
        this.ref_table_sql = ref_table_sql;
    }

    @JsonIgnore
    public String getRef_table_filter() {
        return this.ref_table_filter;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void setRef_table_filter(String ref_table_filter) {
        this.ref_table_filter = ref_table_filter;
        if (ref_table_filter != null) {
            this.refFilter = (Map) Common.fromJson(ref_table_filter, refFilterJsonRef);
        }
    }

    public Map<String, FilterDescribe> getRefFilter() {
        return this.refFilter;
    }

    public boolean isRef_table_display() {
        return this.ref_table_display;
    }

    public void setRef_table_display(boolean ref_table_display) {
        this.ref_table_display = ref_table_display;
    }

    public int getIs_multiple() {
        return this.is_multiple;
    }

    public void setIs_multiple(int is_multiple) {
        this.is_multiple = is_multiple;
    }

    @JsonIgnore
    public String getRef_table_tree() {
        return this.ref_table_tree;
    }

    public void setRef_table_tree(String ref_table_tree) {
        this.ref_table_tree = ref_table_tree;
        if (Common.notBlank(ref_table_tree)) {
            this.refTree = (RefTreeDescribe) Common.fromJson(ref_table_tree, RefTreeDescribe.jsonRef);
        }
    }

    public RefTreeDescribe getRefTree() {
        return this.refTree;
    }

    public boolean isRef_table_new() {
        return this.ref_table_new;
    }

    public void setRef_table_new(boolean ref_table_new) {
        this.ref_table_new = ref_table_new;
    }

    public String getFormula() {
        return this.formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getDefault_value() {
        return this.default_value;
    }

    public void setDefault_value(String default_value) {
        this.default_value = default_value;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Integer getSum_flag() {
        return this.sum_flag;
    }

    public void setSum_flag(Integer sum_flag) {
        this.sum_flag = sum_flag;
    }

    public String getComplex_id() {
        return this.complex_id;
    }

    public void setComplex_id(String complex_id) {
        this.complex_id = complex_id;
    }

    public String getComplex_param() {
        return this.complex_param;
    }

    public void setComplex_param(String complex_param) {
        this.complex_param = complex_param;
    }

    public boolean isRo_insert() {
        return this.ro_insert;
    }

    public void setRo_insert(boolean ro_insert) {
        this.ro_insert = ro_insert;
    }

    public boolean isRo_update() {
        return this.ro_update;
    }

    public void setRo_update(boolean ro_update) {
        this.ro_update = ro_update;
    }

    public String getRead_only_condition() {
        return this.read_only_condition;
    }

    public void setRead_only_condition(String read_only_condition) {
        this.read_only_condition = read_only_condition;
    }

    public String getRead_only_clear() {
        return this.read_only_clear;
    }

    public void setRead_only_clear(String read_only_clear) {
        this.read_only_clear = read_only_clear;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isWrap() {
        return this.wrap;
    }

    public void setWrap(boolean wrap) {
        this.wrap = wrap;
    }

    public int getCell_cnt() {
        return this.cell_cnt;
    }

    public void setCell_cnt(int cell_cnt) {
        this.cell_cnt = cell_cnt;
    }

    @JsonIgnore
    public String getLink_json() {
        return this.link_json;
    }

    public void setLink_json(String link_json) {
        this.link_json = link_json;
        if (Common.notBlank(link_json)) {
            this.link = (ColumnLink) Common.fromJson(link_json, ColumnLink.jsonRef);
        }
    }

    public String getNesting_column_name() {
        return this.nesting_column_name;
    }

    public void setNesting_column_name(String nesting_column_name) {
        this.nesting_column_name = nesting_column_name;
    }

    public ColumnLink getLink() {
        return this.link;
    }

    public void setLink(ColumnLink link) {
        this.link = link;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public I18nDescribe getI18n() {
        return this.i18n;
    }

    public void setI18n(I18nDescribe i18n) {
        this.i18n = i18n;
    }

    public Map<String, String> getRef_mapping() {
        return this.ref_mapping;
    }

    public void setRef_mapping(Map<String, String> ref_mapping) {
        this.ref_mapping = ref_mapping;
    }

    public Map<String, String> getComplex_mapping() {
        return this.complex_mapping;
    }

    public void setComplex_mapping(Map<String, String> complex_mapping) {
        this.complex_mapping = complex_mapping;
    }

    public void addAutoGenChild(String child) {
        if (this.auto_gen_children == null)
            this.auto_gen_children = new ArrayList<>();
        this.auto_gen_children.add(child);
    }

    public List<String> getAuto_gen_children() {
        return this.auto_gen_children;
    }

    public FormatDesc getFormat() {
        return this.format;
    }

    public void setFormat(FormatDesc format) {
        this.format = format;
    }

    @JsonIgnore
    public String getView_style() {
        return this.view_style;
    }

    public void setView_style(String view_style) {
        this.view_style = view_style;
    }

    public void setViewStyle(ColumnViewStyle viewStyle) {
        this.viewStyle = viewStyle;
    }

    public ColumnViewStyle getViewStyle() {
        return this.viewStyle;
    }

    public int getIs_id_column() {
        return this.is_id_column;
    }

    public void setIs_id_column(int is_id_column) {
        this.is_id_column = is_id_column;
    }

    public int getIs_encrypt() {
        return this.is_encrypt;
    }

    public void setIs_encrypt(int is_encrypt) {
        this.is_encrypt = is_encrypt;
    }

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public String getQuick_filter() {
        return this.quick_filter;
    }

    public void setQuick_filter(String quick_filter) {
        this.quick_filter = quick_filter;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getMobile_column() {
        return this.mobile_column;
    }

    public void setMobile_column(int mobile_column) {
        this.mobile_column = mobile_column;
    }

    public int getIsCalendarEvent() {
        return this.isCalendarEvent;
    }

    public void setIsCalendarEvent(int isCalendarEvent) {
        this.isCalendarEvent = isCalendarEvent;
    }

    public String getCalendarEventDefaultColor() {
        return this.calendarEventDefaultColor;
    }

    public void setCalendarEventDefaultColor(String calendarEventDefaultColor) {
        this.calendarEventDefaultColor = calendarEventDefaultColor;
    }
}
