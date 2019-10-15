package cn.com.easyerp.core.cache;

public class ComplexColumnMapModel {
    private String id;
    private String complex_id;
    private String detail_col;
    private String base_col;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComplex_id() {
        return this.complex_id;
    }

    public void setComplex_id(String complex_id) {
        this.complex_id = complex_id;
    }

    public String getDetail_col() {
        return this.detail_col;
    }

    public void setDetail_col(String detail_col) {
        this.detail_col = detail_col;
    }

    public String getBase_col() {
        return this.base_col;
    }

    public void setBase_col(String base_col) {
        this.base_col = base_col;
    }
}
