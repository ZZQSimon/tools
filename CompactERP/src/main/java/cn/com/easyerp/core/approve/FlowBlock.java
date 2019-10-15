package cn.com.easyerp.core.approve;

import java.util.ArrayList;
import java.util.List;

import cn.com.easyerp.core.cache.I18nDescribe;

public class FlowBlock {
    private String table_id;
    private String block_id;
    private String type;
    private String text;
    private int block_x;
    private int block_y;
    private String approve_name;
    private String task_msg;
    private String turn_down_up;
    private String turn_down_source;
    private String time_limit;
    private String time_out_rule;
    private int need_agree_count_people;
    private int need_opinion_count_people;
    private int is_addApprove;
    private int addApprove_count;
    private int is_until_block;
    private int is_approve_block;
    private int is_work_block;
    private String notPassType;
    private I18nDescribe approve_name_I18N;
    private List<FlowBlockEditColumn> flowBlockEditColumns;

    public int getIs_work_block() {
        return this.is_work_block;
    }

    public void setIs_work_block(int is_work_block) {
        this.is_work_block = is_work_block;
    }

    public int getIs_until_block() {
        return this.is_until_block;
    }

    public void setIs_until_block(int is_until_block) {
        this.is_until_block = is_until_block;
    }

    public int getIs_approve_block() {
        return this.is_approve_block;
    }

    public void setIs_approve_block(int is_approve_block) {
        this.is_approve_block = is_approve_block;
    }

    public I18nDescribe getApprove_name_I18N() {
        return this.approve_name_I18N;
    }

    public void setApprove_name_I18N(I18nDescribe approve_name_I18N) {
        this.approve_name_I18N = approve_name_I18N;
    }

    public int getIs_addApprove() {
        return this.is_addApprove;
    }

    public void setIs_addApprove(int is_addApprove) {
        this.is_addApprove = is_addApprove;
    }

    public int getAddApprove_count() {
        return this.addApprove_count;
    }

    public void setAddApprove_count(int addApprove_count) {
        this.addApprove_count = addApprove_count;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getBlock_id() {
        return this.block_id;
    }

    public void setBlock_id(String block_id) {
        this.block_id = block_id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBlock_x() {
        return this.block_x;
    }

    public void setBlock_x(int block_x) {
        this.block_x = block_x;
    }

    public int getBlock_y() {
        return this.block_y;
    }

    public void setBlock_y(int block_y) {
        this.block_y = block_y;
    }

    public String getApprove_name() {
        return this.approve_name;
    }

    public void setApprove_name(String approve_name) {
        this.approve_name = approve_name;
    }

    public String getTask_msg() {
        return this.task_msg;
    }

    public void setTask_msg(String task_msg) {
        this.task_msg = task_msg;
    }

    public String getTurn_down_up() {
        return this.turn_down_up;
    }

    public void setTurn_down_up(String turn_down_up) {
        this.turn_down_up = turn_down_up;
    }

    public String getTurn_down_source() {
        return this.turn_down_source;
    }

    public void setTurn_down_source(String turn_down_source) {
        this.turn_down_source = turn_down_source;
    }

    public String getTime_limit() {
        return this.time_limit;
    }

    public void setTime_limit(String time_limit) {
        this.time_limit = time_limit;
    }

    public String getTime_out_rule() {
        return this.time_out_rule;
    }

    public void setTime_out_rule(String time_out_rule) {
        this.time_out_rule = time_out_rule;
    }

    public int getNeed_agree_count_people() {
        return this.need_agree_count_people;
    }

    public void setNeed_agree_count_people(int need_agree_count_people) {
        this.need_agree_count_people = need_agree_count_people;
    }

    public int getNeed_opinion_count_people() {
        return this.need_opinion_count_people;
    }

    public void setNeed_opinion_count_people(int need_opinion_count_people) {
        this.need_opinion_count_people = need_opinion_count_people;
    }

    public String getNotPassType() {
        return this.notPassType;
    }

    public void setNotPassType(String notPassType) {
        this.notPassType = notPassType;
    }

    public List<FlowBlockEditColumn> getFlowBlockEditColumns() {
        return this.flowBlockEditColumns;
    }

    public void setFlowBlockEditColumns(List<FlowBlockEditColumn> flowBlockEditColumns) {
        this.flowBlockEditColumns = flowBlockEditColumns;
    }

    public void addFlowBlockEditColumns(FlowBlockEditColumn flowBlockEditColumn) {
        if (null == this.flowBlockEditColumns)
            this.flowBlockEditColumns = new ArrayList<>();
        this.flowBlockEditColumns.add(flowBlockEditColumn);
    }
}
