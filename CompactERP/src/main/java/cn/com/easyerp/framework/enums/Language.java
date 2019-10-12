package cn.com.easyerp.framework.enums;

/**
 * 系统支持的语言枚举
 * 
 * @author Simon
 *
 */
public enum Language {
    Chinese("zh_CN", "中文", "Chinese"),

    English("en_US", "英文", "English"),

    Japanese("ja_JP", "日本语", "Japanese");

    private String id;// 中文名称
    private String cname;// 中文名称
    private String ename;// 英文名称

    Language(String id, String cname, String ename) {
        this.id = id;
        this.cname = cname;
        this.ename = ename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getEname() {
        return ename;
    }

    public void setEname(String ename) {
        this.ename = ename;
    }

    public static Language byId(String id) {
        for (Language enu : values()) {
            if (enu.getId().equals(id)) {
                return enu;
            }
        }
        return null;
    }

    public static Language byName(String ename) {
        for (Language enu : values()) {
            if (enu.getEname().equals(ename)) {
                return enu;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
