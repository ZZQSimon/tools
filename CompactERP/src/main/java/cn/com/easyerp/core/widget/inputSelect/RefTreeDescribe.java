package cn.com.easyerp.core.widget.inputSelect;

import com.fasterxml.jackson.core.type.TypeReference;

public class RefTreeDescribe {
    public static TypeReference<RefTreeDescribe> jsonRef;
    private String root;
    private String valid;

    static {
        // Byte code:
        // 0: new com/gainit/g1/core/widget/inputSelect/RefTreeDescribe$1
        // 3: dup
        // 4: invokespecial <init> : ()V
        // 7: putstatic com/gainit/g1/core/widget/inputSelect/RefTreeDescribe.jsonRef :
        // Lcom/fasterxml/jackson/core/type/TypeReference;
        // 10: return
        // Line number table:
        // Java source line number -> byte code offset
        // #13 -> 0
    }

    public String getRoot() {
        return this.root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getValid() {
        return this.valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }
}
