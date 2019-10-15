package cn.com.easyerp.core.authGroup;

public class State {
    public State(boolean selected) {
        this.selected = selected;
    }

    private boolean selected;

    public boolean getSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
