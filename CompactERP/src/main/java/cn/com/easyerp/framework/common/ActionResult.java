package cn.com.easyerp.framework.common;

public class ActionResult {
    private boolean success;
    private Object data;

    public ActionResult(boolean success) {
        this(success, null);
    }

    public ActionResult(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getErrorMessage() {
        return this.success ? null : ((this.data == null) ? null : this.data.toString());
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
