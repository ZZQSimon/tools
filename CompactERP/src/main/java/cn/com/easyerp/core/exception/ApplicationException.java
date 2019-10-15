package cn.com.easyerp.core.exception;

public class ApplicationException extends RuntimeException {
    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(String message) {
        super(message);
    }
}
