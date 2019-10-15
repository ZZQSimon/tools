package cn.com.easyerp.core.widget.inputSelect;

import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.WidgetRequestModelBase;

public class TreeSelectRequestModel extends WidgetRequestModelBase<FieldWithRefModel> {
    static enum TreeType {
        filter, edit;
    }

    private String node;
    private String search;
    private TreeType type;

    public String getNode() {
        return this.node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getSearch() {
        return this.search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public TreeType getType() {
        return this.type;
    }

    public void setType(TreeType type) {
        this.type = type;
    }
}
