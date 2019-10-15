package cn.com.easyerp.core.cache;

import com.fasterxml.jackson.core.type.TypeReference;

public class FormatDesc {
    public static final TypeReference<FormatDesc> jsonRef = new TypeReference<FormatDesc>() {
    };
    private Type type;
    private String format;

    public enum Type {
        date;
    }

    static {
        // Byte code:
        // 0: new com/gainit/g1/core/cache/FormatDesc$1
        // 3: dup
        // 4: invokespecial <init> : ()V
        // 7: putstatic com/gainit/g1/core/cache/FormatDesc.jsonRef :
        // Lcom/fasterxml/jackson/core/type/TypeReference;
        // 10: return
        // Line number table:
        // Java source line number -> byte code offset
        // #13 -> 0
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
