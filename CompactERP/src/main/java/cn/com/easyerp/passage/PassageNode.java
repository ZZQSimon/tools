package cn.com.easyerp.passage;

public class PassageNode {
    private String id;
    private String detailId;
    private String date;
    private Object number;

    public PassageNode() {
    }

    public PassageNode(String id, String detailId, String date, Object number) {
        this.id = id;
        this.detailId = detailId;
        this.date = date;
        this.number = number;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetailId() {
        return this.detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object getNumber() {
        return this.number;
    }

    public void setNumber(Object number) {
        this.number = number;
    }
}
