package cn.com.easyerp.framework.exception;

import cn.com.easyerp.framework.enums.Result;

/**
 * @ClassName: EDIException <br>
 * @Description: [] <br>
 * @date 2018.11.05 <br>
 * 
 * @author Simon Zhang
 */
public class EDIException extends RuntimeException {
    private static final long serialVersionUID = -8387120784671848915L;

    private Result errorCode;

    public EDIException(String message, Throwable cause) {
        super(message, cause);
    }

    public EDIException(String message) {
        super(message);
    }

    public EDIException(Result errorCode) {
        this(errorCode, null);
    }

    public EDIException(Result errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public Result getErrorCode() {
        return this.errorCode;
    }

}
