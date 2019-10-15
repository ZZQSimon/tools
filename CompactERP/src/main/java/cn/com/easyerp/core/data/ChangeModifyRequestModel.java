package cn.com.easyerp.core.data;

public class ChangeModifyRequestModel {
    private String id;
    private String column;
    private Object value;
    private String currentBlock;
    private boolean evalReadonly;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getCurrentBlock() {
        return this.currentBlock;
    }

    public void setCurrentBlock(String currentBlock) {
        this.currentBlock = currentBlock;
    }

    public boolean isEvalReadonly() {
        return this.evalReadonly;
    }

    public void setEvalReadonly(boolean evalReadonly) {
        this.evalReadonly = evalReadonly;
    }
}
