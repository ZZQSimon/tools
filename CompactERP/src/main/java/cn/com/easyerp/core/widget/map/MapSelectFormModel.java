package cn.com.easyerp.core.widget.map;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("map_select")
public class MapSelectFormModel extends FormModelBase {
    private LinkedHashSet<String> selection;
    private LinkedHashMap<String, String> all;
    private String idText;
    private String nameText;

    MapSelectFormModel(String parent, LinkedHashSet<String> selection, LinkedHashMap<String, String> all, String idText,
            String nameText) {
        super(ActionType.map, parent);
        this.selection = selection;
        this.all = all;
        this.idText = idText;
        this.nameText = nameText;
    }

    public LinkedHashSet<String> getSelection() {
        return this.selection;
    }

    public LinkedHashMap<String, String> getAll() {
        return this.all;
    }

    public String getIdText() {
        return this.idText;
    }

    public String getNameText() {
        return this.nameText;
    }

    public String getTitle() {
        return "Map";
    }
}
