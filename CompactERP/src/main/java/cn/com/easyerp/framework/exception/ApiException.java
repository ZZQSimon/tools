package cn.com.easyerp.framework.exception;

import cn.com.easyerp.framework.enums.Result;

/**
 * Used for exception when API is called. </br>
 * It'll return a meaningful message to client.
 * 
 * @ClassName: ApiException <br>
 * @Description: [] <br>
 * @date 2018.11.05 <br>
 * 
 * @author Simon Zhang
 */
public class ApiException extends EDIException {

    private static final long serialVersionUID = -3498981481925565359L;

    public ApiException(Result errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public ApiException(Result errorCode) {
        this(errorCode, null);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message) {
        super(message);
    }

}
