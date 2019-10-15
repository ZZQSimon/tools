package cn.com.easyerp.framework.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: DataStatus <br>
 * @Description: [数据状态] <br>
 * @date 2018.11.05 <br>
 * 
 * @author Simon Zhang
 */
public enum DataType {
    string("string", Integer.valueOf(1)),

    digits("digits", Integer.valueOf(2)),

    number("number", Integer.valueOf(3)),

    date("date", Integer.valueOf(4)),

    booleaN("boolean", Integer.valueOf(5)),

    password("password", Integer.valueOf(6)),

    pic("pic", Integer.valueOf(7)),

    file("file", Integer.valueOf(8)),

    link("link", Integer.valueOf(9)),

    email("email", Integer.valueOf(10)),

    time("time", Integer.valueOf(11)),

    datetime("datetime", Integer.valueOf(12)),

    auto("auto", Integer.valueOf(13)),

    textArea("textArea", Integer.valueOf(14)),

    editorInput("editorInput", Integer.valueOf(15)),

    color("color", Integer.valueOf(16)),

    ;

    private final String key;
    private final Integer code;

    DataType(String key, Integer code) {
        this.key = key;
        this.code = code;
    }

    public String getKey() {
        return key;
    }

    public Integer getCode() {
        return code;
    }

    public static Map<String, Integer> dataTypeMap() {
        Map<String, Integer> dataType = new HashMap<>();
        for (DataType t : DataType.values()) {
            dataType.put(t.getKey(), t.getCode());
        }
        return dataType;
    }
}
