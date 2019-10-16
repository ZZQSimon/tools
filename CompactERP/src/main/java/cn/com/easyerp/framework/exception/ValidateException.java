package cn.com.easyerp.framework.exception;

public class ValidateException extends RuntimeException {
    private static final long serialVersionUID = 7199447874577280121L;

    private String id;

    public ValidateException(String id, Throwable cause) {
        super(cause);
        this.id = id;
    }

    public ValidateException(String id, String message) {
        super(message);
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
