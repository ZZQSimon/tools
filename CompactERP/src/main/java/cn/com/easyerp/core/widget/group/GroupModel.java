package cn.com.easyerp.core.widget.group;

import java.util.ArrayList;

import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.InputEntry;

public class GroupModel extends FieldModelBase {
    private ArrayList<InputEntry> entries;

    public GroupModel(String table, String column, Object value, ArrayList<InputEntry> entries) {
        super(table, column, value);
        this.entries = entries;
    }

    public ArrayList<InputEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(ArrayList<InputEntry> entries) {
        this.entries = entries;
    }
}
