package cn.com.easyerp.core.api;

public class ApiLog {
    private String id;
    private String proc_name;
    private int result;
    private String msg_id;
    private String msg_param;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProc_name() {
        return this.proc_name;
    }

    public void setProc_name(String proc_name) {
        this.proc_name = proc_name;
    }

    public int getResult() {
        return this.result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg_id() {
        return this.msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public String getMsg_param() {
        return this.msg_param;
    }

    public void setMsg_param(String msg_param) {
        this.msg_param = msg_param;
    }
}
