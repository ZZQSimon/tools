package cn.com.easyerp.DeployTool.service;

public class TreeNode {
    private String id;
    private String table_id;
    private String parent;
    private String text;
    private Object data;

    public TreeNode() {
    }

    public TreeNode(String id, String table_id, String parentId, String text) {
        this.id = id;
        this.table_id = table_id;
        this.parent = parentId;
        this.text = text;
    }

    public TreeNode(String id, String parent, String text, Object data, String table_id) {
        this.id = id;
        this.parent = parent;
        this.text = text;
        this.data = data;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return this.parent;
    }

    public void setParent(String parentId) {
        this.parent = parentId;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTable_id() {
        return this.table_id;
    }

    public void setTable_id(String table_id) {
        this.table_id = table_id;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
