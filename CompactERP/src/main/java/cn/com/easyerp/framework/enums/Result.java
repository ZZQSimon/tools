package cn.com.easyerp.framework.enums;

/**
 * @ClassName: ErrorCode <br>
 * @Description: [] <br>
 * @date 2018.11.05 <br>
 * 
 * @author Simon Zhang
 */
public enum Result {

    /* Common Exception (0~100) */
    /** 0 成功 */
    ok(0, "success"),
    /** 1 失败 */
    fail(1, "failed"),
    /** 2 运行异常 */
    error(2, "exception"),
    /** 3 未知业务 */
    UNKNOWN(3, "unknown"),

    /* API Exception (101~200) */

    /* Download Exception (201~300) */

    /* Transfer Exception (301~400) */

    /* Upload Exception (401~500) */

    /* Database Exception (501~500) */

    ;

    private final Integer code;
    private final String message;

    Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static Result parseCode(int code) {
        for (Result resultCode : Result.values()) {
            if (resultCode.code == code) {
                return resultCode;
            }
        }
        return null;
    }

}
