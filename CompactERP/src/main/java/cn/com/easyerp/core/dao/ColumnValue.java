package cn.com.easyerp.core.dao;

public class ColumnValue {
    private String id;
    private Object value;
    private int is_encrypt;
    private int data_type;

    public ColumnValue(String id, Object value) {
        this.id = id;
        this.value = value;
    }

    public ColumnValue(String id, Object value, int is_encrypt, int data_type) {
        this.id = id;
        this.value = value;
        this.is_encrypt = is_encrypt;
        this.data_type = data_type;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getIs_encrypt() {
        return this.is_encrypt;
    }

    public void setIs_encrypt(int is_encrypt) {
        this.is_encrypt = is_encrypt;
    }

    public int getData_type() {
        return this.data_type;
    }

    public void setData_type(int data_type) {
        this.data_type = data_type;
    }
}
