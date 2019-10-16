package cn.com.easyerp.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.framework.common.Common;

public class AuthControlDescribe {
    private static TypeReference<List<AuthEntryModel>> authTargetJsonRef;
    private String id;
    private String relationship;
    private List<AuthEntryModel> entries;

    static {
        // Byte code:
        // 0: new com/gainit/g1/auth/AuthControlDescribe$1
        // 3: dup
        // 4: invokespecial <init> : ()V
        // 7: putstatic com/gainit/g1/auth/AuthControlDescribe.authTargetJsonRef :
        // Lcom/fasterxml/jackson/core/type/TypeReference;
        // 10: return
        // Line number table:
        // Java source line number -> byte code offset
        // #16 -> 0
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getRelationship() {
        return this.relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
        parseEntries();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void parseEntries() {
        if (!Common.isBlank(this.relationship)) {
            this.entries = (List) Common.fromJson(this.relationship, authTargetJsonRef);
        }
    }

    public void setEntries(List<AuthEntryModel> entries) {
        this.entries = entries;
    }

    public List<AuthEntryModel> getEntries() {
        return this.entries;
    }
}
