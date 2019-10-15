package cn.com.easyerp.core.authGroup;

public class MenuTree {
    private String id;
    private String parent;

    public State getState() {
        return this.state;
    }

    private String text;
    private State state;

    public void setState(State state) {
        this.state = state;
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

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
