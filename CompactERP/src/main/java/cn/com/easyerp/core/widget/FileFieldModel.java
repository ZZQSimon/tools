package cn.com.easyerp.core.widget;

public class FileFieldModel extends FieldModelBase {
    private String uuid;

    public FileFieldModel() {
    }

    public FileFieldModel(String table, String column, Object value) {
        super(table, column, value);
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
