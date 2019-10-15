package cn.com.easyerp.framework.common;

public class ActionErrorModel extends ActionResult {
    private String errorMessage;

    public ActionErrorModel(String errorMessage, Exception exception) {
        super(false);
        this.errorMessage = errorMessage;
    }

    public ActionErrorModel(String errorMessage) {
        this(errorMessage, null);
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
