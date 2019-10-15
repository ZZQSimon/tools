package cn.com.easyerp.core.cache;

public class I18nDescribe {
    private int type;
    private String international_id;
    private String key1;
    private String key2;
    private String cn;
    private String en;
    private String jp;
    private String other1;
    private String other2;
    private int value;
    private int hidden;
    private int seq;

    public String getInternational_id() {
        return this.international_id;
    }

    public void setInternational_id(String international_id) {
        this.international_id = international_id;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKey1() {
        return this.key1;
    }

    public void setKey1(String key1) {
        this.key1 = key1;
    }

    public String getKey2() {
        return this.key2;
    }

    public void setKey2(String key2) {
        this.key2 = key2;
    }

    public String getCn() {
        return this.cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getEn() {
        return this.en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getJp() {
        return this.jp;
    }

    public void setJp(String jp) {
        this.jp = jp;
    }

    public String getOther1() {
        return this.other1;
    }

    public void setOther1(String other1) {
        this.other1 = other1;
    }

    public String getOther2() {
        return this.other2;
    }

    public void setOther2(String other2) {
        this.other2 = other2;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getHidden() {
        return this.hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}
