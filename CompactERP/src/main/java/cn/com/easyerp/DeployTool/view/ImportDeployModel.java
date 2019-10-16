package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("importDeploy")
public class ImportDeployModel extends FormModelBase {
    private String table_id;
    private String batch_id;
    private String statement;
    private String memo;
    private String interceptor_service;
    private String service_param;
    private String module;
    private String column_mapper;
    private int is_insert;
    private int is_update;
    private String update_keywords;
    private String create_trigger;
    private String update_statement;

    public ImportDeployModel() {
        super(ActionType.batch, null);
    }

    protected ImportDeployModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public String getTitle() {
        return "importDeploy";
    }

    public String getStatement() {
        return this.statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getService_param() {
        return this.service_param;
    }

    public void setService_param(String service_param) {
        this.service_param = service_param;
    }

    public String getModule() {
        return this.module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getBatch_id() {
        return this.batch_id;
    }

    public void setBatch_id(String batch_id) {
        this.batch_id = batch_id;
    }

    public String getInterceptor_service() {
        return this.interceptor_service;
    }

    public void setInterceptor_service(String interceptor_service) {
        this.interceptor_service = interceptor_service;
    }

    public String getColumn_mapper() {
        return this.column_mapper;
    }

    public void setColumn_mapper(String column_mapper) {
        this.column_mapper = column_mapper;
    }

    public int getIs_insert() {
        return this.is_insert;
    }

    public void setIs_insert(int is_insert) {
        this.is_insert = is_insert;
    }

    public int getIs_update() {
        return this.is_update;
    }

    public void setIs_update(int is_update) {
        this.is_update = is_update;
    }

    public String getUpdate_keywords() {
        return this.update_keywords;
    }

    public void setUpdate_keywords(String update_keywords) {
        this.update_keywords = update_keywords;
    }

    public String getCreate_trigger() {
        return this.create_trigger;
    }

    public void setCreate_trigger(String create_trigger) {
        this.create_trigger = create_trigger;
    }

    public String getUpdate_statement() {
        return this.update_statement;
    }

    public void setUpdate_statement(String update_statement) {
        this.update_statement = update_statement;
    }
}
