package cn.com.easyerp.framework.exception;

import cn.com.easyerp.framework.enums.Result;

/**
 * Used for exception when system application is called. </br>
 * It'll return a meaningful message to client.
 * 
 * @ClassName: ApplicationException <br>
 * @Description: [] <br>
 * @date 2018.11.05 <br>
 * 
 * @author Simon Zhang
 */
public class ApplicationException extends EDIException {

    private static final long serialVersionUID = -100000000000002L;

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(Result errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ApplicationException(Result errorCode) {
        this(errorCode, null);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message) {
        super(message);
    }

}
