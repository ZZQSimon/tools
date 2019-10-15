package cn.com.easyerp.tree;

import java.util.ArrayList;
import java.util.List;

import cn.com.easyerp.core.widget.grid.RecordModel;

public class TreeNodeModel {
    public class State {
        private boolean opened;
        private boolean selected;

        public boolean isOpened() {
            return this.opened;
        }

        public void setOpened(boolean opened) {
            this.opened = opened;
        }

        public boolean isSelected() {
            return this.selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }
    }

    private String id;
    private String parent;
    private String text;
    private RecordModel data;
    private int depth;
    private List<TreeNodeModel> children;
    private State state;

    public TreeNodeModel() {
    }

    public TreeNodeModel(String id, String parent, String text) {
        this.id = id;
        this.parent = parent;
        this.text = text;
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

    public RecordModel getData() {
        return this.data;
    }

    public void setData(RecordModel data) {
        this.data = data;
    }

    public int getDepth() {
        return this.depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<TreeNodeModel> getChildren() {
        return this.children;
    }

    public void setChildren(List<TreeNodeModel> children) {
        this.children = children;
    }

    public void addChildren(TreeNodeModel children) {
        if (this.children == null) {
            this.children = new ArrayList<>();
            this.children.add(children);
        } else {
            this.children.add(children);
        }
    }

    public State getState() {
        return this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void addSelected() {
        State selected = new State();
        selected.setOpened(true);
        selected.setSelected(true);
        this.state = selected;
    }
}
