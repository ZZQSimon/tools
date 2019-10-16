package cn.com.easyerp.core.widget.message;

public class TimmingMessageModel {
    public static int NOT_PERFOM = 0;
    public static int PERFOMED = 1;
    public static int FAILED = 0;
    public static int SUCCEED = 1;

    private String id;

    private String template_id;

    private String param;
    private String receiver;
    private int status;
    private int result;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getParam() {
        return this.param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
