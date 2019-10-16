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
