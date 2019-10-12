package cn.com.easyerp.framework.enums;

/**
 * @ClassName: DataStatus <br>
 * @Description: [数据状态] <br>
 * @date 2018.11.05 <br>
 * 
 * @author Simon Zhang
 */
public enum DataStatus {

    /** 0 ：成功 */
    inuse(0, "0"),

    /** 1： 失败 */
    deleted(1, "1"),

    /** 2 ：已发送 */
    sent(2, "2"),

    /** 99 未知 */
    UNKNOWN(99, "99"),

    ;

    private final Integer code;
    private final String message;

    DataStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String code() {
        return String.valueOf(code);
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static DataStatus parseCode(int code) {
        for (DataStatus resultCode : DataStatus.values()) {
            if (resultCode.code == code) {
                return resultCode;
            }
        }
        return null;
    }

}
