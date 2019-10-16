package cn.com.easyerp.DeployTool.service;

public class ABatch {
    private String batch_id;
    private String statement;
    private String memo;

    public String getBatch_id() {
        return this.batch_id;
    }

    private String interceptor_service;
    private String service_param;
    private String module;

    public void setBatch_id(String batch_id) {
        this.batch_id = batch_id;
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

    public String getInterceptor_service() {
        return this.interceptor_service;
    }

    public void setInterceptor_service(String interceptor_service) {
        this.interceptor_service = interceptor_service;
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
}
